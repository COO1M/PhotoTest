package com.mlf.phototest.util

import android.content.ContentUris
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.mlf.phototest.App
import java.io.File
import java.io.FileOutputStream

object FileUtil {

    private const val relativeSavePath = "Pictures/PhotoTest/"
    private val absoluteSavePath =
        "${Environment.getExternalStorageDirectory().absolutePath}/$relativeSavePath"

    fun createPhotoUri(): Uri {
        val name = "${System.currentTimeMillis()}.jpg"
        return if (Build.VERSION.SDK_INT >= 29) {
            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, name)
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put(MediaStore.Images.Media.RELATIVE_PATH, relativeSavePath)
            }
            App.act.contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            ) ?: Uri.EMPTY
        } else {
            FileProvider.getUriForFile(
                App.act,
                "${App.act.packageName}.fileprovider",
                File("$absoluteSavePath$name")
            )
        }
    }

    fun createPhotoFile(): File {
        val name = "${System.currentTimeMillis()}.jpg"
        return File(absoluteSavePath, name)
    }

    fun scanPhotos(): List<Long> {
        val list: MutableList<Long> = mutableListOf()

        App.act.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            arrayOf(MediaStore.Images.Media._ID),
            "${MediaStore.Images.Media.DATA} LIKE ?",
            arrayOf("$absoluteSavePath%"),
            "${MediaStore.Images.Media.DATE_MODIFIED} DESC"
        )?.use { cursor ->
            while (cursor.moveToNext()) {
                list += cursor.getLong(0)
            }
        }

        return list
    }

    fun mediaId2Uri(id: Long): Uri {
        return ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
    }

    fun getMediaInfo(id: Long, onInfoCallback: (name: String, time: Long, size: Long) -> Unit) {
        App.act.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            arrayOf(
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_MODIFIED,
                MediaStore.Images.Media.SIZE,
            ),
            "${MediaStore.Images.Media._ID} = ?",
            arrayOf("$id"),
            null
        )?.use { cursor ->
            cursor.moveToFirst()
            onInfoCallback(cursor.getString(0), cursor.getLong(1), cursor.getLong(2))
        }
    }

    fun convertSizeUnit(size: Long): String {
        return if (size >= 1024 * 1024) "%.1fMB".format(size / 1024f / 1024f)
        else if (size >= 1024) "%.1fKB".format(size / 1024f)
        else "${size}B"
    }

    fun sharePhoto(uri: Uri) {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_SEND
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        App.act.startActivity(intent)
    }

    fun delete(uri: Uri) {
        App.act.contentResolver.delete(uri, null, null)
    }

    fun exist(id: Long): Boolean {
        App.act.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            null,
            "${MediaStore.Images.Media._ID} = ?",
            arrayOf("$id"),
            null
        )?.use { cursor ->
            return cursor.count > 0
        }
        return false
    }

    @Suppress("DEPRECATION")
    fun uri2Bitmap(uri: Uri): Bitmap? {
        val context = App.act
        kotlin.runCatching {
            return if (Build.VERSION.SDK_INT >= 28) {
                val source = ImageDecoder.createSource(context.contentResolver, uri)
                ImageDecoder.decodeBitmap(source).copy(Bitmap.Config.ARGB_8888, true)
            } else {
                MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
            }
        }
        return null
    }

    fun saveBitmap(bitmap: Bitmap): Long {
        val context = App.act
        val fileName = "${System.currentTimeMillis()}.jpg"
        if (Build.VERSION.SDK_INT >= 30) {
            val resolver = context.contentResolver
            val collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            val details = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put(MediaStore.Images.Media.RELATIVE_PATH, relativeSavePath)
            }
            val uri = resolver.insert(collection, details)
            if (uri != null) {
                resolver.openOutputStream(uri).use {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
                }
                return ContentUris.parseId(uri)
            }
        } else {
            val imagesDir = absoluteSavePath
            File(imagesDir).let { if (!it.exists()) it.mkdirs() }
            val image = File(imagesDir, fileName)
            FileOutputStream(image).use {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            }
            MediaScannerConnection.scanFile(context, arrayOf(image.absolutePath), null, null)
            return ContentUris.parseId(image.toUri())
        }
        return 0
    }

}