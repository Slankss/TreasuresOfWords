package com.example.treaasuresofwords.View.LoginAndRegister.Login

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.treaasuresofwords.R
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
                    isSuccesfull.value = true
                }
            }
        }.addOnFailureListener {  exception ->

            val errorMessage = when(exception.localizedMessage){
                mContext.getString(R.string.f_e_user_not_found) -> mContext.getString(R.string.user_not_found)
                mContext.getString(R.string.f_e_wrong_password) -> mContext.getString(R.string.wrong_password)
                else -> {
                    exception.localizedMessage
                }
            }

            Toast.makeText(mContext,errorMessage,Toast.LENGTH_SHORT).show()

        }

    }


}