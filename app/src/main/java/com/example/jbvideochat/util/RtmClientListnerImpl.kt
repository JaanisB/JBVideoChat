package com.example.jbvideochat.util

import androidx.lifecycle.MutableLiveData
import io.agora.rtm.*
import javax.inject.Inject


class RtmClientListnerImpl: RtmClientListener {


    val actualMessage = MutableLiveData<String>()

    val connectionState = MutableLiveData<String>()

    override fun onConnectionStateChanged(state: Int, reason: Int) {
        val text =
            """Connection state changed to ${state}Reason: $reason """.trimIndent()
        connectionState.value = text
    }

    override fun onMessageReceived(rtmMessage: RtmMessage?, peerId: String?) {

        val text = """Message received from $peerId Message: ${rtmMessage?.text}"""

        actualMessage.value = text
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