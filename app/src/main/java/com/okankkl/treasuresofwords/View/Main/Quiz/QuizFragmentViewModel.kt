package com.okankkl.treasuresofwords.View.Main.Quiz

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.okankkl.treasuresofwords.Model.User
import com.okankkl.treasuresofwords.Model.Word
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date
import java.time.LocalDateTime

@RequiresApi(Build.VERSION_CODES.O)
class QuizFragmentViewModel(var auth : FirebaseAuth, var db : FirebaseFirestore) : ViewModel() {

    var allWordList = ArrayList<Word>()
    var wordList = MutableLiveData<ArrayList<Word>>()
    var learnedWordList = MutableLiveData<ArrayList<Word>>()
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

    @RequiresApi(Build.VERSION_CODES.O)
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
                    val learnedWordListArray = arrayListOf<Word>()
                    allWordList.clear()
                    if(document != null && document.data != null && document.exists()){

                        val wordListInDb = document.get("wordList") as ArrayList<HashMap<String,Any>>
                        wordListInDb.forEach {
                            val word_name = it["word"].toString()
                            val translate = it["translate"].toString()
                            val repeatTime = it["repeatTime"].toString().toInt()
                            val dateString = it["date"].toString()
                            val quizTime = it["quizTime"].toString()
                            var index = it["index"].toString().toInt()
                            val word = Word(word_name,translate,repeatTime,dateString,quizTime,index)

                            allWordList.add(word)

                            var diff : Int = 0
                            if(quizTime.isNotEmpty()){

                                val day = it["quizTime"].toString().subSequence(0,2).toString()
                                val month = it["quizTime"].toString().subSequence(3,5).toString()
                                val year = it["quizTime"].toString().subSequence(6,10).toString()

                                val localDate = LocalDateTime.now()
                                val date = Date(year.toInt(),month.toInt(),day.toInt())
                                val currentDate = Date(localDate.year,localDate.monthValue,localDate.dayOfMonth)

                                diff  = (( currentDate.time - date.time ) / 3600000 /24 ).toInt() // convert to hour

                                when(repeatTime){
                                    3 -> {
                                        if(diff >= 1)  wordListArray.add(word)
                                    }
                                    4 -> {
                                        if(diff >= 2) wordListArray.add(word)
                                    }
                                    5 -> learnedWordListArray.add(word)
                                    else -> {
                                        wordListArray.add(word)
                                    }
                                }
                            }
                            else{
                                if(repeatTime == 5){
                                    learnedWordListArray.add(word)
                                }
                                else{
                                    wordListArray.add(word)
                                }
                            }

                        }
                    }
                    learnedWordList.value = learnedWordListArray
                    wordList.value = wordListArray
                }
            }

        }
    }


}