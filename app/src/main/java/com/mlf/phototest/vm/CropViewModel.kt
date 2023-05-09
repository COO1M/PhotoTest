package com.mlf.phototest.vm

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mlf.phototest.util.FileUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CropViewModel : ViewModel() {

    private var _uiState = MutableStateFlow(CropUiState())
    val uiState = _uiState.asStateFlow()

    fun initPhoto(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val uri = FileUtil.mediaId2Uri(id)
            val bitmap = FileUtil.uri2Bitmap(uri)
            if (bitmap != null) {
                _uiState.update { state ->
                    state.copy(
                        uri = uri,
                        bitmap = bitmap,
                        bitmapWidth = bitmap.width.toFloat(),
                        bitmapHeight = bitmap.height.toFloat()
                    )
                }
            }
        }
    }

    fun crop(
        imgWidth: Float,
        imgHeight: Float,
        rectX: Float,
        rectY: Float,
        rectWidth: Float,
        rectHeight: Float,
        onSuccess: (Long) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value.bitmap?.let { bmp ->
                val cropBmp = Bitmap.createBitmap(
                    bmp,
                    (rectX / imgWidth * bmp.width).toInt(),
                    (rectY / imgHeight * bmp.height).toInt(),
                    (rectWidth / imgWidth * bmp.width).toInt(),
                    (rectHeight / imgHeight * bmp.height).toInt()
                )
                val photoId = FileUtil.saveBitmap(cropBmp)
                withContext(Dispatchers.Main) {
                    onSuccess(photoId)
                }
            }
        }
    }
}

data class CropUiState(
    val uri: Uri = Uri.EMPTY,
    val bitmap: Bitmap? = null,
    val bitmapWidth: Float = 0f,
    val bitmapHeight: Float = 0f,
    val success: Boolean = false,
    val savePhotoId: Long = 0
)