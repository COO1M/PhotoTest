package com.mlf.phototest.util

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import com.mlf.phototest.App
import com.mlf.phototest.R
import com.mlf.phototest.model.PermissionBean

object PermitUtil {

    val permissions = listOf(
        PermissionBean(
            Manifest.permission.CAMERA,
            R.string.permit_camera_label,
            R.string.permit_camera_info
        ),
        PermissionBean(
            if (Build.VERSION.SDK_INT >= 30) Manifest.permission.MANAGE_EXTERNAL_STORAGE else Manifest.permission.WRITE_EXTERNAL_STORAGE,
            R.string.permit_external_label,
            R.string.permit_external_info
        )
    )

    fun check(permission: String): Boolean {
        return if (permission == Manifest.permission.MANAGE_EXTERNAL_STORAGE && Build.VERSION.SDK_INT >= 30) Environment.isExternalStorageManager()
        else App.act.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
    }

    fun checkNever(permission: String): Boolean {
        return if (permission != Manifest.permission.MANAGE_EXTERNAL_STORAGE)
            !App.act.shouldShowRequestPermissionRationale(permission)
        else
            false
    }

    fun checkAll(): Boolean {
        return permissions.all { check(it.value) }
    }

}