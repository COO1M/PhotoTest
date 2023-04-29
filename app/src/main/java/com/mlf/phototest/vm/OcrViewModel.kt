package com.mlf.phototest.vm

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.chinese.ChineseTextRecognizerOptions
import com.mlf.phototest.App
import com.mlf.phototest.util.FileUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OcrViewModel : ViewModel() {

    private var _uiState = MutableStateFlow(OcrUiState())
    val uiState = _uiState.asStateFlow()


    fun changeText(s: String) {
        _uiState.update { state ->
            state.copy(text = s)
        }
    }

    fun process(id: Long) {
        viewModelScope.launch(Dispatchers.Default) {
            _uiState.update { state -> state.copy(isLoading = true) }
            val uri = FileUtil.mediaId2Uri(id)
            val recognizer =
                TextRecognition.getClient(ChineseTextRecognizerOptions.Builder().build())
            val image = kotlin.runCatching { InputImage.fromFilePath(App.act, uri) }.getOrNull()
            if (image != null) {
                recognizer.process(image).addOnSuccessListener {
                    _uiState.update { state ->
                        state.copy(text = it.text)
                    }
                }.addOnFailureListener {
                    Log.d("TAG", "OcrProcessError==>${it.stackTraceToString()}")
                }
            }
            _uiState.update { state -> state.copy(isLoading = false) }
        }
    }

}

data class OcrUiState(
    val uri: Uri = Uri.EMPTY, val text: String = "", val isLoading: Boolean = true
)