package com.example.treaasuresofwords.View

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.example.treaasuresofwords.R
import com.example.treaasuresofwords.View.LoginAndRegister.LoginAndRegisterActivity
import com.example.treaasuresofwords.View.LoginAndRegister.Verification.VerificationActivity
import com.example.treaasuresofwords.View.Main.MainActivity
import com.example.treaasuresofwords.View.SelectLangues.SelectLanguesActivity
import com.example.treaasuresofwords.databinding.ActivitySelectLanguesBinding
import com.example.treaasuresofwords.databinding.ActivitySplashBinding
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
                    val uid = currentUser.uid
                    db.collection("User").document(uid).get().addOnCompleteListener { task ->
                        if(task.isSuccessful){
                            val item = task.result

                            if(item != null){
                                val languages = item.get("languages") as ArrayList<*>
                                if(languages.isEmpty()){
                                    goPage(Intent(applicationContext, SelectLanguesActivity::class.java))
                                }
                                else{
                                    goPage( Intent(applicationContext, MainActivity::class.java))
                                }
                            }
                        }
                    }
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