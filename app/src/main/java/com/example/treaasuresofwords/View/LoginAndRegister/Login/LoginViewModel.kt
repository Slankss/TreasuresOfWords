package com.example.treaasuresofwords.View.LoginAndRegister.Login

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class LoginViewModel(var auth : FirebaseAuth,var mContext : Context) : ViewModel() {

    var isSuccesfull = MutableLiveData<Boolean>()

    init {
        isSuccesfull.value = false
    }

    fun login(email : String,password : String){



        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener { task ->
            if(task.isSuccessful){
                val currentUser = auth.currentUser

                currentUser?.let {
                    if(it.isEmailVerified){
                        isSuccesfull.value = true
                    }
                    else{
                        auth.signOut()
                    }
                }
            }
        }

    }


}