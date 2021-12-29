package com.example.jbvideochat.ui.videochat

import androidx.lifecycle.*
import com.example.jbvideochat.repository.MainRepository
import com.example.jbvideochat.repository.MainRepositoryImpl
import com.example.jbvideochat.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideoChatViewModel @Inject constructor(
    private val state: SavedStateHandle,
    private val mainRepositoryImpl: MainRepositoryImpl
) : ViewModel() {


    // Get selected user data from savedStateHandle, which holds navArgs data by argument name "username"
    private val _username = MutableLiveData(state.get<String>("username")!!)
    val username: LiveData<String>
        get() = _username

    // Get selected user data from savedStateHandle, which holds navArgs data by argument name "channelname"
    private val _channelname = MutableLiveData(state.get<String>("channel_name")!!)
    val channelname: LiveData<String>
        get() = _channelname


}

