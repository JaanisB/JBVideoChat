package com.example.jbvideochat.di.videoImplClasses

import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import io.agora.rtc.IRtcEngineEventHandler
import kotlinx.coroutines.launch


class IRtcEngineEventHandlerImpl: IRtcEngineEventHandler() {

    var onJoinChannelSuccessCallback: ((channel: String?, uid: Int, elapsed: Int) -> Unit)? = null
    var onFirstRemoteVideoDecodedCallback: ((uid: Int, width: Int, height: Int, elapsed: Int) -> Unit)? = null
    var onUserOfflineCallback: ((uid: Int, reason: Int) -> Unit)? = null

    override fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {
        onJoinChannelSuccessCallback?.invoke(channel, uid, elapsed)
    }

    override fun onFirstRemoteVideoDecoded(uid: Int, width: Int, height: Int, elapsed: Int) {
        onFirstRemoteVideoDecodedCallback?.invoke(uid, width, height, elapsed)
    }

    override fun onUserOffline(uid: Int, reason: Int) {
        onUserOfflineCallback?.invoke(uid, reason)
    }

}