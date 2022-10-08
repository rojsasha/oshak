package com.example.notepadmvcpattern.utils

import android.app.Activity
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import androidx.activity.result.contract.ActivityResultContract
import androidx.annotation.Nullable
import java.io.*

class GetFileActivityResultContract: ActivityResultContract<Boolean, List<Uri>>() {

    override fun createIntent(context: Context, input: Boolean): Intent {
        return Intent(Intent.ACTION_OPEN_DOCUMENT)
            .addCategory(Intent.CATEGORY_OPENABLE)
            .setType("*/*")
            .putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): List<Uri> {
        return if (intent == null || resultCode != Activity.RESULT_OK) {
            emptyList()
        } else getClipDataUris(intent)
    }

}

fun getClipDataUris(intent: Intent): List<Uri> {
    val resultSet = HashSet<Uri>()
    intent.data?.let {
        resultSet.add(it)
    }

    val clipData = intent.clipData
    if (clipData == null && resultSet.isEmpty()) {
        return emptyList<Uri>()
    } else if (clipData != null) {
        for (i in 0 until clipData.itemCount) {
            val uri = clipData.getItemAt(i).uri
            if (uri != null) {
                resultSet.add(uri)
            }
        }
    }
    return ArrayList(resultSet)

}

fun getPath(context: Context, uri: Uri): String? {

    if (DocumentsContract.isDocumentUri(context, uri)) {
        if (isExternalStorageDocument(uri)) {
            val docId = DocumentsContract.getDocumentId(uri)
            val split = docId.split(":".toRegex()).toTypedArray()
            val type = split[0]
            if ("primary".equals(type, ignoreCase = true)) {
                return Environment.getExternalStorageDirectory()
                    .toString() + "/" + split[1]
            }
        } else if (isDownloadsDocument(uri)) {
            val id = DocumentsContract.getDocumentId(uri)
            if (id != null && id.startsWith("raw:")) {
                return id.substring(4)
            }
            val contentUriPrefixesToTry = arrayOf(
                "content://downloads/public_downloads",
                "content://downloads/my_downloads"
            )
            for (contentUriPrefix in contentUriPrefixesToTry) {
                val contentUri: Uri
                if (id.startsWith("msf:")) {
                    val splitted = id.split(":")
                    contentUri = ContentUris.withAppendedId(
                        Uri.parse(contentUriPrefix),
                        splitted[1].toLong()
                    )
                } else {
                    contentUri = ContentUris.withAppendedId(
                        Uri.parse(contentUriPrefix),
                        id.toLong()
                    )
                }
                try {
                    val path = getDataColumn(context, contentUri, null, null)
                    if (path != null) {
                        return path
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            val fileName = getFileName(context, uri)
            val cacheDir = getDocumentCacheDir(context)
            val file = generateFileName(fileName, cacheDir)
            var destinationPath: String? = null
            if (file != null) {
                destinationPath = file.absolutePath
                saveFileFromUri(context, uri, destinationPath)
            }
            return destinationPath
        } else if (isMediaDocument(uri)) {
            val docId = DocumentsContract.getDocumentId(uri)
            val split = docId.split(":".toRegex()).toTypedArray()
            val type = split[0]
            var contentUri: Uri? = null
            if ("image" == type) {
                contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            } else if ("video" == type) {
                contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            } else if ("audio" == type) {
                contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            } else if ("document" == type) {
                return getDriveFilePath(uri, context)
            }
            val selection = "_id=?"
            val selectionArgs = arrayOf(
                split[1]
            )
            return getDataColumn(context, contentUri, selection, selectionArgs)
        } else if (isGoogleDriveUri(uri)) {
            return getDriveFilePath(uri, context)
        }
    } else if ("content".equals(uri.scheme, ignoreCase = true)) {


        return when {
            isFileProvider(uri) -> {
                return getDriveFilePath(uri, context)
            }
            isGooglePhotosUri(uri) -> {
                uri.lastPathSegment
            }
            isGoogleDriveUri(uri) -> getDriveFilePath(uri, context)
            else -> {
                getDataColumn(context, uri, null, null)
            }
        }
    } else if ("file".equals(uri.scheme, ignoreCase = true)) {
        return uri.path
    }
    return null
}
private fun isGoogleDriveUri(uri: Uri): Boolean {
    return "com.google.android.apps.docs.storage" == uri.authority || "com.google.android.apps.docs.storage.legacy" == uri.authority
}

fun isExternalStorageDocument(uri: Uri): Boolean {
    return "com.android.externalstorage.documents" == uri.authority
}

fun isFileProvider(uri: Uri): Boolean {
    return "com.luxoft.resident.fileProvider" == uri.authority ||
            "com.luxoft.executor.fileProvider" == uri.authority ||
            "com.luxoft.executor.fileProvider.preprod" == uri.authority ||
            "com.luxoft.resident.fileProvider.preprod" == uri.authority
}
fun isDownloadsDocument(uri: Uri): Boolean {
    return "com.android.providers.downloads.documents" == uri.authority
}
fun isMediaDocument(uri: Uri): Boolean {
    return "com.android.providers.media.documents" == uri.authority
}
fun isGooglePhotosUri(uri: Uri): Boolean {
    return "com.google.android.apps.photos.content" == uri.authority
}

fun getDataColumn(
    context: Context,
    uri: Uri?,
    selection: String?,
    selectionArgs: Array<String>?
): String? {
    var cursor: Cursor? = null
    val column = MediaStore.Files.FileColumns.DATA
    val projection = arrayOf(
        column
    )
    try {
        cursor = context.contentResolver.query(uri!!, projection, selection, selectionArgs, null)
        if (cursor != null && cursor.moveToFirst()) {
            val column_index = cursor.getColumnIndexOrThrow(column)
            return cursor.getString(column_index)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        cursor?.close()
    }
    return null
}
fun getFileName(context: Context, uri: Uri): String? {
    var filename: String? = null
    val returnCursor =
        context.contentResolver.query(uri, null, null, null, null)
    if (returnCursor != null) {
        val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        returnCursor.moveToFirst()
        filename = returnCursor.getString(nameIndex)
        returnCursor.close()
    }
    return filename
}

private fun getDriveFilePath(
    uri: Uri,
    context: Context
): String? {
    val returnCursor =
        context.contentResolver.query(uri, null, null, null, null)
    val nameIndex = returnCursor!!.getColumnIndex(OpenableColumns.DISPLAY_NAME)
    val sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE)
    returnCursor.moveToFirst()
    val name = returnCursor.getString(nameIndex)
    val size = returnCursor.getLong(sizeIndex).toString()
    val file = File(context.cacheDir, name)
    try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)
        var read = 0
        val maxBufferSize = 1 * 1024 * 1024
        val bytesAvailable = inputStream!!.available()


        val bufferSize = Math.min(bytesAvailable, maxBufferSize)
        val buffers = ByteArray(bufferSize)
        while (inputStream.read(buffers).also { read = it } != -1) {
            outputStream.write(buffers, 0, read)
        }
        inputStream.close()
        outputStream.close()
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return file.path
}

fun getDocumentCacheDir(context: Context): File {
    val dir = File(context.cacheDir, "document")
    if (!dir.exists()) {
        dir.mkdirs()
    }
    return dir
}
@Nullable
fun generateFileName(@Nullable name: String?, directory: File?): File? {
    var name = name ?: return null
    var file = File(directory, name)
    if (file.exists()) {
        var fileName = name
        var extension = ""
        val dotIndex = name.lastIndexOf('.')
        if (dotIndex > 0) {
            fileName = name.substring(0, dotIndex)
            extension = name.substring(dotIndex)
        }
        var index = 0
        while (file.exists()) {
            index++
            name = "$fileName($index)$extension"
            file = File(directory, name)
        }
    }
    try {
        if (!file.createNewFile()) {
            return null
        }
    } catch (e: IOException) {
        return null
    }
    return file
}

fun saveFileFromUri(
    context: Context,
    uri: Uri,
    destinationPath: String?
) {
    var `is`: InputStream? = null
    var bos: BufferedOutputStream? = null
    try {
        `is` = context.contentResolver.openInputStream(uri)
        bos = BufferedOutputStream(FileOutputStream(destinationPath, false))
        val buf = ByteArray(1024)
        `is`!!.read(buf)
        do {
            bos.write(buf)
        } while (`is`.read(buf) != -1)
    } catch (e: IOException) {
        e.printStackTrace()
    } finally {
        try {
            `is`?.close()
            if (bos != null) bos.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}