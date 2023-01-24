package com.example.treaasuresofwords.View.Main.Word

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.treaasuresofwords.Model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class WordFragmentViewModel(var auth : FirebaseAuth,var db : FirebaseFirestore) : ViewModel() {

    //var wordList = MutableList<ArrayList<Word>>()
    var userProfile = MutableLiveData<User>()

    init {
        getCurrentUser()
    }

    fun getCurrentUser(){

        val currentUser = auth.currentUser

        currentUser?.let {
            var uid = it.uid
            db.collection("User").document(uid).get().addOnCompleteListener { task ->
                if(task.isSuccessful){
                    val document = task.result
                    if(document != null ){
                        val username = document.getString("username") as String
                        val email = document.getString("email") as String
                        val number = document.getString("number") as String
                        val selectedLanguageState = document.getBoolean("selectedLanguageState") as Boolean
                        val pageLanguage = document.getString("pageLanguage") as String
                        val languages = document.get("languages") as ArrayList<HashMap<String,Any>>

                        val user = User(username,email,number,selectedLanguageState,pageLanguage,languages)

                        userProfile.value = user
                    }

                }
            }.addOnFailureListener { exception ->
                Log.e("errorMsg",exception.localizedMessage)
            }
        }

    }



}