package com.example.treaasuresofwords.View.Main.Quiz.MatchingQuiz

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.treaasuresofwords.Model.Question
import com.example.treaasuresofwords.Model.User
import com.example.treaasuresofwords.Model.Word
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

@RequiresApi(Build.VERSION_CODES.O)
class QuizViewModel(private var auth : FirebaseAuth, private var db : FirebaseFirestore, ) : ViewModel() {

    var allWordList = ArrayList<Word>()
    var wordList = MutableLiveData<ArrayList<HashMap<String,Any>>>()
    var userProfile = MutableLiveData<User>()
    var currentUser : FirebaseUser? = null

    init {
        getCurrentUser()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getCurrentUser() {

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
            db.collection("Word").document(uid)
                .get().addOnCompleteListener { task ->
                    if(task.isSuccessful){
                        val document = task.result
                        val wordListArray = arrayListOf<HashMap<String,Any>>()
                        if(document != null && document.data != null && document.exists()){

                            val wordListInDb = document.get("wordList") as ArrayList<HashMap<String,Any>>
                            allWordList.clear()
                            wordListInDb.forEachIndexed { index, it ->
                                val word_name = it["word"].toString()
                                val translate = it["translate"].toString()
                                val repeatTime = it["repeatTime"].toString().toInt()
                                val dateString = it["date"].toString()
                                val quizTime = it["quizTime"].toString()
                                val index = it["index"].toString().toInt()

                                val word = Word(word_name,translate,repeatTime,dateString,quizTime,index)

                                allWordList.add(word)

                                var diff : Long = 0
                                if(quizTime.isNotEmpty() ){
                                    val day = quizTime.subSequence(0,2).toString()
                                    val month = quizTime.subSequence(3,5).toString()
                                    val year = quizTime.subSequence(6,10).toString()

                                    val localDate = LocalDateTime.now()
                                    val date = Date(year.toInt(),month.toInt(),day.toInt())
                                    var currentDate = Date(localDate.year,localDate.monthValue,localDate.dayOfMonth)

                                    diff  = ( currentDate.time - date.time ) / 1000 / 60 / 60 // convert to hour
                                }

                                var hashMap = hashMapOf<String,Any>()
                                hashMap.put("word",word)
                                hashMap.put("position",index)

                                when(repeatTime){
                                    3 -> {
                                        if(diff >= 3600*24) wordListArray.add(hashMap)
                                    }
                                    4 -> {
                                        if(diff >= 3600*48) wordListArray.add(hashMap)
                                    }
                                    else -> {
                                        if(repeatTime != 5)  wordListArray.add(hashMap)
                                    }
                                }
                            }
                        }
                        wordList.value = wordListArray
                    }
                }.addOnFailureListener { e ->
                    Log.e("errorMsg",e.localizedMessage)
                }
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun update(updatedList : ArrayList<Question>){

        val currentDate = LocalDateTime.now()
        val day = currentDate.dayOfMonth.toString()
        val month = currentDate.monthValue.toString()
        val year = currentDate.year.toString()

        val dayString = when(day.length){
            1 -> "0$day"
            else -> { day}
        }
        val monthString = when(month.length){
            1 -> "0$month"
            else -> { month}
        }
        val dateString = "$dayString-$monthString-$year"

        updatedList.forEachIndexed { index, question ->
            Log.w("ARABAM3","word = ${allWordList[question.index].word} level = ${allWordList[question.index].repeatTime} ")
            allWordList[question.index].repeatTime +=1
            allWordList[question.index].quizTime = dateString
            }


        if(currentUser != null){

            var uid = currentUser!!.uid
            db.collection("Word").document(uid).update("wordList",allWordList)
                .addOnCompleteListener { task ->
                if(task.isSuccessful)
                    {

                    }
                }
                .addOnFailureListener {  exception ->
                    Log.e("errorMsg",exception.localizedMessage)
                }

        }

    }

    fun updateRepeatQuiz(updatedList : ArrayList<Question>){

        val currentDate = LocalDateTime.now()
        val day = currentDate.dayOfMonth.toString()
        val month = currentDate.monthValue.toString()
        val year = currentDate.year.toString()

        val dayString = when(day.length){
            1 -> "0$day"
            else -> { day}
        }
        val monthString = when(month.length){
            1 -> "0$month"
            else -> { month}
        }
        val dateString = "$dayString-$monthString-$year"

        updatedList.forEachIndexed { index, question ->
            allWordList[question.index].repeatTime -=1
            allWordList[question.index].quizTime = dateString

        }


        if(currentUser != null){

            var uid = currentUser!!.uid
            db.collection("Word").document(uid).update("wordList",allWordList)
                .addOnCompleteListener { task ->
                    if(task.isSuccessful)
                    {

                    }
                }
                .addOnFailureListener {  exception ->
                    Log.e("errorMsg",exception.localizedMessage)
                }

        }


    }



}