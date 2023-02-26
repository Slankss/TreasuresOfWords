package com.example.treaasuresofwords.View.Main

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.MenuItem
import androidx.annotation.StringRes
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.get
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.treaasuresofwords.R
import com.example.treaasuresofwords.View.LoginAndRegister.LoginAndRegisterActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var NavigationView : BottomNavigationView

    private val childFragmentList = arrayOf(
        R.id.homeFragment,
        R.id.wordFragment,
        R.id.quizFragment
    )
    private val childFragmentMenuList = arrayOf(0,1,1)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        try {
            this.supportActionBar!!.hide()
        } catch (e: NullPointerException) {
        }

        NavigationView = findViewById(R.id.adminBottomNavigationView)
        navController = findNavController(R.id.nav_host_fragment)
        NavigationView.setupWithNavController(navController)

        var appBarConfiguration  = AppBarConfiguration(
            setOf(
                R.id.homeFragment,
                R.id.wordFragment,
                R.id.quizFragment
            )
        )
        setupActionBarWithNavController(navController,appBarConfiguration)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            android.R.id.home ->{
                navController.navigateUp()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {

        val previous = navController.previousBackStackEntry
        if(previous != null){

            val destinationFragment = previous.destination.id
            if(childFragmentList.contains(destinationFragment)){
                NavigationView.menu[childFragmentMenuList[childFragmentList.indexOf(destinationFragment)]].isChecked = true
            }
            else{
            }
            super.onBackPressed()
        }
        else{
            val dialogBuilder = AlertDialog.Builder(this)
            dialogBuilder.setMessage(R.string.app_leave_out_message)
                .setPositiveButton(R.string.yes, DialogInterface.OnClickListener { dialog, id ->
                    finish()
                })
                .setNegativeButton(R.string.no, DialogInterface.OnClickListener {
                        dialog, id -> dialog.cancel()
                })
            val alert = dialogBuilder.create()
            alert.setTitle(R.string.log_out)
            alert.show()

        }

    }



}