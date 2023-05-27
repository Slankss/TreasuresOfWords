package com.okankkl.treasuresofwords.View.LoginAndRegister

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.okankkl.treasuresofwords.R

class LoginAndRegisterActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_and_register)

        try {
            this.supportActionBar!!.hide()
        } catch (e: NullPointerException) {
        }


    }


}