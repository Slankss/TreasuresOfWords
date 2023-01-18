package com.example.treaasuresofwords.View.LoginAndRegister.Verification

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.treaasuresofwords.R
import com.example.treaasuresofwords.View.SelectLangues.SelectLanguesActivity
import com.example.treaasuresofwords.databinding.ActivityVerificationBinding
import com.google.firebase.auth.FirebaseAuth


class VerificationActivity : AppCompatActivity()  {

    private lateinit var binding : ActivityVerificationBinding
    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        var currentUser = auth.currentUser

        checkVerified(auth)
        var send_time = 60
        var millisInFuture = 60000

        val timer = object: CountDownTimer(millisInFuture.toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long)
            {
                var text_time = send_time.toString() + "s " +getString(R.string.send_verification_message_time)
                binding.txtSendTime.setText(text_time)
                send_time--
            }

            override fun onFinish() {
                binding.txtSendTime.setText("")
                binding.btnSendVerificationMail.isClickable = true
                binding.btnSendVerificationMail.isEnabled = true
            }
        }

        timer.start()
        binding.btnSendVerificationMail.isClickable = false
        binding.btnSendVerificationMail.isEnabled = false

        binding.btnSendVerificationMail.setOnClickListener {
            if(send_time == 0)
            {
                currentUser?.let {
                    if(!it.isEmailVerified){
                        it.sendEmailVerification()

                        send_time = 60
                        timer.start()
                        binding.btnSendVerificationMail.isClickable = false
                        binding.btnSendVerificationMail.isEnabled = false

                    }

                }

            }
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
                        Toast.makeText(applicationContext,getString(R.string.verification_succesfull),Toast.LENGTH_SHORT)
                            .show()
                        startActivity(Intent(applicationContext,SelectLanguesActivity::class.java))
                        cancel()
                        finish()
                    }

                }
            }

            override fun onFinish() {
                start()
            }
        }
        timer.start()

    }

    override fun onDestroy() {
        super.onDestroy()

        val currentUser = auth.currentUser
        currentUser?.let {
            if(it.isEmailVerified == false)    {
                auth.signOut()
            }

        }
    }


}