package com.mlf.phototest.model

import androidx.annotation.StringRes

data class PermissionBean(
    val value: String,
    @StringRes val labelId: Int,
    @StringRes val infoId: Int
)