package com.mlf.phototest.view

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.mlf.phototest.R
import com.mlf.phototest.util.FileUtil
import com.mlf.phototest.view.common.TopBackBar
import com.mlf.phototest.vm.CheckViewModel

@Composable
fun CheckScreen(
    navPhotoId: Long,
    savePhotoId: Long,
    onNavigateBack: () -> Unit = {},
    onNavigateToCrop: (photoId: Long) -> Unit = {},
    onNavigateToOcr: (photoId: Long) -> Unit = {},
    onNavigateToSr: (photoId: Long) -> Unit = {}
) {
    val vm: CheckViewModel = viewModel()
    val uiState by vm.uiState.collectAsStateWithLifecycle()
    var photoId by remember { mutableStateOf(0L) }
    LaunchedEffect(Unit) {
        photoId = if (savePhotoId != 0L) savePhotoId else navPhotoId
        vm.getMediaInfo(photoId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {

        TopBackBar(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
            onBack = onNavigateBack
        ) {
            Text(text = uiState.name, style = MaterialTheme.typography.h6)
            Row {
                Text(text = uiState.time, style = MaterialTheme.typography.caption)
                Spacer(modifier = Modifier.width(10.dp))
                Text(text = uiState.size, style = MaterialTheme.typography.caption)
            }
        }

        AsyncImage(
            model = uiState.uri,
            contentDescription = stringResource(R.string.image),
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
        ) {
            CheckItem(
                labelId = R.string.crop,
                iconId = R.drawable.baseline_crop,
                onClick = { onNavigateToCrop(photoId) }
            )
            CheckItem(
                labelId = R.string.extract_text,
                iconId = R.drawable.outline_font_download,
                onClick = { onNavigateToOcr(photoId) })
            CheckItem(
                labelId = R.string.quality_enhance,
                iconId = R.drawable.baseline_gradient,
                onClick = { onNavigateToSr(photoId) }
            )
            CheckItem(labelId = R.string.share, iconId = R.drawable.baseline_share, onClick = {
                FileUtil.sharePhoto(uiState.uri)
            })
            CheckItem(labelId = R.string.delete, iconId = R.drawable.outline_delete, onClick = {
                FileUtil.delete(uiState.uri)
                onNavigateBack()
            })
        }

    }

}


@Composable
fun RowScope.CheckItem(
    @StringRes labelId: Int,
    @DrawableRes iconId: Int,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .weight(1f)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(bounded = false, radius = 50.dp),
                onClick = onClick
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(painter = painterResource(iconId), contentDescription = stringResource(labelId))
        Text(text = stringResource(labelId), style = MaterialTheme.typography.caption)
    }
}