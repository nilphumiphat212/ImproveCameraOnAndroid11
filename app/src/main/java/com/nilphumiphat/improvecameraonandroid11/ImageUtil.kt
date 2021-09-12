package com.nilphumiphat.improvecameraonandroid11

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.os.SystemClock
import android.provider.MediaStore
import java.io.File
import java.io.File.separator
import java.io.FileOutputStream
import java.io.OutputStream

fun Bitmap.saveImage(context: Context): Uri? {
    if (android.os.Build.VERSION.SDK_INT >= 29) {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000)
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
        values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/test_pictures")
        values.put(MediaStore.Images.Media.IS_PENDING, true)
        values.put(MediaStore.Images.Media.DISPLAY_NAME, "img_${SystemClock.uptimeMillis()}")

        val uri: Uri? =
            context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        if (uri != null) {
            saveImageToStream(this, context.contentResolver.openOutputStream(uri))
            values.put(MediaStore.Images.Media.IS_PENDING, false)
            context.contentResolver.update(uri, values, null, null)
            return uri
        }
    } else {
        val directory =
            File(
                context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                    .toString() + separator + "ContentValues"
            )
        if (!directory.exists()) {
            directory.mkdirs()
        }
        val fileName = "img_${SystemClock.uptimeMillis()}" + ".jpeg"
        val file = File(directory, fileName)
        saveImageToStream(this, FileOutputStream(file))
        if (file.absolutePath != null) {
            val values = ContentValues()
            values.put(MediaStore.Images.Media.DATA, file.absolutePath)
            // .DATA is deprecated in API 29
            context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            return Uri.fromFile(file)
        }
    }
    return null
}


fun saveImageToStream(bitmap: Bitmap, outputStream: OutputStream?) {
    if (outputStream != null) {
        try {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}