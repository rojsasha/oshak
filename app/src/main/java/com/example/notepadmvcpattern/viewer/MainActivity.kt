package com.example.notepadmvcpattern.viewer

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.ui.AppBarConfiguration
import com.example.notepadmvcpattern.R
import com.example.notepadmvcpattern.controller.Controller
import com.example.notepadmvcpattern.utils.GetFileActivityResultContract
import com.example.notepadmvcpattern.utils.PermissionUtil
import com.example.notepadmvcpattern.utils.PermissionUtil.LOCATION_REQUEST_CODE
import com.example.notepadmvcpattern.utils.SaveFileActivityResultContract
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import java.io.*


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var appBarMain: androidx.appcompat.widget.Toolbar
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var titleName: EditText
    private lateinit var bottomNav: BottomNavigationView
    private var controller: Controller
    private var uri: Uri? = null
    private var pasteData: String = ""


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


    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        appBarMain = findViewById(R.id.toolbar)
        titleName = findViewById(R.id.titleName)
        drawerLayout = findViewById(R.id.drawer_layout)
        bottomNav = findViewById(R.id.bottomNav)
        setupFrameLayot()
        if (PermissionUtil.checkPermisssion(this))

            setSupportActionBar(appBarMain)
        appBarMain.title = "Меню"

        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            appBarMain,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        appBarConfiguration = AppBarConfiguration(
            setOf(
            ), drawerLayout
        )
        val navigationView: NavigationView =
            findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)


    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }

    }

    private fun setupFrameLayot() {
        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_text_size -> {
                    TextSizeDialog.newInstance(controller.getTextSize())
                        .show(supportFragmentManager, "textSize")
                }
                R.id.nav_text_color -> {
                    TextColorDialog.newInstance(controller.getTextColor())
                        .show(supportFragmentManager, "textColor")
                }
                R.id.nav_copy -> {
                    copyTextToClipboard()
                }
                R.id.nav_paste -> {
                    pasteTextFromClipboard()
                }
            }
            true
        }
    }

    private fun pasteTextFromClipboard() {
        val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        titleName.setText(clipboardManager.primaryClip?.getItemAt(0)?.text)
    }

    private fun copyTextToClipboard() {
        val textToCopy = titleName.text

        val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("text", textToCopy)
        clipboardManager.setPrimaryClip(clipData)

        Toast.makeText(this, "Text copied to clipboard", Toast.LENGTH_LONG).show()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val id: Int = item.itemId
        when (id) {
            R.id.nav_cart -> {
                startActivity(
                    Intent(this, MainActivity::class.java)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                )
            }
            R.id.open -> {
                getDocumentResult.launch(false)
            }
            R.id.save -> {
                uri?.let { alterDocument(it) }
            }
            R.id.download -> {
                saveDocumentResult.launch(false)
            }
            R.id.print -> {
            }
            R.id.info -> {
            }
            R.id.power -> {
                finish()
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return false
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
            Toast.makeText(this, "Сохранено", Toast.LENGTH_SHORT).show()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun setTextColor(color: ColorsType) {
        controller.setTextColor(color)
        titleName.setTextColor(resources.getColor(color.value))
    }

    fun setTextSize(textSize: Int) {
        controller.setTextSize(textSize)
        titleName.textSize = textSize.toFloat()
    }
}