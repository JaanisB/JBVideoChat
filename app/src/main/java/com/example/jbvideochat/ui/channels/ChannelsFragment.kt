package com.example.jbvideochat.ui.channels

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.example.jbvideochat.databinding.FragmentChannelsBinding
import com.example.jbvideochat.ui.BindingFragment

class ChannelsFragment : BindingFragment<FragmentChannelsBinding>() {

    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentChannelsBinding::inflate

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()

    }


}