package com.mlf.phototest.util

import android.widget.Toast
import androidx.annotation.StringRes
import com.mlf.phototest.App

object ToastUtil {

    fun normal(
        @StringRes id: Int
    ) {
        Toast.makeText(App.act, App.act.getString(id), Toast.LENGTH_SHORT).show()
    }

}