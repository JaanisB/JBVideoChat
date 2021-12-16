package com.example.jbvideochat.ui.videochat

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.agora.rtc.IRtcEngineEventHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class VideoChatViewModel  @Inject constructor(

): ViewModel() {


    private val _isRemoteUserConnected = MutableStateFlow(false)
    val isRemoteUserConnected = _isRemoteUserConnected.asStateFlow()








}