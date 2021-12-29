package com.example.jbvideochat.ui.chat

import androidx.lifecycle.*
import com.example.jbvideochat.model.Token
import com.example.jbvideochat.repository.MainRepositoryImpl
import com.example.jbvideochat.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ChatViewModel @Inject constructor(
    private val state: SavedStateHandle,
    private val mainRepositoryImpl: MainRepositoryImpl
) : ViewModel() {

    sealed class GetTokenEvent {
        class Success(token: String) : GetTokenEvent()
        class Failure(errorText: String) : GetTokenEvent()
        object Loading : GetTokenEvent()
        object Empty : GetTokenEvent()
    }

    private val _userTokenState = MutableStateFlow<GetTokenEvent>(GetTokenEvent.Empty)
    val userTokenState: StateFlow<GetTokenEvent> = _userTokenState

    private val _userToken = MutableLiveData<String>()
    val userToken: LiveData<String>
        get() = _userToken


    // Get selected user data from savedStateHandle, which holds navArgs data by argument name "username"
    private val _username = MutableLiveData(state.get<String>("username_ch")!!)
    val username: LiveData<String>
        get() = _username

    // Get selected user data from savedStateHandle, which holds navArgs data by argument name "channelname"
    private val _channelname = MutableLiveData(state.get<String>("channel_name_ch")!!)
    val channelname: LiveData<String>
        get() = _channelname


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
}