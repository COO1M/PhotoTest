package com.mlf.phototest.view

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.mlf.phototest.R
import com.mlf.phototest.theme.Black
import com.mlf.phototest.theme.Transparent
import com.mlf.phototest.theme.White
import com.mlf.phototest.util.FileUtil
import com.mlf.phototest.view.common.Container
import com.mlf.phototest.view.common.RoundBtn
import com.mlf.phototest.vm.CropViewModel
import kotlin.math.abs

@Composable
fun CropScreenOld(photoId: Long, onNavigateBack: () -> Unit = {}) {
    val vm: CropViewModel = viewModel()
    val uiState by vm.uiState.collectAsStateWithLifecycle()

    var imgWidth by remember { mutableStateOf(0f) }
    var imgHeight by remember { mutableStateOf(0f) }
    var rectStartX by remember { mutableStateOf(150f) }
    var rectStartY by remember { mutableStateOf(150f) }
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

            ConstraintLayout {
                val (img, canvas) = createRefs()

                AsyncImage(
                    model = FileUtil.mediaId2Uri(photoId),
                    contentDescription = stringResource(R.string.image),
                    modifier = Modifier
                        .fitBoundsOld(uiState.bitmapWidth / uiState.bitmapHeight >= parentWidth / parentHeight)
                        .onSizeChanged {
                            imgWidth = it.width.toFloat()
                            imgHeight = it.height.toFloat()
                        }
                        .constrainAs(img) {}
                )

                Canvas(
                    modifier = Modifier
                        .constrainAs(canvas) {
                            top.linkTo(img.top)
                            bottom.linkTo(img.bottom)
                            start.linkTo(img.start)
                            end.linkTo(img.end)
                            width = Dimension.fillToConstraints
                            height = Dimension.fillToConstraints
                        }
                        .pointerInput(Unit) {
                            detectTransformGestures { centroid, pan, zoom, rotation ->
                                if (abs(centroid.x - rectStartX) <= 50) rectStartX =
                                    (rectStartX + pan.x).coerceIn(0f, rectEndX - 200f)
                                if (abs(rectEndX - centroid.x) <= 50) rectEndX =
                                    (rectEndX + pan.x).coerceIn(rectStartX + 200f, imgWidth)
                                if (abs(centroid.y - rectStartY) <= 50) rectStartY =
                                    (rectStartY + pan.y).coerceIn(0f, rectEndY - 200f)
                                if (abs(rectEndY - centroid.y) <= 50) rectEndY =
                                    (rectEndY + pan.y).coerceIn(rectStartY + 200f, imgHeight)
                            }
                        }
                ) {
                    drawRect(
                        color = Transparent,
                        topLeft = Offset(rectStartX, rectStartY),
                        size = Size(rectEndX - rectStartX, rectEndY - rectStartY)
                    )
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
                        strokeWidth = 3f
                    )
                    drawLine(
                        color = White,
                        start = Offset(rectEndX, rectStartY),
                        end = Offset(rectEndX, rectEndY),
                        strokeWidth = 3f
                    )
                    drawLine(
                        color = White,
                        start = Offset(rectStartX, rectStartY),
                        end = Offset(rectStartX, rectEndY),
                        strokeWidth = 3f
                    )
                    drawLine(
                        color = White,
                        start = Offset(rectStartX, rectEndY),
                        end = Offset(rectEndX, rectEndY),
                        strokeWidth = 3f
                    )
                    drawCircle(
                        color = White,
                        center = Offset(rectStartX, rectStartY),
                        radius = 5f
                    )
                    drawCircle(
                        color = White,
                        center = Offset(rectEndX, rectStartY),
                        radius = 5f
                    )
                    drawCircle(
                        color = White,
                        center = Offset(rectStartX, rectEndY),
                        radius = 5f
                    )
                    drawCircle(
                        color = White,
                        center = Offset(rectEndX, rectEndY),
                        radius = 5f
                    )


                }
            }
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
                onClick = onNavigateBack
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
                        rectEndY - rectStartY
                    )
                    onNavigateBack()
                }
            )
        }

    }

}

fun Modifier.fitBoundsOld(isHorizontal: Boolean) =
    if (isHorizontal) fillMaxWidth() else fillMaxHeight()