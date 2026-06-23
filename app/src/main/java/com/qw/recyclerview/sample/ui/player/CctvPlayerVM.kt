package com.qw.recyclerview.sample.ui.player

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class CctvPlayerVM : ViewModel() {

    val channels = MutableLiveData<Result<List<TvChannel>>>()

    fun refreshChannels() {
        viewModelScope.launch {
            channels.value = runCatching {
                TvChannelRepository.loadChannels()
            }
        }
    }
}
