package com.mlf.phototest.vm

import android.media.MediaScannerConnection
import android.util.Log
import android.util.Size
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.mlf.phototest.App
import com.mlf.phototest.R
import com.mlf.phototest.util.FileUtil
import com.mlf.phototest.util.ToastUtil

class CameraViewModel(
    val cameraController: LifecycleCameraController = LifecycleCameraController(App.act)
) : ViewModel() {

    init {
        cameraController.apply {
            cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            imageCaptureTargetSize = CameraController.OutputSize(Size(1080, 1920))
            previewTargetSize = CameraController.OutputSize(Size(1080, 1920))
        }
    }

    fun shoot() {
        val file = FileUtil.createPhotoFile()
        val outputOption = ImageCapture.OutputFileOptions.Builder(file).build()
        cameraController.takePicture(
            outputOption,
            ContextCompat.getMainExecutor(App.act),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    ToastUtil.normal(R.string.success)
                    MediaScannerConnection.scanFile(App.act, arrayOf(file.absolutePath), null, null)
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.d("TAG", exception.stackTraceToString())
                }
            }
        )
    }

    fun changeCamera() {
        if (cameraController.cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
            cameraController.cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
        } else {
            cameraController.cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        }
    }

}
