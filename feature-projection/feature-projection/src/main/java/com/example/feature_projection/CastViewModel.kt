package com.example.feature_projection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CastViewModel @Inject constructor() : ViewModel() {
    
    private val _startCasting = MutableStateFlow(false)
    val startCasting: StateFlow<Boolean> = _startCasting.asStateFlow()
    
    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()
    
    fun triggerCast() {
        viewModelScope.launch {
            _startCasting.value = true
            // Simular conexión
            _isConnected.value = true
        }
    }
    
    fun stopCasting() {
        viewModelScope.launch {
            _startCasting.value = false
            _isConnected.value = false
        }
    }
    
    fun resetCasting() {
        viewModelScope.launch {
            _startCasting.value = false
        }
    }
}
