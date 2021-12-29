package com.example.jbvideochat.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.example.jbvideochat.R
import com.example.jbvideochat.databinding.FragmentHomeBinding
import com.example.jbvideochat.ui.BindingFragment
import com.example.jbvideochat.ui.chat.ChatViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BindingFragment<FragmentHomeBinding>() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.btnVideocall.setOnClickListener {

            if (binding.etxtUsername.text.isEmpty() || binding.etxtChannelname.text.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Please fill both fields",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                this.findNavController()
                    .navigate(
                        HomeFragmentDirections.actionLoginFragmentToVideoChatFragment(
                            binding.etxtUsername.text.toString(),
                            binding.etxtChannelname.text.toString()
                        )
                    )
            }
        }

        binding.btnChat.setOnClickListener {

            if (binding.etxtUsername.text.isEmpty() || binding.etxtChannelname.text.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Please fill both fields",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                // Call function to get userToken
                // Navigate to chat screen

                this.findNavController()
                    .navigate(
                        (HomeFragmentDirections.actionHomeFragmentToChatFragment(
                            binding.etxtUsername.text.toString(),
                            binding.etxtChannelname.text.toString()
                        ))
                    )
            }
        }

        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentHomeBinding::inflate
}