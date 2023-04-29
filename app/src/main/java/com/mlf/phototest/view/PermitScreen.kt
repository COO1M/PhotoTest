package com.mlf.phototest.view

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mlf.phototest.App
import com.mlf.phototest.R
import com.mlf.phototest.theme.Apple
import com.mlf.phototest.util.PermitUtil
import com.mlf.phototest.util.ToastUtil
import com.mlf.phototest.view.common.Btn
import com.mlf.phototest.view.common.Container
import com.mlf.phototest.view.common.TopBackTextBar
import com.mlf.phototest.vm.PermitViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PermitScreen(
    onNavigateBack: () -> Unit = {}
) {
    val vm: PermitViewModel = viewModel()
    val uiState by vm.uiState.collectAsStateWithLifecycle()

    val permitLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        vm.check()
    }

    val intentLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        vm.check()
    }

    Container {

        TopBackTextBar(titleId = R.string.manage_permit, onBack = onNavigateBack)

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            itemsIndexed(PermitUtil.permissions) { i, item ->
                ListItem(
                    text = { Text(text = stringResource(item.labelId)) },
                    secondaryText = { Text(text = stringResource(item.infoId)) },
                    trailing = {
                        if (uiState.grantState[i]) {
                            Icon(
                                painter = painterResource(R.drawable.baseline_check_circle),
                                contentDescription = stringResource(R.string.success),
                                tint = Apple
                            )
                        } else {
                            Btn(labelId = R.string.grant) {
                                if (item.value == Manifest.permission.MANAGE_EXTERNAL_STORAGE && Build.VERSION.SDK_INT >= 30) {
                                    val intent = Intent(
                                        Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
                                    ).apply {
                                        data = Uri.parse("package:" + App.act.packageName)
                                    }
                                    intentLauncher.launch(intent)
                                } else {
                                    if (uiState.neverState[i]) {
                                        val intent = Intent(
                                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                                        ).apply {
                                            data = Uri.parse("package:" + App.act.packageName)
                                        }
                                        intentLauncher.launch(intent)
                                        ToastUtil.normal(R.string.permit_denied)
                                    } else {
                                        permitLauncher.launch(item.value)
                                    }
                                }
                            }
                        }
                    }
                )
            }
        }

    }

}