package com.example.notepadmvcpattern.viewer

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MenuItem
import android.webkit.WebChromeClient.FileChooserParams
import android.widget.EditText
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
import com.google.android.material.navigation.NavigationView


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var appBarMain: androidx.appcompat.widget.Toolbar
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var titleName: EditText
    private var controller: Controller
    private lateinit var chooser: FileChooserParams
    private val result = registerForActivityResult(GetFileActivityResultContract()){}

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
        val navigationView: NavigationView = findViewById(R.id.nav_view)
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
            R.id.nav_cart -> {

            }
            R.id.open -> {
                result.launch(false)
            }
            R.id.save -> {

            }
            R.id.download -> {
                controller
            }
            R.id.print -> {
                controller
            }
            R.id.info -> {
                controller
            }
            R.id.power -> {
                controller
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return false
    }
}