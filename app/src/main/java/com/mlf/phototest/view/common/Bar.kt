package com.mlf.phototest.view.common

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mlf.phototest.R

@Composable
fun TopTextBar(
    @StringRes titleId: Int
) {
    Text(
        text = stringResource(titleId),
        style = MaterialTheme.typography.h4
    )
}

@Composable
fun TopBackTextBar(
    @StringRes titleId: Int,
    modifier: Modifier = Modifier,
    onBack: () -> Unit
) {
    Row(
        modifier = modifier.height(50.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(
                painter = painterResource(R.drawable.baseline_arrow_back),
                contentDescription = stringResource(R.string.back_last_page)
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = stringResource(titleId),
            style = MaterialTheme.typography.h5
        )
    }
}

@Composable
fun TopBackBar(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Row(
        modifier = modifier.height(50.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(
                painter = painterResource(R.drawable.baseline_arrow_back),
                contentDescription = stringResource(R.string.back_last_page)
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column(content = content)
    }
}

@Composable
fun Container(
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        content = content
    )
}