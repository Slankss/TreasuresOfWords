package com.okankkl.treasuresofwords.View.LoginAndRegister.Login

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.okankkl.treasuresofwords.R
import com.google.firebase.auth.FirebaseAuth
import com.muhammed.toastoy.Toastoy

class LoginViewModel(var auth : FirebaseAuth,var mContext : Context) : ViewModel() {

    var isSuccesfull = MutableLiveData<Boolean>()
    var isComplete = MutableLiveData<Boolean>()

    init {
        isSuccesfull.value = false
        isComplete.value = false
    }

    fun login(email : String,password : String){


        isComplete.value = false
        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener { task ->
            if(task.isSuccessful){
                val currentUser = auth.currentUser

                currentUser?.let {
                    isSuccesfull.value = true
                    isComplete.value = true
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
            isComplete.value = true
            Toastoy.showErrorToast(mContext,errorMessage)

        }

    }


}