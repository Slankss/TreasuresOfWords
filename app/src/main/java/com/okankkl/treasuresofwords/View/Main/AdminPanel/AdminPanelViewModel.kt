package com.okankkl.treasuresofwords.View.Main.AdminPanel

import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.okankkl.treasuresofwords.Model.User
import com.okankkl.treasuresofwords.Model.Word
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

@RequiresApi(Build.VERSION_CODES.O)
class AdminPanelViewModel(var auth : FirebaseAuth, var db : FirebaseFirestore, var mContext : Context) : ViewModel() {

    var allWordList = arrayListOf<Word>()
    var wordList = MutableLiveData<ArrayList<Word>>()
    var userProfile = MutableLiveData<User>()
    var currentUser : FirebaseUser? = null

    init {
        getCurrentUser()

    }

    @RequiresApi(Build.VERSION_CODES.O)
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
                        val role = document.getString("role") as String

                        val user = User(username,email,number,selectedLanguageState,pageLanguage,role)

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
                    if(document != null && document.data != null && document.exists()){

                        val wordListInDb = document.get("wordList") as ArrayList<HashMap<String,Any>>
                        wordListInDb.forEach {
                            val word_name = it["word"].toString()
                            val translate = it["translate"].toString()
                            val repeatTime = it["repeatTime"].toString().toInt()
                            val date = it["date"].toString()
                            val quizTime = it["quizTime"].toString()
                            val index = it["index"].toString().toInt()


                            val word = Word(word_name,translate,repeatTime,date,quizTime,index)

                            wordListArray.add(word)
                        }
                    }
                    allWordList = wordListArray.map { it } as ArrayList<Word>
                    wordList.value = wordListArray
                }
            }

        }
    }

    fun setWorlLevel(wordLevel : Int){

        val uuid = currentUser!!.uid

        allWordList.forEachIndexed { index, word ->

            word.repeatTime = wordLevel

        }

        db.collection("Word").document(uuid).update("wordList",allWordList).addOnCompleteListener { task ->
            if(task.isSuccessful){
                Toast.makeText(mContext,"Tüm Kelimeler $wordLevel oldu",Toast.LENGTH_SHORT).show()
            }
        }

    }

    fun setWordIndex(){

        val uuid = currentUser!!.uid

        wordList.value?.forEachIndexed { index, word ->
            word.index = index
        }

        db.collection("Word").document(uuid).update("wordList",wordList.value).addOnCompleteListener { task ->
            if(task.isSuccessful){
                Toast.makeText(mContext,"Kelime index'leri fixlendi",Toast.LENGTH_SHORT).show()
            }
        }

    }



}