package com.example.jbvideochat.ui.videochat

//Imports from Agora documentation
import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.example.jbvideochat.databinding.FragmentVideoChatBinding
import com.example.jbvideochat.ui.BindingFragment
import com.example.jbvideochat.util.Constants
import dagger.hilt.android.AndroidEntryPoint
import io.agora.rtc.IRtcEngineEventHandler
import io.agora.rtc.RtcEngine
import io.agora.rtc.video.VideoCanvas
import kotlinx.coroutines.launch

@AndroidEntryPoint
class VideoChatFragment : BindingFragment<FragmentVideoChatBinding>()  {


    private val PERMISSION_REQ_ID_RECORD_AUDIO = 22
    private val PERMISSION_REQ_ID_CAMERA = 23

    // Fill the channel name.
    private val CHANNEL = "JBVideoChatChannel"
    // Fill the temp token generated on Agora Console.
    private val TOKEN = "00629a794ea6ffd4eff913b98c00fdb9546IABoZPgzP8MM274tY9ejKZ6RjbO6zSzoSUuiptR7l7c+ytm/NisAAAAAEAAirequ80e8YQEAAQDxR7xh"

    private val viewmodel: VideoChatViewModel by viewModels()



    private var mRtcEngine: RtcEngine ?= null



    private val mRtcEventHandler = object : IRtcEngineEventHandler() {
        // Listen for the remote user joining the channel to get the uid of the user.
        override fun onUserJoined(uid: Int, elapsed: Int) {
            lifecycleScope.launch {
                // Call setupRemoteVideo to set the remote video view after getting uid from the onUserJoined callback.
                setupRemoteVideo(uid)
            }
        }
    }




    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        if (checkPermission(Manifest.permission.RECORD_AUDIO, PERMISSION_REQ_ID_RECORD_AUDIO)
            && checkPermission(Manifest.permission.CAMERA, PERMISSION_REQ_ID_CAMERA)) {
            Toast.makeText(context, "Audio and Video permission's are granted", Toast.LENGTH_SHORT).show()
            initializeAndJoinChannel()
        }
    }

    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentVideoChatBinding::inflate


    private fun checkPermission(permission: String, requestCode: Int): Boolean {
        if (ContextCompat.checkSelfPermission(requireContext(), permission) !=
            PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                arrayOf(permission),
                requestCode)

            return false
        }
        return true
    }

    fun initializeAndJoinChannel () {

        try {
            mRtcEngine = RtcEngine.create(context, Constants.APP_ID, mRtcEventHandler)
        } catch (e: Exception) {

        }

        mRtcEngine?.enableVideo()


        // Call CreateRendererView to create a SurfaceView object and add it as a child to the FrameLayout.
        val localFrame = RtcEngine.CreateRendererView(requireContext())
        binding.localVideoViewContainer.addView(localFrame)

        // Pass the SurfaceView object to Agora so that it renders the local video.
        mRtcEngine?.setupLocalVideo(VideoCanvas(localFrame, VideoCanvas.RENDER_MODE_FIT, 0))

        // Join the channel with a token.
        mRtcEngine?.joinChannel(TOKEN, CHANNEL, "", 0)


    }

    private fun setupRemoteVideo (uid: Int) {

        val remoteFrame = RtcEngine.CreateRendererView(requireContext())
        remoteFrame.setZOrderMediaOverlay(true)
        binding.remoteVideoViewContainer.addView(remoteFrame)
        mRtcEngine?.setupRemoteVideo(VideoCanvas(remoteFrame, VideoCanvas.RENDER_MODE_FIT, uid))

    }


    override fun onDestroyView() {
        super.onDestroyView()

        mRtcEngine?.leaveChannel()
        RtcEngine.destroy()



    }

}