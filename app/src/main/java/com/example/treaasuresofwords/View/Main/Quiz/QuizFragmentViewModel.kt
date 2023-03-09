package com.example.treaasuresofwords.View.Main.Quiz

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
import com.google.firebase.ktx.Firebase
import com.muhammed.toastoy.Toastoy
import java.util.Date
import java.time.LocalDate
import java.time.ZoneId

@RequiresApi(Build.VERSION_CODES.O)
class QuizFragmentViewModel(var auth : FirebaseAuth, var db : FirebaseFirestore) : ViewModel() {

    var allWordList = ArrayList<Word>()
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
            db.collection("Word").document(uid).addSnapshotListener { value, error ->
                if(error != null){
                    Log.e("errorMsg",error.localizedMessage)
                }
                else{
                    val document = value
                    val wordListArray = arrayListOf<Word>()
                    allWordList.clear()
                    if(document != null && document.data != null && document.exists()){

                        val wordListInDb = document.get("wordList") as ArrayList<HashMap<String,Any>>
                        wordListInDb.forEach {
                            val word_name = it["word"].toString()
                            val translate = it["translate"].toString()
                            val repeatTime = it["repeatTime"].toString().toInt()
                            val dateString = it["date"].toString()
                            val quizTime = it["quizTime"].toString()
                            val quizTimeHour = it["quizTimeHour"].toString()
                            val word = Word(word_name,translate,repeatTime,dateString,quizTime,quizTimeHour)

                            allWordList.add(word)

                            val day = it["date"].toString().subSequence(0,2).toString()
                            val month = it["date"].toString().subSequence(3,5).toString()
                            val year = it["date"].toString().subSequence(6,10).toString()

                            val localDate = LocalDate.now()
                            val date = LocalDate.of(year.toInt(),month.toInt(),day.toInt())
                            val diff : Long = converDate(localDate).time - converDate(date).time
                            when(repeatTime){
                                0 -> {
                                    wordListArray.add(word)
                                }
                                1 -> {
                                    if(diff >= 1){
                                        wordListArray.add(word)
                                    }
                                }
                                2 -> {
                                    if(diff >= 4){
                                        wordListArray.add(word)
                                    }
                                }
                                3 -> {
                                    if(diff >= 9){
                                        wordListArray.add(word)
                                    }
                                }
                                4 -> {
                                    if(diff >= 16){
                                        wordListArray.add(word)
                                    }
                                }
                            }

                        }
                        wordListArray.reverse()
                    }
                    wordList.value = wordListArray
                }
            }

        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun converDate(dateToConvert: LocalDate): Date {
        return Date.from(dateToConvert.atStartOfDay(ZoneId.systemDefault()).toInstant())
    }

    fun reset(){

        if(currentUser != null) {

            var uid = currentUser!!.uid
            allWordList.forEachIndexed { index, word ->
                word.quizTime = ""
                word.quizTimeHour = ""
                word.repeatTime = 0
            }

            db.collection("Word").document(uid).update("wordList",allWordList).addOnCompleteListener { task ->
                if(task.isSuccessful){

                }
            }

        }
    }

}