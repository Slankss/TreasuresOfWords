package com.example.treaasuresofwords.View.Main.Word

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
import com.google.firebase.firestore.ktx.snapshots
import com.muhammed.toastoy.Toastoy

class WordFragmentViewModel(var auth : FirebaseAuth,var db : FirebaseFirestore,var mContext : Context) : ViewModel() {

    var allWordList = arrayListOf<Word>()
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

                        getWordList(languages.get(0)["languageToTranslated"].toString(),languages.get(0)["translatedLanguage"].toString())
                    }

                }
            }.addOnFailureListener { exception ->
                Log.e("errorMsg",exception.localizedMessage)
            }
        }

    }

    fun getWordList(currentLanguageToTranslated : String,currentTranslatedLanguage : String){

        if(currentUser != null){

            val uid = currentUser!!.uid
            db.collection("Word").document(uid).addSnapshotListener { value, error ->
                if(error != null){
                    Log.e("errorMsg",error.localizedMessage)
                }
                else{
                    val document = value
                    val wordListArray = arrayListOf<Word>()
                    if(document != null && document.data != null){
                        val wordListInDb = document.get("wordList") as ArrayList<HashMap<String,Any>>
                        wordListInDb.forEach {
                            val languageToTranslated = it["languageToTranslated"].toString()
                            val translatedLanguage = it["translatedLanguage"].toString()
                            val word_name = it["word"].toString()
                            val translate = it["translate"].toString()
                            val repeatTime = it["repeatTime"].toString().toInt()
                            val word = Word(languageToTranslated,translatedLanguage,word_name,translate,repeatTime)
                            if(currentLanguageToTranslated == languageToTranslated &&
                                currentTranslatedLanguage == translatedLanguage){
                                wordListArray.add(word)
                            }

                        }

                    }
                    allWordList = wordListArray.map { it } as ArrayList<Word>
                    wordList.value = wordListArray
                }
            }

        }

    }



    fun deleteWord(selectedWords : ArrayList<Word>){
        currentUser?.let { current ->
            val uid = current.uid
            if(wordList.value != null){
                val wordListArray = wordList.value
            if (wordListArray!!.size == selectedWords.size) {
                Log.w("ggg", "IF GİRDİ")
                db.collection("Word").document(uid).delete()
                    .addOnCompleteListener { deleteTask ->
                        if (deleteTask.isSuccessful) {
                            Toastoy.showInfoToast(mContext,mContext.getString(R.string.words_deleted))
                        }
                    }
            }
            else{
                selectedWords.forEachIndexed { index, word ->
                    if(wordListArray.contains(word)){
                        wordListArray.remove(word)
                    }
                }

                db.collection("Word").document(uid).update("wordList", wordListArray)
                    .addOnCompleteListener { updateTask ->
                        if (updateTask.isSuccessful) {
                            var message = ""
                            if(selectedWords.size > 1){
                                message = mContext.getString(R.string.words_deleted)
                            }
                            else{
                                message = mContext.getString(R.string.word_deleted)
                            }
                            Toastoy.showInfoToast(mContext,message)
                        }
                    }
                }
                allWordList.clear()
                allWordList = wordListArray.map { it } as ArrayList<Word>
            }
        }
    }

    fun searchWord(searchedWord : String){

        currentUser?.let {  current ->
            var uid = current.uid
            if(searchedWord.isNotEmpty()){
                if(allWordList.isNotEmpty()){

                    val searchedWordList = arrayListOf<Word>()
                    allWordList.forEach { word ->
                        if(word.word.startsWith(searchedWord)){
                            searchedWordList.add(word)
                        }
                    }
                    wordList.value!!.clear()
                    wordList.value = searchedWordList

                }
            }
        }
    }


}