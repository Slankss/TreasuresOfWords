package com.example.treaasuresofwords.View.Main

import android.Manifest
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.treaasuresofwords.R
import com.example.treaasuresofwords.View.Main.WorkManager.NotificationListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var NavigationView : BottomNavigationView
    private var PERMISSION_NOTIFICATİON = Manifest.permission.POST_NOTIFICATIONS


    val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.UNMETERED)
        .setRequiresCharging(true)
        .build()

    val data = Data.Builder().putInt("intKey",1).build()

    private val childFragmentList = arrayOf(
        R.id.homeFragment,
        R.id.wordFragment,
        R.id.quizFragment
    )
    private val childFragmentMenuList = arrayOf(0,1,1)

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        try {
            this.supportActionBar!!.hide()
        } catch (e: NullPointerException) {
        }

        saveVisitedTime()

        askForNotificationPermission()

        // Optional
        val workManager = WorkManager.getInstance(this)
        workManager.cancelAllWorkByTag("notificationWork")

        createWorkListener()


        createBottomMenu()

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun saveVisitedTime(){

        var preferences = getSharedPreferences("notification",Context.MODE_PRIVATE)
        val editor = preferences.edit()

        var localDate = LocalDateTime.now()
        var instant = localDate.atZone(ZoneId.systemDefault()).toInstant()
        var currentDate = Date.from(instant)

        editor.putString("usedDate",currentDate.toString())
        editor.apply()
    }


    fun askForNotificationPermission(){

        if(ContextCompat.checkSelfPermission(this,PERMISSION_NOTIFICATİON) != PackageManager.PERMISSION_GRANTED){
            // permission denied
            ActivityCompat.requestPermissions(this, arrayOf(PERMISSION_NOTIFICATİON),1)
        }
        else{
            // permission granted
        }

    }


    fun createWorkListener(){
        val myPeriodicWorkRequest : PeriodicWorkRequest =
            PeriodicWorkRequestBuilder<NotificationListener>(15,TimeUnit.MINUTES)
                //.setConstraints(constraints)
                .addTag("notificationWork")
                .build()

        WorkManager.getInstance(this,).enqueue(myPeriodicWorkRequest)
    }

    fun createBottomMenu(){
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

    override fun onStop() {
        super.onStop()

        if(isFinishing){
            //createWorkListener()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == 1){
            if(grantResults.size > 0){
                if(ContextCompat.checkSelfPermission(this,PERMISSION_NOTIFICATİON) == PackageManager.PERMISSION_GRANTED){

                }
            }
        }

    }

}