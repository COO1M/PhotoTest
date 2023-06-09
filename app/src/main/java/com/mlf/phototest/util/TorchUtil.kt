package com.mlf.phototest.util

import android.graphics.Bitmap
import com.mlf.phototest.App
import org.pytorch.IValue
import org.pytorch.LiteModuleLoader
import org.pytorch.torchvision.TensorImageUtils
import kotlin.math.roundToInt

object TorchUtil {
    private val normMeanRGB = floatArrayOf(0f, 0f, 0f)
    private val normStdRGB = floatArrayOf(1f, 1f, 1f)

    private val realesrModel by lazy {
        LiteModuleLoader.loadModuleFromAsset(App.ctx.assets, "realesrgan.ptl")
    }

    fun runRealesr(inputBitmap: Bitmap): Bitmap {
        val inputTensor = TensorImageUtils.bitmapToFloat32Tensor(
            inputBitmap,
            normMeanRGB,
            normStdRGB
        )
        val outputTensor = realesrModel.forward(IValue.from(inputTensor)).toTensor()
        val outputData = outputTensor.dataAsFloatArray
        val outputShape = outputTensor.shape()
        val outputWidth = outputShape[3].toInt()
        val outputHeight = outputShape[2].toInt()
        return pixels2bitmap(outputData, outputWidth, outputHeight)
    }

    private fun pixels2bitmap(input: FloatArray, width: Int, height: Int): Bitmap {
        val pixelsCount = height * width
        val pixels = IntArray(pixelsCount)
        val output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        val conversion = { v: Float -> ((v.coerceIn(0f, 1f)) * 255f).roundToInt() }

        for (i in 0 until pixelsCount) {
            val r = conversion(input[i] * normStdRGB[0] + normMeanRGB[0])
            val g = conversion(input[i + pixelsCount] * normStdRGB[1] + normMeanRGB[1])
            val b = conversion(input[i + 2 * pixelsCount] * normStdRGB[2] + normMeanRGB[2])
            pixels[i] = 255 shl 24 or (r and 0xff shl 16) or (g and 0xff shl 8) or (b and 0xff)
        }
        output.setPixels(pixels, 0, width, 0, 0, width, height)
        return output
    }

}