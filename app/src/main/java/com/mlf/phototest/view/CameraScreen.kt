package com.mlf.phototest.view

import android.view.GestureDetector
import android.view.MotionEvent
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mlf.phototest.R
import com.mlf.phototest.theme.BrilliantAzure
import com.mlf.phototest.view.common.RoundBtn
import com.mlf.phototest.vm.CameraViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun CameraScreen(
    onNavigateBack: () -> Unit = {}
) {
    val vm: CameraViewModel = viewModel()
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutine = rememberCoroutineScope()
    var focusPos by remember { mutableStateOf(Offset.Zero) }
    val zoomState by vm.cameraController.zoomState.observeAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { ctx ->
                PreviewView(ctx).also { cameraPreview ->
                    vm.cameraController.bindToLifecycle(lifecycleOwner)
                    cameraPreview.controller = vm.cameraController
                    cameraPreview.implementationMode = PreviewView.ImplementationMode.COMPATIBLE

                    val gesture =
                        GestureDetector(ctx, object : GestureDetector.SimpleOnGestureListener() {
                            override fun onSingleTapUp(e: MotionEvent): Boolean {
                                coroutine.launch {
                                    focusPos = Offset(e.x, e.y)
                                    delay(500)
                                    focusPos = Offset.Zero
                                }
                                return false
                            }
                        })
                    cameraPreview.setOnTouchListener { v, event ->
                        if (event.action == MotionEvent.ACTION_UP) {
                            v.performClick()
                        }
                        return@setOnTouchListener gesture.onTouchEvent(event)
                    }
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .drawWithContent {
                    drawContent()
                    if (focusPos != Offset.Zero) {
                        drawRoundRect(
                            color = BrilliantAzure,
                            topLeft = focusPos.minus(Offset(100f, 100f)),
                            size = Size(200f, 200f),
                            cornerRadius = CornerRadius(50f, 50f),
                            style = Stroke(5f)
                        )
                    }
                }
        )
        RoundBtn(
            iconId = R.drawable.baseline_close,
            labelId = R.string.cancel,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(20.dp),
            onClick = onNavigateBack
        )
        RoundBtn(
            iconId = R.drawable.baseline_camera,
            labelId = R.string.shoot,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(20.dp),
            onClick = {
                vm.shoot()
            }
        )
        RoundBtn(
            iconId = R.drawable.baseline_autorenew,
            labelId = R.string.shoot,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp),
            onClick = {
                vm.changeCamera()
            }
        )
        Text(
            text = "%.1fX".format(zoomState?.zoomRatio),
            color = MaterialTheme.colors.primary,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(100.dp)
        )
    }

}