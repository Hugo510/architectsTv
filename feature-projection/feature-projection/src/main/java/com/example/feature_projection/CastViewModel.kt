package com.example.feature_projection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class CastViewModel : ViewModel() {
    private val _startCasting = MutableSharedFlow<Boolean>()
    val startCasting: SharedFlow<Boolean> = _startCasting.asSharedFlow()

    fun triggerCast() {
        viewModelScope.launch { _startCasting.emit(true) }
    }
}
