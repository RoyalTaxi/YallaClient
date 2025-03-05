package uz.yalla.client.core.common.system

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


suspend fun Context.isFileSizeTooLarge(
    uri: Uri,
    maxFileSizeInBytes: Long
): Boolean {
    return withContext(Dispatchers.IO) {
        val size = contentResolver.openAssetFileDescriptor(uri, "r")?.use { it.length } ?: 0
        return@withContext size > maxFileSizeInBytes
    }
}

suspend fun Context.isImageDimensionTooLarge(
    uri: Uri,
    maxWidth: Int,
    maxHeight: Int
): Boolean {
    return withContext(Dispatchers.IO) {
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        contentResolver.openInputStream(uri)?.use { stream ->
            BitmapFactory.decodeStream(stream, null, options)
        }
        val (width, height) = options.outWidth to options.outHeight
        return@withContext (width > maxWidth || height > maxHeight)
    }
}
