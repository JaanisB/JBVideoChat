package com.example.jbvideochat.ui.chat

import android.widget.Toast
import androidx.lifecycle.*
import com.example.jbvideochat.model.Message
import com.example.jbvideochat.model.Token
import com.example.jbvideochat.repository.MainRepositoryImpl
import com.example.jbvideochat.util.Resource
import com.example.jbvideochat.util.RtmClientListnerImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import io.agora.rtm.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ChatViewModel @Inject constructor(
    private val state: SavedStateHandle,
    private val mainRepositoryImpl: MainRepositoryImpl,
    private val mRtmClientListener: RtmClientListnerImpl,
    private val mRtmClient: RtmClient

) : ViewModel() {

    sealed class GetTokenEvent {
        class Success(token: Token) : GetTokenEvent()
        class Failure(errorText: String) : GetTokenEvent()
        object Loading : GetTokenEvent()
        object Empty : GetTokenEvent()
    }

    /**
     * GET TOKEN LOGIC AND MESSAGES
     * */

    private val _userTokenState = MutableStateFlow<GetTokenEvent>(GetTokenEvent.Empty)
    val userTokenState: StateFlow<GetTokenEvent> = _userTokenState

    private val _userToken = MutableLiveData<Token>()
    val userToken: LiveData<Token>
        get() = _userToken


    // Get selected user data from savedStateHandle, which holds navArgs data by argument name "username"
    private val _username = MutableLiveData(state.get<String>("username_ch")!!)
    val username: LiveData<String>
        get() = _username

    // Get selected user data from savedStateHandle, which holds navArgs data by argument name "channelname"
    private val _channelname = MutableLiveData(state.get<String>("channel_name_ch")!!)
    val channelname: LiveData<String>
        get() = _channelname

    private val _messageList = MutableLiveData<List<Message>>()
    val messageList: LiveData<List<Message>>
        get() = _messageList

    private val _actualMessage = mRtmClientListener.actualMessage
    val actualMessage: LiveData<String>
        get() = _actualMessage


    fun getUserToken() {
        viewModelScope.launch {
            _userTokenState.value = GetTokenEvent.Loading

            when (val userResponse = mainRepositoryImpl.getToken(username.value)) {

                is Resource.Success -> {
                    _userTokenState.value = GetTokenEvent.Success(userResponse.data!!)
                    _userToken.value = userResponse.data
                }

                is Resource.Loading -> {
                    _userTokenState.value = GetTokenEvent.Loading
                }

                is Resource.Error -> {
                    _userTokenState.value = GetTokenEvent.Failure(userResponse.message!!)
                }

            }
        }
    }

    fun updateMessageList(id: Int, username: String, message: String) {

        viewModelScope.launch {
            _messageList.value = _messageList.value?.plus(Message(id, username, message)) ?: listOf(
                Message(
                    id,
                    username,
                    message
                )
            )
        }

    }


    // VIDEO CHAT SDK

    private lateinit var mRtmChannel: RtmChannel

    // RTM uid
    private var uid: String? = null


    // Login to SDK
    fun login() {

        // Log in to the RTM system
        mRtmClient.login(
            userToken.value?.token,
            username.value,
            object : ResultCallback<Void> {


                override fun onSuccess(p0: Void?) {


                }

                override fun onFailure(p0: ErrorInfo?) {

                }
            })
    }

    // Join the RTM channel
    fun joinChannel() {

        val channel_name = channelname.value
        // Create a channel listener
        val mRtmChannelListener: RtmChannelListener = object : RtmChannelListener {
            override fun onMemberCountUpdated(i: Int) {}
            override fun onAttributesUpdated(list: List<RtmChannelAttribute>) {}
            override fun onMessageReceived(message: RtmMessage, fromMember: RtmChannelMember) {
                val text = message.text
                val fromUser = fromMember.userId
                var id = 1
                val message_text = "Message received from $fromUser : $text\n"

            }

            override fun onImageMessageReceived(
                rtmImageMessage: RtmImageMessage,
                rtmChannelMember: RtmChannelMember
            ) {
            }

            override fun onFileMessageReceived(
                rtmFileMessage: RtmFileMessage,
                rtmChannelMember: RtmChannelMember
            ) {
            }

            override fun onMemberJoined(member: RtmChannelMember) {}
            override fun onMemberLeft(member: RtmChannelMember) {}
        }
        try {
            // Create an RTM channel
            mRtmChannel = mRtmClient.createChannel(channel_name, mRtmChannelListener)
        } catch (e: RuntimeException) {
        }
        // Join the RTM channel

        mRtmChannel.join(object : ResultCallback<Void> {

            override fun onSuccess(responseInfo: Void) {

            }

            override fun onFailure(errorInfo: ErrorInfo) {
                val text: CharSequence =
                    "User: " + uid.toString() + " failed to join the channel!" + errorInfo.toString()

            }
        })
    }

    // Send channel message
    fun sendChannelMsg(msgText: String) {

        //Create RTM message instance
        val message: RtmMessage = mRtmClient.createMessage()
        message.text = msgText

        // Send message to channel
        mRtmChannel.sendMessage(message, object : ResultCallback<Void?> {
            override fun onSuccess(p0: Void?) {
                val text = """"Message sent to channel ${mRtmChannel.id} : ${msgText}"""
                // writeToMessageHistory(text)


                //Update viewmodel varibale for ChatAdapter
                updateMessageList(1, "user1", msgText!!)

            }

            override fun onFailure(p0: ErrorInfo?) {
                val text =
                    """Message fails to send to channel ${mRtmChannel.id} Error: $p0"""
                // writeToMessageHistory(text)
            }

        })
    }

    // Log out of RTM system
    fun logout() {
        mRtmClient.logout(null)
    }

    // Leave channel
    fun leave() {
        mRtmChannel.leave(null)
    }


}