package com.example.jbvideochat.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.example.jbvideochat.databinding.FragmentChatBinding
import com.example.jbvideochat.ui.BindingFragment
import com.example.jbvideochat.ui.videochat.VideoChatViewModel
import com.example.jbvideochat.util.Constants
import dagger.hilt.android.AndroidEntryPoint
import io.agora.rtm.*
import kotlinx.coroutines.flow.collect


@AndroidEntryPoint
class ChatFragment : BindingFragment<FragmentChatBinding>() {

    private lateinit var mRtmClient: RtmClient
    private lateinit var mRtmChannel: RtmChannel

    private val viewModel: ChatViewModel by viewModels()

    // RTM uid
    private var uid: String? = null

    // Message content value
    private var message_content: String? = null

    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentChatBinding::inflate

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {



        try {

            // Initialize the RTM client
            mRtmClient = RtmClient.createInstance(requireContext(), Constants.APP_ID_TOKEN,
                object : RtmClientListener {
                    override fun onConnectionStateChanged(state: Int, reason: Int) {
                        val text =
                            """Connection state changed to ${state}Reason: $reason """.trimIndent()
                        writeToMessageHistory(text)
                    }

                    override fun onImageMessageReceivedFromPeer(
                        rtmImageMessage: RtmImageMessage,
                        s: String
                    ) {
                    }

                    override fun onFileMessageReceivedFromPeer(
                        rtmFileMessage: RtmFileMessage,
                        s: String
                    ) {
                    }

                    override fun onMediaUploadingProgress(
                        rtmMediaOperationProgress: RtmMediaOperationProgress,
                        l: Long
                    ) {
                    }

                    override fun onMediaDownloadingProgress(
                        rtmMediaOperationProgress: RtmMediaOperationProgress,
                        l: Long
                    ) {
                    }

                    override fun onTokenExpired() {}
                    override fun onPeersOnlineStatusChanged(map: Map<String, Int>) {}
                    override fun onMessageReceived(rtmMessage: RtmMessage, peerId: String) {
                        val text = """Message received from $peerId Message: ${rtmMessage.text}
"""
                        writeToMessageHistory(text)
                    }
                })
        } catch (e: Exception) {
            throw RuntimeException("RTM initialization failed!")
        }



        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getUserToken()

        lifecycleScope.launchWhenCreated {
            viewModel.userTokenState.collect { userTokenState ->

                when (userTokenState) {
                    is ChatViewModel.GetTokenEvent.Success -> {
                        Toast.makeText(context, "User token loaded successfully.", Toast.LENGTH_SHORT)
                            .show()
                    }
                    is ChatViewModel.GetTokenEvent.Failure -> {

                        Toast.makeText(context, "Failed to get token", Toast.LENGTH_SHORT)
                            .show()
                    }
                    is ChatViewModel.GetTokenEvent.Loading -> {
                        Toast.makeText(context, "Loading token", Toast.LENGTH_SHORT)
                            .show()
                    }
                    else -> {}
                }
            }
        }


        // Login button listener
        binding.loginButton.setOnClickListener {
            onClickLogin()
        }

        // Join channel button
        binding.joinButton.setOnClickListener {
            onClickJoin()
         }

        // Send channel message button
        binding.sendChannelMsgButton.setOnClickListener {
            onClickSendChannelMsg()
        }

        // Logout button
        binding.logoutButton.setOnClickListener {
            onClickLogout()
        }

        // Leave channel button
        binding.leaveButton.setOnClickListener {
            onClickLeave()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()

    }

    // When login button is clicked
    fun onClickLogin() {

        // Log in to the RTM system
        mRtmClient.login(viewModel.userToken.value?.token, viewModel.username.value, object : ResultCallback<Void> {


            override fun onSuccess(p0: Void?) {
                Toast.makeText(requireContext(), "Login successful!", Toast.LENGTH_SHORT).show()

            }

            override fun onFailure(p0: ErrorInfo?) {
                val text: CharSequence =
                    "User: $uid failed to log in!  $p0"
                Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
            }
        })
    }


    // Button to join the RTM channel
    fun onClickJoin() {

        val channel_name = viewModel.channelname.value
        // Create a channel listener
        val mRtmChannelListener: RtmChannelListener = object : RtmChannelListener {
            override fun onMemberCountUpdated(i: Int) {}
            override fun onAttributesUpdated(list: List<RtmChannelAttribute>) {}
            override fun onMessageReceived(message: RtmMessage, fromMember: RtmChannelMember) {
                val text = message.text
                val fromUser = fromMember.userId
                val message_text = "Message received from $fromUser : $text\n"
                writeToMessageHistory(message_text)
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

                Toast.makeText(requireContext(), "Successfully joined ${uid.toString()} channel", Toast.LENGTH_SHORT).show()
            }
            override fun onFailure(errorInfo: ErrorInfo) {
                val text: CharSequence =
                    "User: " + uid.toString() + " failed to join the channel!" + errorInfo.toString()

                Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Button to send channel message
    fun onClickSendChannelMsg() {
        message_content = binding.msgBox.text.toString()

        //Create RTM message instance
        val message: RtmMessage = mRtmClient.createMessage()
        message.text = message_content

        // Send message to channel
        mRtmChannel.sendMessage(message, object : ResultCallback<Void?> {
            override fun onSuccess(p0: Void?) {
                val text = """"Message sent to channel ${mRtmChannel.id} : ${message_content}"""
                writeToMessageHistory(text)
            }

            override fun onFailure(p0: ErrorInfo?) {
                val text =
                    """Message fails to send to channel ${mRtmChannel.id} Error: $p0"""
                writeToMessageHistory(text)
            }

        })
    }


    // Log out of RTM system
    fun onClickLogout() {
        mRtmClient.logout(null)
    }

    // Leave channel
    fun onClickLeave() {
        mRtmChannel.leave(null)
    }

    // Function to write to message history
    fun writeToMessageHistory(record: String) {
        binding.messageHistory.append(record)
    }


}