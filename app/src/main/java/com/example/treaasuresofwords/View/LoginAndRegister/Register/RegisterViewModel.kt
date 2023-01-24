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
import com.muhammed.toastoy.Toastoy

class RegisterViewModel(val auth : FirebaseAuth,val db : FirebaseFirestore,val mContext : Context) : ViewModel() {

    var is_succesfull = MutableLiveData<Boolean>()
    var is_complete = MutableLiveData<Boolean>()



    init {
        is_succesfull.value = false
        is_complete.value = false
    }


    fun register(user : User,password : String){

        is_complete.value = false
        auth.createUserWithEmailAndPassword(user.email,password).addOnCompleteListener { authTask ->
            if(authTask.isSuccessful){

                val currentUser = auth.currentUser
                currentUser?.let {
                    val user_id = it.uid

                    db.collection("User").document(user_id).set(user).addOnCompleteListener { profile_task ->
                        if(profile_task.isSuccessful){
                            Toastoy.showSuccessToast(mContext,mContext.getString(R.string.create_user_message))
                            is_succesfull.value = true
                            is_complete.value = true
                        }
                    }

                }


            }
        }.addOnFailureListener { exception ->

            is_complete.value = true

            val errorMessage = when(exception.localizedMessage){
                mContext.getString(R.string.f_e_email_already_in_use) -> mContext.getString(R.string.email_already_in_use)
                mContext.getString(R.string.f_e_weak_password) -> mContext.getString(R.string.weak_password)
                else -> {
                    exception.localizedMessage
                }
            }

            Toastoy.showErrorToast(mContext,errorMessage)

        }

    }


}