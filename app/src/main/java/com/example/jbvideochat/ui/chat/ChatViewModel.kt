package com.example.jbvideochat.ui.chat

import androidx.lifecycle.*
import com.example.jbvideochat.di.RtmChannelListenerImpl
import com.example.jbvideochat.model.Message
import com.example.jbvideochat.model.Token
import com.example.jbvideochat.repository.MainRepositoryImpl
import com.example.jbvideochat.util.Resource
import com.example.jbvideochat.di.RtmClientListnerImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import io.agora.rtm.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class ChatViewModel @Inject constructor(
    private val state: SavedStateHandle,
    private val mainRepositoryImpl: MainRepositoryImpl,
    private val mRtmClientListener: RtmClientListnerImpl,
    private val mRtmClient: RtmClient,
    private val mRtmChannelListener: RtmChannelListenerImpl

) : ViewModel() {


    sealed class GetTokenEvent {
        class Success(token: Token) : GetTokenEvent()
        class Failure(val errorText: String) : GetTokenEvent()
        object Loading : GetTokenEvent()
        object Empty : GetTokenEvent()
    }

    sealed class LoginEvent {
        object Loading : LoginEvent()
        object Success : LoginEvent()
        object Error : LoginEvent()
    }

    sealed class ChannelEvent {
        object Loading : ChannelEvent()
        object Success : ChannelEvent()
        object Error : ChannelEvent()
    }


    /**
     * GET TOKEN LOGIC AND MESSAGES
     * */

    // TOKEN, LOGIN and CHANNEL status

    private val _userTokenState = MutableStateFlow<GetTokenEvent>(GetTokenEvent.Empty)
    val userTokenState: StateFlow<GetTokenEvent> = _userTokenState

    private val _loginState = MutableStateFlow<LoginEvent>(LoginEvent.Loading)
    val loginState: StateFlow<LoginEvent> = _loginState

    private val _channelState = MutableStateFlow<ChannelEvent>(ChannelEvent.Loading)
    val channelState: StateFlow<ChannelEvent> = _channelState

    private val _isLoggedInState = MutableLiveData<Boolean>(false)
    val isLoggedInState: LiveData<Boolean>
        get() = _isLoggedInState

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

    private val _receivedMessageList = MutableLiveData<List<Message>>()
    val receivedMessageList: LiveData<List<Message>>
        get() = _receivedMessageList


    fun getUserToken() {
        viewModelScope.launch {

            when (val userResponse = mainRepositoryImpl.getToken(username.value)) {

                is Resource.Success -> {
                    _userTokenState.value = GetTokenEvent.Success(userResponse.data!!)
                    _userToken.value = userResponse.data
                    login()
                    _isLoggedInState.value = true
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


    fun updateMessageList(isReceived: Boolean, username: String, message: String) {

        viewModelScope.launch {
            _messageList.value =
                _messageList.value?.plus(Message(isReceived, username, message)) ?: listOf(
                    Message(isReceived, username, message)
                )
        }
    }

    val myFun: (RtmChannelMember?, RtmMessage?) -> Unit =
        { fromUser, message ->

            viewModelScope.launch {
                    _receivedMessageList.value = _receivedMessageList.value?.plus(
                        Message(
                            true,
                            fromUser!!.userId,
                            message!!.text
                        )
                    )
                        ?: listOf(
                            Message(true, fromUser!!.userId, message!!.text)
                        )
            }
        }


/*    fun updateReceivedMessageList() {
        viewModelScope.launch {
            mRtmChannelListener.myCallBackFun = myFun
        }
    }*/

    init {
        mRtmChannelListener.myCallBackFun = myFun
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
                    // Launch joinChannel function if login was succesfull
                    joinChannel()
                    _loginState.value = LoginEvent.Success
                }

                override fun onFailure(p0: ErrorInfo?) {
                    _loginState.value = LoginEvent.Error
                }
            })
    }

    // Join the RTM channel
    fun joinChannel() {

        val channel_name = channelname.value
        // Create a channel listener

        try {
            _channelState.value = ChannelEvent.Loading
            // Create an RTM channel
            mRtmChannel = mRtmClient.createChannel(channel_name, mRtmChannelListener)
            _channelState.value = ChannelEvent.Success
        } catch (e: RuntimeException) {
            _channelState.value = ChannelEvent.Error
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
                val text = """"Message sent to channel ${mRtmChannel.id} : $msgText"""

                //Update viewmodel varibale for ChatAdapter
                updateMessageList(false, "Me", msgText)
            }

            override fun onFailure(p0: ErrorInfo?) {
                val text =
                    """Message fails to send to channel ${mRtmChannel.id} Error: $p0"""
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