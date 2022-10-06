package com.example.notepadmvcpattern.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContract

class GetFileActivityResultContract: ActivityResultContract<Boolean, List<Uri>>() {

    override fun createIntent(context: Context, input: Boolean): Intent {
        return Intent(Intent.ACTION_GET_CONTENT)
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