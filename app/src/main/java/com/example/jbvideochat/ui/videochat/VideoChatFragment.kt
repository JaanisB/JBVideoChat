package com.example.jbvideochat.ui.videochat

//Imports from Agora documentation
import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.SurfaceView
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.example.jbvideochat.R
import com.example.jbvideochat.databinding.FragmentVideoChatBinding
import com.example.jbvideochat.ui.BindingFragment
import com.example.jbvideochat.util.Constants
import com.example.jbvideochat.util.Constants.CHANNEL
import com.example.jbvideochat.util.Constants.TOKEN
import dagger.hilt.android.AndroidEntryPoint
import io.agora.rtc.IRtcEngineEventHandler
import io.agora.rtc.RtcEngine
import io.agora.rtc.video.VideoCanvas
import io.agora.rtc.video.VideoEncoderConfiguration
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class VideoChatFragment : BindingFragment<FragmentVideoChatBinding>() {


    private val viewmodel: VideoChatViewModel by viewModels()

    // State of call
    private var mEndCall = false

    // State of "mute"
    private var mMuted = false

    // View for remote view
    private var remoteView: SurfaceView? = null

    // View for local view
    private var localView: SurfaceView? = null

    // Agora rtcEngine
    private lateinit var rtcEngine: RtcEngine

    // Initialize mRtcEventHandler and override callback methods
    private val mRtcEventHandler = object : IRtcEngineEventHandler() {
        // Listen for the remote user joining the channel to get the uid of the user.
        override fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {
            lifecycleScope.launch {
                Toast.makeText(requireContext(), "Joined Channel Successfully", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        override fun onFirstRemoteVideoDecoded(uid: Int, width: Int, height: Int, elapsed: Int) {
            lifecycleScope.launch {
                setupRemoteVideoView(uid)
            }
        }

        override fun onUserOffline(uid: Int, reason: Int) {
            lifecycleScope.launch {
                removeRemoteVideo()
            }
        }
    }

    private val isAllPermissionsGranted = mutableListOf<Int>()

    private val activityResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        )
        { permissions ->
            // Handle Permission granted/rejected

            permissions.entries.forEach {
                val permissionName = it.key
                val isGranted = it.value
                if (isGranted) {
                    isAllPermissionsGranted.add(1)
                } else {
                    isAllPermissionsGranted.add(0)
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        activityResultLauncher.launch(
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            )
        )

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {

            delay(300L)
            if (checkIfAllPermissionsAllowed(isAllPermissionsGranted)) {
                initializeAndJoinChannel()
            }
        }

        binding.buttonCall.setOnClickListener {
            if (mEndCall) {
                startCall()
                mEndCall = false
                binding.buttonCall.setImageResource(R.drawable.ic_baseline_call_end_24)
                binding.buttonMute.visibility = VISIBLE
                binding.buttonSwitchCamera.visibility = VISIBLE
            } else {
                endCall()
                mEndCall = true
                binding.buttonCall.setImageResource(R.drawable.ic_baseline_call_24)
                binding.buttonMute.visibility = INVISIBLE
                binding.buttonSwitchCamera.visibility = INVISIBLE
            }
        }

        binding.buttonSwitchCamera.setOnClickListener {
            rtcEngine.switchCamera()
        }

        binding.buttonMute.setOnClickListener {
            mMuted = !mMuted
            rtcEngine.muteLocalAudioStream(mMuted)
            val res: Int = if (mMuted) {
                R.drawable.ic_baseline_mic_off_24
            } else {
                R.drawable.ic_baseline_mic_24
            }

            binding.buttonMute.setImageResource(res)
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        RtcEngine.destroy()
    }

    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentVideoChatBinding::inflate

    fun checkIfAllPermissionsAllowed(list: List<Int>): Boolean {
        var result = 0
        list.forEach {
            result += it
        }
        return result == list.size
    }

    private fun initializeAndJoinChannel() {

        // This is our usual steps for joining
        // a channel and starting a call.
        initRtcEngine()
        setupVideoConfig()
        setupLocalVideoView()
        joinChannel()
    }

    private fun setupLocalVideoView() {

        localView = RtcEngine.CreateRendererView(requireContext())
        localView!!.setZOrderMediaOverlay(true)
        binding.localVideoView.addView(localView)

        // Set the local video view.
        rtcEngine.setupLocalVideo(VideoCanvas(localView, VideoCanvas.RENDER_MODE_HIDDEN, 0))
    }

    private fun setupRemoteVideoView(uid: Int) {

        if (binding.remoteVideoView.childCount > 1) {
            return
        }
        remoteView = RtcEngine.CreateRendererView(requireContext())
        binding.remoteVideoView.addView(remoteView)

        rtcEngine.setupRemoteVideo(VideoCanvas(remoteView, VideoCanvas.RENDER_MODE_FILL, uid))
    }



    private fun initRtcEngine() {
        try {
            rtcEngine = RtcEngine.create(requireContext(), Constants.APP_ID, mRtcEventHandler)
        } catch (e: Exception) {

        }
    }

    private fun setupVideoConfig() {
        rtcEngine.enableVideo()

        rtcEngine.setVideoEncoderConfiguration(
            VideoEncoderConfiguration(
                VideoEncoderConfiguration.VD_640x360,
                VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15,
                VideoEncoderConfiguration.STANDARD_BITRATE,
                VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT
            )
        )
    }

    private fun removeLocalVideo() {
        binding.remoteVideoView.removeView(remoteView)
    }

    private fun removeRemoteVideo() {
        if (remoteView != null) {
            binding.remoteVideoView.removeView(remoteView)
        }
        remoteView = null
    }


    private fun joinChannel() {
        rtcEngine.joinChannel(Constants.TOKEN, Constants.CHANNEL, "", 0)
    }

    private fun startCall() {
        setupLocalVideoView()
        joinChannel()
    }

    private fun endCall() {
        removeLocalVideo()
        removeRemoteVideo()
        leaveChannel()
    }

    private fun leaveChannel() {
        rtcEngine.leaveChannel()
    }




}