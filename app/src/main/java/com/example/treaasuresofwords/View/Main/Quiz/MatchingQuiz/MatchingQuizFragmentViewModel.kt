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
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.math.log
import kotlin.math.min

@RequiresApi(Build.VERSION_CODES.O)
class MatchingQuizFragmentViewModel(private var auth : FirebaseAuth, private var db : FirebaseFirestore, ) : ViewModel() {

    var allWordList = ArrayList<Word>()
    var wordList = MutableLiveData<ArrayList<Word>>()
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

                        val user = User(username,email,number,selectedLanguageState,pageLanguage)

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
                        val wordListArray = arrayListOf<Word>()
                        if(document != null && document.data != null && document.exists()){

                            val wordListInDb = document.get("wordList") as ArrayList<HashMap<String,Any>>
                            allWordList.clear()
                            wordListInDb.forEach {
                                val word_name = it["word"].toString()
                                val translate = it["translate"].toString()
                                val repeatTime = it["repeatTime"].toString().toInt()
                                val dateString = it["date"].toString()
                                val quizTime = it["quizTime"].toString()
                                val quizTimeHour = it["quizTimeHour"].toString()
                                val word = Word(word_name,translate,repeatTime,dateString,quizTime,quizTimeHour)

                                allWordList.add(word)

                                var diff : Long = 0
                                if(quizTime.isNotEmpty() && quizTimeHour.isNotEmpty()){
                                    val day = quizTime.subSequence(0,2).toString()
                                    val month = quizTime.subSequence(3,5).toString()
                                    val year = quizTime.subSequence(6,10).toString()
                                    val hour = quizTimeHour.subSequence(0,2).toString()
                                    var minute = quizTimeHour.subSequence(3,5).toString()

                                    val localDate = LocalDateTime.now()
                                    val date = Date(year.toInt(),month.toInt(),day.toInt(),hour.toInt(),hour.toInt(),minute.toInt())
                                    var currentDate = Date(localDate.year,localDate.monthValue,localDate.dayOfMonth,localDate.hour,localDate.minute)
                                    // LocalDate.of(year.toInt(),month.toInt(),day.toInt())
                                    Log.w("DATEEEEEE","DATE  = $date")
                                    Log.w("DATEEEEEE","LOCAL DATE  = $currentDate")

                                    diff  = ( currentDate.time - date.time ) / 1000 / 60 / 60 // convert to hour
                                    Log.w("DATEEEEEE","TARİH FARKI = ${diff.toInt()}")
                                    Log.w("DATEEEEEE","TARİH FARKI = ${currentDate.time - date.time}")
                                }

                                when(repeatTime){
                                    0 -> {
                                        wordListArray.add(word)
                                    }
                                    1 -> {
                                        if(diff >= 3600){
                                            wordListArray.add(word)
                                        }
                                    }
                                    2 -> {
                                        if(diff >= 3600*4){
                                            wordListArray.add(word)
                                        }
                                    }
                                    3 -> {
                                        if(diff >= 3600*9){
                                            wordListArray.add(word)
                                        }
                                    }
                                    4 -> {
                                        if(diff >= 3600*16){
                                            wordListArray.add(word)
                                        }
                                    }
                                }

                            }
                            wordListArray.reverse()
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
        var hour = currentDate.hour.toString()
        var minute = currentDate.minute.toString()

        if(hour.length == 1){
            hour = "0$hour"
        }
        if(minute.length == 1){
            minute = "0$minute"
        }

        val dayString = when(day.length){
            1 -> "0$day"
            else -> { day}
        }
        val monthString = when(month.length){
            1 -> "0$month"
            else -> { month}
        }
        val dateString = "$dayString-$monthString-$year"
        val hourString = "$hour:$minute"

        updatedList.forEachIndexed { index, question ->
            allWordList[question.index].repeatTime +=1
            allWordList[question.index].quizTime = dateString
            allWordList[question.index].quizTimeHour = hourString
            }
            Log.w("tag","${allWordList.size}")

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