package com.example.treaasuresofwords.View.LoginAndRegister.Register

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.treaasuresofwords.Model.User
import com.example.treaasuresofwords.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterViewModel(val auth : FirebaseAuth,val db : FirebaseFirestore,val mContext : Context) : ViewModel() {

    var is_succesfull = MutableLiveData<Boolean>()

    init {
        is_succesfull.value = false
    }


    // EthhskxDhOVSiPZA8KXBUiOgwln1
    // EthhskxDhOVSiPZA8KXBUiOgwln1

    fun register(user : User,password : String){

        auth.createUserWithEmailAndPassword(user.email,password).addOnCompleteListener { authTask ->
            if(authTask.isSuccessful){

                val currentUser = auth.currentUser
                currentUser?.let {
                    val user_id = it.uid

                    db.collection("User").document(user_id).set(user).addOnCompleteListener { profile_task ->
                        if(profile_task.isSuccessful){
                            it.sendEmailVerification()
                            Toast.makeText(mContext,mContext.getString(R.string.create_user_message),Toast.LENGTH_LONG).show()
                            is_succesfull.value = true
                        }
                    }

                }


            }
        }

    }


}