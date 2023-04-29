package com.mlf.phototest.view

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.mlf.phototest.R
import com.mlf.phototest.theme.GiantsOrange
import com.mlf.phototest.theme.SelectiveYellow
import com.mlf.phototest.util.FileUtil
import com.mlf.phototest.util.ToastUtil
import com.mlf.phototest.view.common.Container
import com.mlf.phototest.view.common.Btn
import com.mlf.phototest.view.common.TopTextBar
import com.mlf.phototest.vm.MainViewModel

@Composable
fun MainScreen(
    onNavigateToPermit: () -> Unit = {},
    onNavigateToCamera: () -> Unit = {},
    onNavigateToCheck: (photoId: Long) -> Unit = {}
) {
    val vm: MainViewModel = viewModel()
    val uiState by vm.uiState.collectAsStateWithLifecycle()
    var createUri by remember { mutableStateOf(Uri.EMPTY) }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                vm.refresh()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val photoLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) {
        if (!it) FileUtil.delete(createUri)
    }

    Container {

        TopTextBar(titleId = R.string.app_name)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MainCameraItem(
                iconId = R.drawable.pic_camera_base,
                labelId = R.string.system_camera_shoot,
                backgroundColor = SelectiveYellow.copy(alpha = 0.2f),
                onClick = {
                    if (uiState.permit) {
                        createUri = FileUtil.createPhotoUri()
                        photoLauncher.launch(createUri)
                    } else onNavigateToPermit()
                }
            )
            MainCameraItem(
                iconId = R.drawable.pic_camera_enhance,
                labelId = R.string.builtin_camera_shoot,
                backgroundColor = GiantsOrange.copy(alpha = 0.2f),
                onClick = {
                    if (uiState.permit) onNavigateToCamera()
                    else onNavigateToPermit()
                }
            )
        }

        if (uiState.permit) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(5.dp),
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                items(uiState.photoIds) { item ->
                    MainPhotoItem(
                        uri = FileUtil.mediaId2Uri(item),
                        onClick = {
                            if (FileUtil.exist(item)) onNavigateToCheck(item)
                            else ToastUtil.normal(R.string.no_image)
                        }
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = stringResource(R.string.no_permit))
                Spacer(modifier = Modifier.height(10.dp))
                Btn(labelId = R.string.go_permit, onClick = onNavigateToPermit)
            }
        }

    }
}

@Composable
fun RowScope.MainCameraItem(
    @DrawableRes iconId: Int,
    @StringRes labelId: Int,
    backgroundColor: Color,
    onClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .weight(1f)
            .clip(RoundedCornerShape(15.dp))
            .background(backgroundColor)
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(iconId),
            contentDescription = stringResource(labelId),
            modifier = Modifier.size(48.dp)
        )
        Text(
            text = stringResource(labelId),
            style = MaterialTheme.typography.body2
        )
    }
}

@Composable
fun MainPhotoItem(
    uri: Uri,
    onClick: () -> Unit
) {
    AsyncImage(
        model = uri,
        contentDescription = stringResource(R.string.image),
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1 / 1f)
            .clickable { onClick() }
    )
}