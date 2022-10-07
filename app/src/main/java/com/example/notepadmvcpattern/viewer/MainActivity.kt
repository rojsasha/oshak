package com.example.notepadmvcpattern.viewer

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.EditText
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.ui.AppBarConfiguration
import com.example.notepadmvcpattern.controller.Controller
import com.example.notepadmvcpattern.utils.FileUtils.getUri
import com.example.notepadmvcpattern.utils.GetFileActivityResultContract
import com.example.notepadmvcpattern.utils.PermissionUtil
import com.example.notepadmvcpattern.utils.PermissionUtil.LOCATION_REQUEST_CODE
import com.example.notepadmvcpattern.utils.getPath
import com.google.android.material.navigation.NavigationView


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var appBarMain: androidx.appcompat.widget.Toolbar
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var titleName: EditText
    private var controller: Controller
    private val result = registerForActivityResult(GetFileActivityResultContract()){
//        Toast.makeText(this, it.size,Toast.LENGTH_SHORT).show()
        val uri = getPath(this,it[0])
        getUri(uri,contentResolver, titleName)
        Log.d("vvvvvvvv","nnnnnnn")
    }

    init {
        controller = Controller(viewer = this)
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
        val navigationView: NavigationView = findViewById(com.example.notepadmvcpattern.R.id.nav_view)
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
                result.launch(false)
            }
            com.example.notepadmvcpattern.R.id.save -> {

            }
            com.example.notepadmvcpattern.R.id.download -> {
                controller
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