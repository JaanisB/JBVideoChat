package com.example.jbvideochat.di

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.agora.rtm.*


class RtmClientListnerImpl: RtmClientListener {


    private val _actualMessage = MutableLiveData<String>()
    val actualMessage: LiveData<String>
        get() = _actualMessage

    private val _connectionState = MutableLiveData<String>()
    val connectionState: LiveData<String>
        get() = _connectionState

    override fun onConnectionStateChanged(state: Int, reason: Int) {
        val text =
            """Connection state changed to ${state}Reason: $reason """.trimIndent()
        _connectionState.value = text
    }

    override fun onMessageReceived(rtmMessage: RtmMessage?, peerId: String?) {

        val text = """Message received from $peerId Message: ${rtmMessage?.text}"""

        _actualMessage.value = text
    }

    override fun onImageMessageReceivedFromPeer(p0: RtmImageMessage?, p1: String?) {
        TODO("Not yet implemented")
    }

    override fun onFileMessageReceivedFromPeer(p0: RtmFileMessage?, p1: String?) {
        TODO("Not yet implemented")
    }

    override fun onMediaUploadingProgress(p0: RtmMediaOperationProgress?, p1: Long) {
        TODO("Not yet implemented")
    }

    override fun onMediaDownloadingProgress(p0: RtmMediaOperationProgress?, p1: Long) {
        TODO("Not yet implemented")
    }

    override fun onTokenExpired() {
        TODO("Not yet implemented")
    }

    override fun onPeersOnlineStatusChanged(p0: MutableMap<String, Int>?) {
        TODO("Not yet implemented")
    }
}