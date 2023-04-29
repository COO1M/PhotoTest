package com.mlf.phototest.vm

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.mlf.phototest.util.FileUtil
import com.mlf.phototest.util.TimeUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CheckViewModel : ViewModel() {

    private var _uiState = MutableStateFlow(CheckUiState())
    val uiState = _uiState.asStateFlow()

    fun getMediaInfo(id: Long) {
        FileUtil.getMediaInfo(
            id = id,
            onInfoCallback = { name, time, size ->
                _uiState.update { state ->
                    state.copy(
                        uri = FileUtil.mediaId2Uri(id),
                        name = name,
                        time = TimeUtil.millisFormat(time),
                        size = FileUtil.convertSizeUnit(size)
                    )
                }
            }
        )
    }

}

data class CheckUiState(
    val uri: Uri = Uri.EMPTY,
    val name: String = "",
    val time: String = "",
    val size: String = ""
)