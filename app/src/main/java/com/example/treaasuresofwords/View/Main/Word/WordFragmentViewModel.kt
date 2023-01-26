package com.example.treaasuresofwords.View.Main.Word

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.treaasuresofwords.Model.User
import com.example.treaasuresofwords.Model.Word
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class WordFragmentViewModel(var auth : FirebaseAuth,var db : FirebaseFirestore) : ViewModel() {

    var wordList = MutableLiveData<ArrayList<Word>>()
    var userProfile = MutableLiveData<User>()
    var currentUser : FirebaseUser? = null

    init {
        getCurrentUser()
    }

    fun getCurrentUser(){

        currentUser = auth.currentUser

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

                        getWordList()
                    }

                }
            }.addOnFailureListener { exception ->
                Log.e("errorMsg",exception.localizedMessage)
            }
        }

    }

    fun getWordList(){

        if(currentUser != null){

            val uid = currentUser!!.uid
            db.collection("Word").document(uid).addSnapshotListener { value, error ->
                if(error != null){
                    Log.e("errorMsg",error.localizedMessage)
                }
                else{
                    val document = value
                    val wordListArray = arrayListOf<Word>()
                    if(document != null){
                        val wordListInDb = document.get("wordList") as ArrayList<HashMap<String,Any>>
                        Log.w("aaa",wordListInDb.toString())
                        wordListInDb.forEach {
                            val languageToTranslated = it["languageToTranslated"].toString()
                            val translatedLanguage = it["translatedLanguage"].toString()
                            val word_name = it["word"].toString()
                            val translate = it["translate"].toString()
                            val repeatTime = it["repeatTime"].toString().toInt()
                            val word = Word(languageToTranslated,translatedLanguage,word_name,translate,repeatTime)
                            wordListArray.add(word)
                        }

                    }
                    wordList.value = wordListArray
                }
            }

        }

    }



}