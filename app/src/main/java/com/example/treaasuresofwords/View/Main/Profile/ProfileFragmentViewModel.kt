package com.example.treaasuresofwords.View.Main.Profile

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.treaasuresofwords.Model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore

class ProfileFragmentViewModel(var auth : FirebaseAuth,var db : FirebaseFirestore) : ViewModel() {

    var user = MutableLiveData<User>()
    var currentUser : FirebaseUser? = null
    var isSuccesfull = MutableLiveData<Boolean>()
    var isComplete = MutableLiveData<Boolean>()


    init {
        isSuccesfull.value = false
        isComplete.value = false
        getCurrentUser()
    }

    fun getCurrentUser(){

        currentUser = auth.currentUser
        currentUser?.let { current->
            val uid = current.uid
            db.collection("User").document(uid).get().addOnCompleteListener { completeTask ->
                if(completeTask.isComplete){
                    if(completeTask.isSuccessful){
                        var document = completeTask.result
                        var username = document.getString("username") as String
                        var email = document.getString("email") as String
                        var number = document.getString("number") as String
                        var selectedLanguageState = document.getBoolean("selectedLanguageState") as Boolean
                        var pageLanguage = document.getString("pageLanguage") as String

                        user.value = User(username,email,number,selectedLanguageState,pageLanguage)
                    }
                }
            }.addOnFailureListener { exception ->

                Log.e("errorMsg",exception.localizedMessage)
            }
        }
    }

    fun updateProfile(user : User){

        currentUser?.let { current ->
            //it.updateProfile()
            val uid = current.uid

            val profileUpdates = userProfileChangeRequest {
                displayName = user.username
            }

            current.updateProfile(profileUpdates).addOnCompleteListener { updateProfileTask ->
                if(updateProfileTask.isSuccessful){
                    db.collection("User").document(uid).update("username",user.username,
                        "number",user.number).addOnCompleteListener { completeTask ->

                        if(completeTask.isComplete){
                            isComplete.value = true
                            if(completeTask.isSuccessful){
                                isSuccesfull.value = true
                            }
                        }

                    }
                }
            }


        }

    }

}