package com.example.jbvideochat.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.example.jbvideochat.databinding.FragmentChatBinding
import com.example.jbvideochat.ui.BindingFragment
import com.example.jbvideochat.ui.videochat.VideoChatViewModel
import com.example.jbvideochat.util.Constants
import com.example.jbvideochat.util.RtmClientListnerImpl
import dagger.hilt.android.AndroidEntryPoint
import io.agora.rtc.RtcEngine
import io.agora.rtm.*
import kotlinx.coroutines.flow.collect
import javax.inject.Inject


@AndroidEntryPoint
class ChatFragment  : BindingFragment<FragmentChatBinding>() {




    private val viewModel: ChatViewModel by viewModels()



    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentChatBinding::inflate

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


/*        try {

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
                        val text = """Message received from $peerId Message: ${rtmMessage.text}"""
                        writeToMessageHistory(text)
                    }
                })
        } catch (e: Exception) {
            throw RuntimeException("RTM initialization failed!")
        }*/



        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getUserToken()

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = viewLifecycleOwner


        // Initialize viewModel
        binding.viewmodel = viewModel


        binding.messageRecyclerview.adapter = ChatAdapter()


        lifecycleScope.launchWhenCreated {
            viewModel.userTokenState.collect { userTokenState ->

                when (userTokenState) {
                    is ChatViewModel.GetTokenEvent.Success -> {
                        Toast.makeText(
                            context,
                            "User token loaded successfully.",
                            Toast.LENGTH_SHORT
                        )
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

        // Pbserve data from RtmClienListener onMessageReceived callback
        viewModel.actualMessage.observe(viewLifecycleOwner, Observer {
            binding.messageHistory.text = it
        })

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = viewLifecycleOwner


        // Initialize viewModel
        binding.viewmodel = viewModel


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
        viewModel.login()
    }


    // Button to join the RTM channel
    fun onClickJoin() {
        viewModel.joinChannel()
    }

    // Button to send channel message
    fun onClickSendChannelMsg() {
        viewModel.sendChannelMsg(binding.msgBox.text.toString())
    }


    // Log out of RTM system
    fun onClickLogout() {

    }

    // Leave channel
    fun onClickLeave() {

    }

    // Function to write to message history
    fun writeToMessageHistory(record: String) {
        binding.messageHistory.append(record)
    }


}