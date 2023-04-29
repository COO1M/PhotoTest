package com.mlf.phototest.vm

import androidx.lifecycle.ViewModel
import com.mlf.phototest.util.PermitUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class PermitViewModel : ViewModel() {

    private var _uiState = MutableStateFlow(PermitUiState())
    val uiState = _uiState.asStateFlow()

    init {
        _uiState.update { state ->
            state.copy(
                grantState = PermitUtil.permissions.map { PermitUtil.check(it.value) },
                neverState = PermitUtil.permissions.map { false }
            )
        }
    }

    fun check() {
        _uiState.update { state ->
            state.copy(
                grantState = PermitUtil.permissions.map { PermitUtil.check(it.value) },
                neverState = PermitUtil.permissions.map { PermitUtil.checkNever(it.value) }
            )
        }
    }

}

data class PermitUiState(
    val grantState: List<Boolean> = listOf(),
    val neverState: List<Boolean> = listOf()
)