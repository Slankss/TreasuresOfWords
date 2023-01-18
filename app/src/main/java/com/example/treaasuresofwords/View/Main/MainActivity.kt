package com.example.treaasuresofwords.View.Main

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
        R.id.quizFragment,
        R.id.profileFragment
    )
    private val childFragmentMenuList = arrayOf(0,1,1)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        NavigationView = findViewById(R.id.adminBottomNavigationView)
        navController = findNavController(R.id.nav_host_fragment)
        NavigationView.setupWithNavController(navController)

        var appBarConfiguration  = AppBarConfiguration(
            setOf(
                R.id.homeFragment,
                R.id.wordFragment,
                R.id.quizFragment,
                R.id.profileFragment
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

        navController.previousBackStackEntry?.let { it ->
            val destinationFragment = it.destination.id
            if(childFragmentList.contains(destinationFragment)){
                NavigationView.menu[childFragmentMenuList[childFragmentList.indexOf(destinationFragment)]].isChecked = true
            }
        }

        super.onBackPressed()
    }

}