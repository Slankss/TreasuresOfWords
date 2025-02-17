package com.okankkl.treasuresofwords.View

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.okankkl.treasuresofwords.View.LoginAndRegister.LoginAndRegisterActivity
import com.okankkl.treasuresofwords.View.Main.MainActivity
import com.okankkl.treasuresofwords.databinding.ActivitySplashBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class SplashActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySplashBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val currentUser = auth.currentUser

        try {
            this.supportActionBar!!.hide()
        } catch (e: NullPointerException) {
        }



        getSharedPreferences("User_Local_Data", Context.MODE_PRIVATE)
            ?.let {
                val current_language = it.getString("current_language","en")
                current_language?.let { current ->
                    changeLanguage(current)
                }
            }



        try {
            if(currentUser != null){

                if(currentUser.isEmailVerified){
                    goPage( Intent(applicationContext, MainActivity::class.java))
                }
                else{
                    goPage(Intent(applicationContext, LoginAndRegisterActivity::class.java))
                }
            }
            else{
                goPage(Intent(applicationContext, LoginAndRegisterActivity::class.java))
            }
        }
        catch (e : Exception){
            goPage(Intent(applicationContext, LoginAndRegisterActivity::class.java))
        }

    }

    fun goPage(intent : Intent){
        Handler().postDelayed({
            startActivity(intent)
            finish()
        } , 1000
        )
    }

    fun changeLanguage(language : String){

        // ingilizce -> en
        // türkçe -> tr
        val locale = Locale(language)
        Locale.setDefault(locale)
        val configuration = Configuration()
        configuration.locale = locale
        baseContext.resources.updateConfiguration(configuration,applicationContext.resources.displayMetrics)

        val preferences = getSharedPreferences("User_Local_Data",Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putString("current_language",language)
        editor.apply()


    }

}