package com.mlf.phototest.view

import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.mlf.phototest.R
import com.mlf.phototest.theme.Black
import com.mlf.phototest.theme.White
import com.mlf.phototest.util.FileUtil
import com.mlf.phototest.view.common.Container
import com.mlf.phototest.view.common.RoundBtn
import com.mlf.phototest.vm.CropViewModel
import kotlin.math.abs

@Composable
fun CropScreen(photoId: Long, onNavigateBack: (savePhotoId: Long?) -> Unit = {}) {
    val vm: CropViewModel = viewModel()
    val uiState by vm.uiState.collectAsStateWithLifecycle()

    var imgWidth by remember { mutableStateOf(0f) }
    var imgHeight by remember { mutableStateOf(0f) }
    var rectStartX by remember { mutableStateOf(50f) }
    var rectStartY by remember { mutableStateOf(50f) }
    var rectEndX by remember { mutableStateOf(450f) }
    var rectEndY by remember { mutableStateOf(450f) }

    LaunchedEffect(Unit) {
        vm.initPhoto(photoId)
    }

    Container {

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            val parentWidth = with(LocalDensity.current) { maxWidth.toPx() }
            val parentHeight = with(LocalDensity.current) { maxHeight.toPx() }

            AsyncImage(
                model = FileUtil.mediaId2Uri(photoId),
                contentDescription = stringResource(R.string.image),
                modifier = Modifier
                    .fitBounds(uiState.bitmapWidth / uiState.bitmapHeight >= parentWidth / parentHeight)
                    .onSizeChanged {
                        imgWidth = it.width.toFloat()
                        imgHeight = it.height.toFloat()
                        rectEndX = imgWidth - 50f
                        rectEndY = imgHeight - 50f
                    }
                    .pointerInput(Unit) {
                        detectTransformGestures { centroid, pan, zoom, rotation ->
                            if (abs(centroid.x - rectStartX) <= 100) rectStartX =
                                (rectStartX + pan.x).coerceIn(0f, rectEndX - 200f)
                            if (abs(rectEndX - centroid.x) <= 100) rectEndX =
                                (rectEndX + pan.x).coerceIn(rectStartX + 200f, imgWidth)
                            if (abs(centroid.y - rectStartY) <= 100) rectStartY =
                                (rectStartY + pan.y).coerceIn(0f, rectEndY - 200f)
                            if (abs(rectEndY - centroid.y) <= 100) rectEndY =
                                (rectEndY + pan.y).coerceIn(rectStartY + 200f, imgHeight)
                        }
                    }
                    .drawWithContent {
                        drawContent()
                        if (imgWidth != 0f && imgHeight != 0f && uiState.bitmapWidth != 0f && uiState.bitmapHeight != 0f) {
                            drawRect(
                                color = Black.copy(alpha = 0.3f),
                                size = Size(imgWidth, rectStartY)
                            )
                            drawRect(
                                color = Black.copy(alpha = 0.3f),
                                topLeft = Offset(0f, rectEndY),
                                size = Size(imgWidth, imgHeight - rectEndY)
                            )
                            drawRect(
                                color = Black.copy(alpha = 0.3f),
                                topLeft = Offset(0f, rectStartY),
                                size = Size(rectStartX, rectEndY - rectStartY)
                            )
                            drawRect(
                                color = Black.copy(alpha = 0.3f),
                                topLeft = Offset(rectEndX, rectStartY),
                                size = Size(imgWidth - rectEndX, rectEndY - rectStartY)
                            )
                            drawLine(
                                color = White,
                                start = Offset(rectStartX, rectStartY),
                                end = Offset(rectEndX, rectStartY),
                                strokeWidth = 5f
                            )
                            drawLine(
                                color = White,
                                start = Offset(rectEndX, rectStartY),
                                end = Offset(rectEndX, rectEndY),
                                strokeWidth = 5f
                            )
                            drawLine(
                                color = White,
                                start = Offset(rectStartX, rectStartY),
                                end = Offset(rectStartX, rectEndY),
                                strokeWidth = 5f
                            )
                            drawLine(
                                color = White,
                                start = Offset(rectStartX, rectEndY),
                                end = Offset(rectEndX, rectEndY),
                                strokeWidth = 5f
                            )
                            drawCircle(
                                color = White,
                                center = Offset(rectStartX, rectStartY),
                                radius = 10f
                            )
                            drawCircle(
                                color = White,
                                center = Offset(rectEndX, rectStartY),
                                radius = 10f
                            )
                            drawCircle(
                                color = White,
                                center = Offset(rectStartX, rectEndY),
                                radius = 10f
                            )
                            drawCircle(
                                color = White,
                                center = Offset(rectEndX, rectEndY),
                                radius = 10f
                            )
                        }
                    }
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            RoundBtn(
                iconId = R.drawable.baseline_close,
                labelId = R.string.cancel,
                onClick = { onNavigateBack(null) }
            )
            RoundBtn(
                iconId = R.drawable.baseline_check,
                labelId = R.string.confirm,
                onClick = {
                    vm.crop(
                        imgWidth,
                        imgHeight,
                        rectStartX,
                        rectStartY,
                        rectEndX - rectStartX,
                        rectEndY - rectStartY,
                        onNavigateBack
                    )
                }
            )
        }

    }

}

fun Modifier.fitBounds(isHorizontal: Boolean) =
    if (isHorizontal) fillMaxWidth() else fillMaxHeight()