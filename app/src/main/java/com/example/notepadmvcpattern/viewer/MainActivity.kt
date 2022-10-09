package com.example.notepadmvcpattern.viewer

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.view.MenuItem
import android.widget.EditText
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.ui.AppBarConfiguration
import com.example.notepadmvcpattern.controller.Controller
import com.example.notepadmvcpattern.utils.GetFileActivityResultContract
import com.example.notepadmvcpattern.utils.PermissionUtil
import com.example.notepadmvcpattern.utils.PermissionUtil.LOCATION_REQUEST_CODE
import com.example.notepadmvcpattern.utils.SaveFileActivityResultContract
import com.google.android.material.navigation.NavigationView
import java.io.*


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var appBarMain: androidx.appcompat.widget.Toolbar
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var titleName: EditText
    private var controller: Controller
    private var uri: Uri? = null

    val CREATE_FILE = 1


    private val getDocumentResult = registerForActivityResult(GetFileActivityResultContract()) {
//        Toast.makeText(this, it.size,Toast.LENGTH_SHORT).show()
        uri = it.firstOrNull()
        val text = readTextFromUri(it.first())
        setTextToEdit(text)
    }

    private val saveDocumentResult = registerForActivityResult(SaveFileActivityResultContract()) {
        uri = it.firstOrNull()
        uri?.let { it1 -> alterDocument(it1) }
    }

    init {
        controller = Controller(viewer = this)
    }

    private fun setTextToEdit(text: String) {
        titleName.setText(text)
    }

    @Throws(IOException::class)
    private fun readTextFromUri(uri: Uri): String {
        val stringBuilder = StringBuilder()
        contentResolver.openInputStream(uri)?.use { inputStream ->
            BufferedReader(InputStreamReader(inputStream)).use { reader ->
                var line: String? = reader.readLine()
                while (line != null) {
                    stringBuilder.append(line)
                    line = reader.readLine()
                }
            }
        }
        return stringBuilder.toString()
    }

//    private fun createFile(pickerInitialUri: Uri) {
//        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
//            addCategory(Intent.CATEGORY_OPENABLE)
//            type = "application/txt"
//            putExtra(Intent.EXTRA_TITLE, "invoice.txt")
//
//            // Optionally, specify a URI for the directory that should be opened in
//            // the system file picker before your app creates the document.
//            putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri)
//        }
//        startActivityForResult(intent, CREATE_FILE)
//    }

    private fun alterDocument(uri: Uri) {
        try {
            val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            contentResolver.takePersistableUriPermission(uri, takeFlags)
            contentResolver.openFileDescriptor(uri, "rwt")?.use {
                FileOutputStream(it.fileDescriptor).use { file ->
                    file.write(
                        (titleName.text.toString()).toByteArray()
                    )

                }
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.notepadmvcpattern.R.layout.activity_main)
        appBarMain = findViewById(com.example.notepadmvcpattern.R.id.toolbar)
        titleName = findViewById(com.example.notepadmvcpattern.R.id.titleName)
        drawerLayout = findViewById(com.example.notepadmvcpattern.R.id.drawer_layout)

        if (PermissionUtil.checkPermisssion(this))

            setSupportActionBar(appBarMain)
        appBarMain.title = "Меню"

        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            appBarMain,
            com.example.notepadmvcpattern.R.string.navigation_drawer_open,
            com.example.notepadmvcpattern.R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        appBarConfiguration = AppBarConfiguration(
            setOf(
            ), drawerLayout
        )
        val navigationView: NavigationView =
            findViewById(com.example.notepadmvcpattern.R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)


    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED
            ) {

            }
        }
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val id: Int = item.itemId
        when (id) {
            com.example.notepadmvcpattern.R.id.nav_cart -> {

            }
            com.example.notepadmvcpattern.R.id.open -> {
                getDocumentResult.launch(false)
            }
            com.example.notepadmvcpattern.R.id.save -> {
                uri?.let { alterDocument(it) }
            }
            com.example.notepadmvcpattern.R.id.download -> {
                saveDocumentResult.launch(false)
            }
            com.example.notepadmvcpattern.R.id.print -> {
                controller
            }
            com.example.notepadmvcpattern.R.id.info -> {
                controller
            }
            com.example.notepadmvcpattern.R.id.power -> {
                controller
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return false
    }
}