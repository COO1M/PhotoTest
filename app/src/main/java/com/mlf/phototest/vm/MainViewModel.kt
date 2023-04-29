package com.mlf.phototest.vm

import androidx.lifecycle.ViewModel
import com.mlf.phototest.util.FileUtil
import com.mlf.phototest.util.PermitUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MainViewModel : ViewModel() {

    private var _uiState = MutableStateFlow(MainUiState())
    val uiState = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        _uiState.update { state ->
            state.copy(
                permit = PermitUtil.checkAll(),
                photoIds = FileUtil.scanPhotos()
            )
        }
    }

}

data class MainUiState(
    val permit: Boolean = false,
    val photoIds: List<Long> = listOf()
)