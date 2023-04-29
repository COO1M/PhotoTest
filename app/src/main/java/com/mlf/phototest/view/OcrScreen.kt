package com.mlf.phototest.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mlf.phototest.R
import com.mlf.phototest.view.common.Container
import com.mlf.phototest.view.common.TopBackTextBar
import com.mlf.phototest.vm.OcrViewModel

@Composable
fun OcrScreen(
    photoId: Long,
    onNavigateBack: () -> Unit = {}
) {
    val vm: OcrViewModel = viewModel()
    val uiState by vm.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        vm.process(photoId)
    }

    Container {

        TopBackTextBar(titleId = R.string.extract_text, onBack = onNavigateBack)

        if (!uiState.isLoading) {
            OutlinedTextField(
                value = uiState.text,
                onValueChange = { vm.changeText(it) },
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

    }

}