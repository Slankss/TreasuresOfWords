package com.example.treaasuresofwords.View.Main.AddWord

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.treaasuresofwords.Model.User
import com.example.treaasuresofwords.Model.Word
import com.example.treaasuresofwords.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.muhammed.toastoy.Toastoy

class AddWordFragmentViewModel(var auth : FirebaseAuth,var db : FirebaseFirestore,var mContext : Context) : ViewModel() {

    var userProfile = MutableLiveData<User>()
    var currentUser : FirebaseUser? = null
    var isComplete = MutableLiveData<Boolean>()
    var isSuccesfull = MutableLiveData<Boolean>()

    init {
        isSuccesfull.value = false
        isComplete.value = false
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

                        val user = User(username,email,number,selectedLanguageState,pageLanguage)

                        userProfile.value = user
                    }

                }
            }.addOnFailureListener { exception ->
                Log.e("errorMsg",exception.localizedMessage)
            }
        }

    }

    fun addWord(new_word : Word){

        if(currentUser != null){
            val uid = currentUser!!.uid
            db.collection("Word").document(uid).get().addOnCompleteListener { task ->
                if(task.isSuccessful){
                    val document = task.result

                    if(document.data == null){

                        val wordHashMap = hashMapOf<String,Any>()
                        val wordList = arrayListOf<Word>()
                        wordList.add(new_word)

                        wordHashMap.put("wordList",wordList)
                        db.collection("Word").document(uid).set(wordHashMap).addOnCompleteListener { createTask ->
                            if(createTask.isSuccessful){
                                Toastoy.showSuccessToast(mContext,mContext.getString(R.string.word_added))
                                isComplete.value = true
                                isSuccesfull.value = true
                            }
                            else{
                                isComplete.value = true
                            }
                        }.addOnFailureListener { exception ->
                            Log.e("errorMsg",exception.localizedMessage)
                            isComplete.value = true
                        }
                    }
                    else{

                        val wordList = document.get("wordList") as ArrayList<Word>
                        wordList.add(new_word)

                        db.collection("Word").document(uid).update("wordList",wordList)
                            .addOnCompleteListener { updateTask ->
                                if(updateTask.isSuccessful){
                                    Toastoy.showSuccessToast(mContext,mContext.getString(R.string.word_added))
                                    isComplete.value = true
                                    isSuccesfull.value = true
                                }
                                else{
                                    isComplete.value = true

                                }
                            }.addOnFailureListener { exception ->
                                isComplete.value = true
                            }


                    }
                }
            }
        }
    }

}