package com.example.treaasuresofwords.View.LoginAndRegister.Verification

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.example.treaasuresofwords.R
import com.example.treaasuresofwords.View.LoginAndRegister.LoginAndRegisterActivity
import com.example.treaasuresofwords.View.SelectLangues.SelectLanguesActivity
import com.example.treaasuresofwords.databinding.ActivityVerificationBinding
import com.google.firebase.auth.FirebaseAuth
import com.muhammed.toastoy.Toastoy
import java.time.LocalDateTime


class VerificationActivity : AppCompatActivity()  {

    private lateinit var binding : ActivityVerificationBinding
    private lateinit var auth : FirebaseAuth

    var send_time = 60
    var millisInFuture = 60000

    val timer = object: CountDownTimer(millisInFuture.toLong(), 1000) {
        override fun onTick(millisUntilFinished: Long)
        {
            val text_time = send_time.toString() + "s " +getString(R.string.send_verification_message_time)
            setEnabled(text_time,false)
            if(send_time > 0 ) { send_time-- }
        }

        override fun onFinish() {
            setEnabled("",true)
        }


    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        checkVerified(auth)

        binding.btnSignInDifferentLanguage.setOnClickListener {
            auth.signOut()
            startActivity(Intent(applicationContext,LoginAndRegisterActivity::class.java))
            finish()
        }


        binding.btnSendVerificationMail.setOnClickListener {
            if(send_time == 0)
            {
                currentUser?.let { user->
                    if(!user.isEmailVerified){
                        user.sendEmailVerification().addOnCompleteListener { task ->
                            if(task.isSuccessful){
                                send_time = 60
                                timer.start()
                                binding.btnSendVerificationMail.visibility = View.INVISIBLE
                                Toastoy.showInfoToast(this,getString(R.string.email_send_message))
                            }
                        }.addOnFailureListener { exception ->
                            Log.e("errorMsg",exception.localizedMessage)
                        }
                    }
                }
            }
        }

        val localDate = LocalDateTime.now()
        val day_total_second = localDate.hour*3600 + localDate.minute*60 + localDate.second

        val preferences = getSharedPreferences("User_Verification_Time", Context.MODE_PRIVATE)
        if(preferences != null)
        {
            val total_time = preferences.getInt("total_second", 0)
            val difference = day_total_second - total_time
            if(difference > 60){
                send_time = 60
                millisInFuture = 60
                binding.btnSendVerificationMail.visibility = View.INVISIBLE
                currentUser?.sendEmailVerification()

                val editor = preferences.edit()
                editor.putInt("total_second",day_total_second)
                editor.apply()
                timer.start()
            }
            else{
                send_time -= difference
                millisInFuture -= difference
                timer.start()
            }
        }
        else{
            val new_preferences = getSharedPreferences("User_Verification_Time", Context.MODE_PRIVATE)
            val total_time = new_preferences.getInt("total_second", 0)
            val editor = new_preferences.edit()
            editor.putInt("total_second",day_total_second)
            editor.apply()

        }

    }

    fun checkVerified(auth : FirebaseAuth){

        val timer = object: CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long)
            {
                val currentUser = auth.currentUser
                currentUser?.let {
                    it.reload()
                    if(it.isEmailVerified){
                        Toastoy.showSuccessToast(applicationContext,getString(R.string.verification_succesfull))
                        startActivity(Intent(applicationContext,SelectLanguesActivity::class.java))
                        finish()
                        cancel()

                    }

                }
            }

            override fun onFinish() {
                start()
            }
        }
        timer.start()

    }

    fun setEnabled(time_text : String,state : Boolean){

        if(send_time <= 0) {
            timer.cancel()
            binding.txtSendTime.setText("")
            binding.btnSendVerificationMail.visibility = View.VISIBLE

            send_time = 0
        }
        else{
            binding.txtSendTime.setText(time_text)
            binding.btnSendVerificationMail.visibility = when(state){
                true -> View.VISIBLE
                else -> {
                    View.INVISIBLE
                }
            }

        }



    }

    override fun onDestroy() {
        super.onDestroy()

        val currentUser = auth.currentUser
        currentUser?.let {
            if(!it.isEmailVerified)    {
                auth.signOut()
            }

        }

    }


}