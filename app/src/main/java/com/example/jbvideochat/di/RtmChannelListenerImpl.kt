package com.example.jbvideochat.di

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.jbvideochat.model.Message
import com.example.jbvideochat.ui.chat.ChatViewModel
import io.agora.rtm.*
import kotlinx.coroutines.Job

class RtmChannelListenerImpl () : RtmChannelListener {


/*
    private val _receivedChannelMessage = MutableLiveData<Message>()
    val receivedChannelMessage: LiveData<Message>
        get() = _receivedChannelMessage
*/
    var myCallBackFun: ((fromUser: RtmChannelMember?, message: RtmMessage?) -> Unit)? = null


/*    private val _receivedChannelMessageList = MutableLiveData<List<Message>>()
    val receivedChannelMessageList: LiveData<List<Message>>
        get() = _receivedChannelMessageList*/

    override fun onMemberCountUpdated(p0: Int) {
        TODO("Not yet implemented")
    }

    override fun onAttributesUpdated(p0: MutableList<RtmChannelAttribute>?) {
        TODO("Not yet implemented")
    }

    override fun onMessageReceived(message: RtmMessage?, fromUser: RtmChannelMember?) {

        myCallBackFun?.invoke(fromUser, message)

/*        _receivedChannelMessage.value = Message(true, fromUser!!.userId, message!!.text)



        _receivedChannelMessageList.value = _receivedChannelMessageList.value?.plus(
            Message(
                true,
                fromUser!!.userId,
                message!!.text
            )
        ) ?: listOf(
            Message(
                true, fromUser!!.userId, message!!.text
            )
        )*/
    }

    override fun onImageMessageReceived(p0: RtmImageMessage?, p1: RtmChannelMember?) {
        TODO("Not yet implemented")
    }

    override fun onFileMessageReceived(p0: RtmFileMessage?, p1: RtmChannelMember?) {
        TODO("Not yet implemented")
    }

    override fun onMemberJoined(p0: RtmChannelMember?) {
        TODO("Not yet implemented")
    }

    override fun onMemberLeft(p0: RtmChannelMember?) {
        TODO("Not yet implemented")
    }
}