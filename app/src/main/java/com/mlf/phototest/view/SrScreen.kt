package com.mlf.phototest.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.mlf.phototest.R
import com.mlf.phototest.view.common.Container
import com.mlf.phototest.view.common.RoundBtn
import com.mlf.phototest.vm.SrViewModel

@Composable
fun SrScreen(
    photoId: Long,
    onNavigateBack: (savePhotoId: Long?) -> Unit = {}
) {
    val vm: SrViewModel = viewModel()
    val uiState by vm.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        vm.process(photoId)
    }

    if (!uiState.isLoading) {
        Container {
            AsyncImage(
                model = uiState.resultBmp,
                contentDescription = stringResource(R.string.image),
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            )
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
                        vm.save(onNavigateBack)
                    }
                )
            }
        }
    } else {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }

}