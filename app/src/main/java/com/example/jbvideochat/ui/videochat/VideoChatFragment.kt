package com.example.jbvideochat.ui.videochat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.example.jbvideochat.databinding.FragmentVideoChatBinding
import com.example.jbvideochat.ui.BindingFragment

//Imports from Agora documentation
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Camera
import android.hardware.Camera.open
import android.view.SurfaceView;
import android.widget.FrameLayout;
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import io.agora.rtc.RtcEngine
import io.agora.rtc.video.VideoCanvas
import java.lang.RuntimeException
import javax.inject.Inject

@AndroidEntryPoint
class VideoChatFragment : BindingFragment<FragmentVideoChatBinding>() {

    private val PERMISSION_REQ_ID_RECORD_AUDIO = 22
    private val PERMISSION_REQ_ID_CAMERA = 23

    // Fill the channel name.
    private val CHANNEL = "JBVideoChatChannel"
    // Fill the temp token generated on Agora Console.
    private val TOKEN = "00629a794ea6ffd4eff913b98c00fdb9546IABd0xmLG02uWuOXWLcqR9Stq6ulg29XrSsqNHrVM8O6Ntm/NisAAAAAEABgg+xaHCSpYQEAAQAcJKlh"

    private val viewmodel: VideoChatViewModel by viewModels()

    @Inject
    lateinit var mRtcEngine: RtcEngine

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        if (checkPermission(Manifest.permission.RECORD_AUDIO, PERMISSION_REQ_ID_RECORD_AUDIO)
            && checkPermission(Manifest.permission.CAMERA, PERMISSION_REQ_ID_CAMERA)) {
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

        mRtcEngine.enableVideo()

        // Call CreateRendererView to create a SurfaceView object and add it as a child to the FrameLayout.
        val localFrame = RtcEngine.CreateRendererView(requireContext())
        binding.localVideoViewContainer.addView(localFrame)

        // Pass the SurfaceView object to Agora so that it renders the local video.
        mRtcEngine!!.setupLocalVideo(VideoCanvas(localFrame, VideoCanvas.RENDER_MODE_FIT, 0))

        // Join the channel with a token.
        mRtcEngine!!.joinChannel(TOKEN, CHANNEL, "", 0)


    }

    private fun setupRemoteVideo (uid: Int) {

        val remoteFrame = RtcEngine.CreateRendererView(requireContext())
        remoteFrame.setZOrderMediaOverlay(true)
        binding.remoteVideoViewContainer.addView(remoteFrame)
        mRtcEngine.setupRemoteVideo(VideoCanvas(remoteFrame, VideoCanvas.RENDER_MODE_FIT, uid))

    }


    override fun onDestroyView() {
        super.onDestroyView()

        mRtcEngine.leaveChannel()
        RtcEngine.destroy()



    }

}