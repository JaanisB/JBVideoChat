package com.example.jbvideochat.ui.video

//Imports from Agora documentation
import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.SurfaceView
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.example.jbvideochat.R
import com.example.jbvideochat.databinding.FragmentVideoChatBinding
import com.example.jbvideochat.ui.BindingFragment
import dagger.hilt.android.AndroidEntryPoint
import io.agora.rtc.RtcEngine
import io.agora.rtc.video.VideoCanvas
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class VideoFragment : BindingFragment<FragmentVideoChatBinding>() {

    @Inject
    lateinit var rtcEngine: RtcEngine

    private val viewmodel: VideoViewModel by viewModels()

    // State of call
    private var mEndCall = false

    // State of "mute"
    private var mMuted = false

    // View for remote view
    private var remoteView: SurfaceView? = null


    // View for local view
    private var localView: SurfaceView? = null


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

                } else {
                    grantPermission(permissionName)
                }
            }

            val isAllPermissionsGranted = permissions.values.all { isGranted ->
                isGranted == true
            }

            if (isAllPermissionsGranted) {
                viewmodel.permissionGranted()
            }
        }

    private fun grantPermission(permission: String) {
        activityResultLauncher.launch(
            arrayOf(permission)
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        activityResultLauncher.launch(
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.INTERNET
            )
        )


        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            delay(300L)
            if (viewmodel.permissionState.value) {
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
            viewmodel.switchCamera()
        }

        binding.buttonMute.setOnClickListener {
            viewmodel.muteCall()
            val res: Int = if (viewmodel.mMuted) {
                R.drawable.ic_baseline_mic_off_24
            } else {
                R.drawable.ic_baseline_mic_24
            }
            binding.buttonMute.setImageResource(res)
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        viewmodel.leaveChannel()
//        RtcEngine.destroy()
    }


    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentVideoChatBinding::inflate


    private fun initializeAndJoinChannel() {

        // This is our usual steps for joining
        // a channel and starting a call.
        viewmodel.setupVideoConfig()
        setupLocalVideoView()
        viewmodel.joinChannel()
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


    private fun removeLocalVideo() {
        binding.remoteVideoView.removeView(remoteView)
    }

    private fun removeRemoteVideo() {
        if (remoteView != null) {
            binding.remoteVideoView.removeView(remoteView)
        }
        remoteView = null
    }


    private fun startCall() {
        setupLocalVideoView()
        viewmodel.joinChannel()
    }

    private fun endCall() {
        removeLocalVideo()
        removeRemoteVideo()
        viewmodel.leaveChannel()
    }


}