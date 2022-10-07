package com.example.notepadmvcpattern.utils

import android.content.ContentResolver
import android.net.Uri
import android.widget.EditText
import java.io.*

object FileUtils {

    fun getUri(uris: String?, contentResolver: ContentResolver, titleName: EditText) {
        var inputStream: InputStream? = null
        var str = ""
        val buf = StringBuffer()
        try {
            inputStream = contentResolver.openInputStream(Uri.parse(uris))
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        val reader = BufferedReader(InputStreamReader(inputStream))
        if (inputStream != null) {
            try {
                while (reader.readLine().also { str = it } != null) {
                    buf.append(
                        """
                    $str
                    
                    """.trimIndent()
                    )
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
            try {
                inputStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            titleName.setText(buf.toString())
        }
    }
}