package com.example.jbvideochat.ui.chat

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.example.jbvideochat.databinding.FragmentChatBinding
import com.example.jbvideochat.ui.BindingFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect


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


        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (viewModel.isLoggedInState.value == false) {
            viewModel.getUserToken()
        }

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
                        Toast.makeText(context, "Failed to get token ${userTokenState.errorText}", Toast.LENGTH_SHORT)
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

        lifecycleScope.launchWhenCreated {
            viewModel.loginState.collect{ loginState ->
                when (loginState) {
                    is ChatViewModel.LoginEvent.Loading -> {
                        binding.loginStatus.apply {
                            text = "Login: Wait"
                            setBackgroundColor(Color.YELLOW)
                        }
                    }
                    is ChatViewModel.LoginEvent.Success -> {
                        binding.loginStatus.apply {
                            text = "Login: OK"
                            setBackgroundColor(Color.GREEN)

                        }
                    }
                    is ChatViewModel.LoginEvent.Error -> {
                        binding.loginStatus.apply {
                            text = "Login: Error"
                            setBackgroundColor(Color.RED)

                        }
                    }
                }
            }
        }

        lifecycleScope.launchWhenCreated {
            viewModel.channelState.collect{ channelState ->
                when (channelState) {
                    is ChatViewModel.ChannelEvent.Loading -> {
                        binding.channelStatus.apply {
                            text = "Channel: Wait"
                            setBackgroundColor(Color.YELLOW)
                        }
                    }
                    is ChatViewModel.ChannelEvent.Success -> {
                        binding.channelStatus.apply {
                            text = "Channel: OK"
                            setBackgroundColor(Color.GREEN)
                        }
                    }

                    is ChatViewModel.ChannelEvent.Error -> {
                        binding.channelStatus.apply {
                            text = "Channel: Error"
                            setBackgroundColor(Color.RED)
                        }
                    }
                }
            }
        }


        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = viewLifecycleOwner

        // Initialize viewModel
        binding.viewmodel = viewModel



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

}