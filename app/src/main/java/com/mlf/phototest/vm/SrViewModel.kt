package com.mlf.phototest.vm

import android.graphics.Bitmap
import androidx.core.graphics.scale
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mlf.phototest.util.FileUtil
import com.mlf.phototest.util.TorchUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.sqrt

class SrViewModel : ViewModel() {
    private var _uiState = MutableStateFlow(SrUiState())
    val uiState = _uiState.asStateFlow()

    fun process(id: Long) {
        viewModelScope.launch(Dispatchers.Default) {
            _uiState.update { state -> state.copy(isLoading = true) }

            val uri = FileUtil.mediaId2Uri(id)
            var bmp = FileUtil.uri2Bitmap(uri)
            if (bmp != null) {
                val width = bmp.width
                val height = bmp.height
                val pixelCount = width * height
                if (pixelCount > 1000000) {
                    val scaleFactor = sqrt(pixelCount / 1000000.0)
                    bmp = bmp.scale((width / scaleFactor).toInt(), (height / scaleFactor).toInt())
                }
                _uiState.update { state -> state.copy(resultBmp = TorchUtil.runRealesr(bmp)) }
            }

            _uiState.update { state -> state.copy(isLoading = false) }
        }
    }

    fun save(
        onSuccess: (Long) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value.resultBmp?.let {
                val id = FileUtil.saveBitmap(it)
                withContext(Dispatchers.Main) {
                    onSuccess(id)
                }
            }
        }
    }

}

data class SrUiState(
    val resultBmp: Bitmap? = null, val isLoading: Boolean = true
)