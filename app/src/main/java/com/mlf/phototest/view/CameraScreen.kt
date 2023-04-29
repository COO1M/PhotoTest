package com.mlf.phototest.view

import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mlf.phototest.R
import com.mlf.phototest.view.common.Container
import com.mlf.phototest.view.common.RoundBtn
import com.mlf.phototest.vm.CameraViewModel

@Composable
fun CameraScreen(
    onNavigateBack: () -> Unit = {}
) {
    val vm: CameraViewModel = viewModel()
    val lifecycleOwner = LocalLifecycleOwner.current

    Container {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            AndroidView(
                factory = { ctx ->
                    PreviewView(ctx).also { cameraPreview ->
                        cameraPreview.implementationMode = PreviewView.ImplementationMode.COMPATIBLE

                        vm.cameraController.bindToLifecycle(lifecycleOwner)
                        cameraPreview.controller = vm.cameraController
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            RoundBtn(
                iconId = R.drawable.baseline_close,
                labelId = R.string.cancel,
                modifier = Modifier.align(Alignment.CenterStart),
                onClick = onNavigateBack
            )
            RoundBtn(
                iconId = R.drawable.baseline_camera,
                labelId = R.string.shoot,
                modifier = Modifier.align(Alignment.Center),
                onClick = {
                    vm.shoot()
                }
            )
            RoundBtn(
                iconId = R.drawable.baseline_autorenew,
                labelId = R.string.shoot,
                modifier = Modifier.align(Alignment.CenterEnd),
                onClick = {
                    vm.changeCamera()
                }
            )
        }

    }

}