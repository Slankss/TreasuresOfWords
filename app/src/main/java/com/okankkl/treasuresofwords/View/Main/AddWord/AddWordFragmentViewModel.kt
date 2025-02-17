package com.okankkl.treasuresofwords.View.Main.AddWord

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.okankkl.treasuresofwords.Model.User
import com.okankkl.treasuresofwords.Model.Word
import com.okankkl.treasuresofwords.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions
import com.muhammed.toastoy.Toastoy
import com.okankkl.treasuresofwords.Model.Roles

class AddWordFragmentViewModel(var auth : FirebaseAuth,var db : FirebaseFirestore,var mContext : Context) : ViewModel() {

    var userProfile = MutableLiveData<User>()
    var currentUser : FirebaseUser? = null
    var isComplete = MutableLiveData<Boolean>()
    var isSuccesfull = MutableLiveData<Boolean>()
    var user_role = MutableLiveData<String>()
    private var languageModelsDowloaded = false
    private var options : FirebaseTranslatorOptions? = null
    private var englishTurkishTranslator : FirebaseTranslator? = null
    var isTranslateCompleted = MutableLiveData<Boolean>()

    var translatedWord = ""



    init {
        user_role.value = Roles.user.name
        isTranslateCompleted.postValue(false)
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
                        val role = document.getString("role") as String

                        val user = User(username,email,number,selectedLanguageState,pageLanguage,role)

                        user_role.value = role
                        if(user_role.value.equals(Roles.admin.name.trim()))
                        {
                            setupLanguageModel()
                        }

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
                        new_word.index = wordList.lastIndex +1
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

    fun setupLanguageModel(){
        options = FirebaseTranslatorOptions.Builder()
            .setSourceLanguage(FirebaseTranslateLanguage.EN)
            .setTargetLanguage(FirebaseTranslateLanguage.TR)
            .build()

        options?.let {
            englishTurkishTranslator  = FirebaseNaturalLanguage.getInstance().getTranslator(it)
        }

        englishTurkishTranslator?.let {
            it.downloadModelIfNeeded()
                .addOnCompleteListener { task->
                    if(task.isSuccessful){
                        languageModelsDowloaded = true
                    }
                }
                .addOnFailureListener {

                }
        }
    }

    fun translateWord(translate : String) {

        if(englishTurkishTranslator != null && options != null){
            englishTurkishTranslator!!.translate(translate).addOnSuccessListener { translatedText ->
               translatedWord = translatedText
               isTranslateCompleted.value = true
            }.addOnFailureListener { e ->
                Log.w("ARABAM",e.localizedMessage)
            }
        }
        else{
            var errorMsg = mContext.getString(R.string.models_not_ready)
            Toast.makeText(mContext,errorMsg,Toast.LENGTH_SHORT).show()
        }
    }

}