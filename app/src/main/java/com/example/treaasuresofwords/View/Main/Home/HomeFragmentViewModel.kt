package com.example.treaasuresofwords.View.Main.Home

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.treaasuresofwords.Model.User
import com.example.treaasuresofwords.Model.Word
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.muhammed.toastoy.Toastoy
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
class HomeFragmentViewModel(var auth : FirebaseAuth, var db : FirebaseFirestore) : ViewModel(){

    var user = MutableLiveData<User>()
    var currentUser : FirebaseUser? = null
    var learnedWord = MutableLiveData<Int>()
    var allWordList = arrayListOf<Word>()
    var userProfile = MutableLiveData<User>()
    var wordList = MutableLiveData<ArrayList<Word>>()


    init {
        learnedWord.value = 0
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

                        getWordList(null)
                    }

                }
            }.addOnFailureListener { exception ->
                Log.e("errorMsg",exception.localizedMessage)
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getWordList(filterDate : LocalDate?){

        if(currentUser != null){

            val uid = currentUser!!.uid
            db.collection("Word").document(uid).addSnapshotListener { value, error ->
                if(error != null){
                    Log.e("errorMsg",error.localizedMessage)
                }
                else{
                    val document = value
                    val wordListArray = arrayListOf<Word>()
                    wordList.value?.clear()
                    learnedWord.value = 0
                    var learned = 0
                    if(document != null && document.data != null && document.exists()){
                        val wordListInDb = document.get("wordList") as ArrayList<HashMap<String,Any>>
                        wordListInDb.forEach {
                            val word_name = it["word"].toString()
                            val translate = it["translate"].toString()
                            val repeatTime = it["repeatTime"].toString().toInt()
                            val date = it["date"].toString()
                            val quizTime = it["quizTime"].toString()
                            val quizTimeHour = it["quizTimeHour"].toString()

                            val word = Word(word_name,translate,repeatTime,date,quizTime,quizTimeHour)


                            if(filterDate == null){
                                wordListArray.add(word)
                                if(word.repeatTime >= 3){
                                    learned++
                                }
                            }
                            else{

                                val day = it["date"].toString().subSequence(0,2).toString()
                                val month = it["date"].toString().subSequence(3,5).toString()
                                val year = it["date"].toString().subSequence(6,10).toString()

                                var date = LocalDate.of(year.toInt(),month.toInt(),day.toInt())

                                if(date.isAfter(filterDate))
                                {
                                    wordListArray.add(word)
                                    if(word.repeatTime >= 5){
                                    learned++
                                    }
                                }

                            }



                        }
                        learnedWord.value = learned
                        wordListArray.reverse()

                    }
                    allWordList = wordListArray.map { it } as ArrayList<Word>
                    wordList.value = wordListArray.map { it } as ArrayList<Word>

                }
            }

        }
    }

}