package com.example.jbvideochat.ui.video

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.jbvideochat.di.videoImplClasses.IRtcEngineEventHandlerImpl
import com.example.jbvideochat.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import io.agora.rtc.RtcEngine
import io.agora.rtc.video.VideoEncoderConfiguration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class VideoViewModel @Inject constructor(
    private val state: SavedStateHandle,
    private val mRtcEventHandler: IRtcEngineEventHandlerImpl,
    private val rtcEngine: RtcEngine

) : ViewModel() {

/*
    // State class to handle permission state
    sealed class PermissionState {
        object Granted : PermissionState()
        object Rejected : PermissionState()
        object Nothing : PermissionState()
    }
*/



    // Get selected user data from savedStateHandle, which holds navArgs data by argument name "username"
    private val _username = MutableLiveData(state.get<String>("username_v")!!)
    val username: LiveData<String>
        get() = _username

    // Get selected user data from savedStateHandle, which holds navArgs data by argument name "channelname"
    private val _channelname = MutableLiveData(state.get<String>("channel_name_v")!!)
    val channelname: LiveData<String>
        get() = _channelname

    private val _permissionState = MutableStateFlow<Boolean>(false)
    val permissionState: StateFlow<Boolean>
        get() = _permissionState



    // State of "mute"
    var mMuted = false


    fun setupVideoConfig() {
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

    fun joinChannel() {
        rtcEngine.joinChannel(Constants.TOKEN, Constants.CHANNEL, "", 0)

        // Define callback fun
    }

    fun leaveChannel() {
        rtcEngine.leaveChannel()
    }

    fun switchCamera() {
        rtcEngine.switchCamera()
    }

    fun muteCall () {
        mMuted = !mMuted
        rtcEngine.muteLocalAudioStream(mMuted)
    }

    fun permissionGranted() {
        _permissionState.value = true
    }


}

