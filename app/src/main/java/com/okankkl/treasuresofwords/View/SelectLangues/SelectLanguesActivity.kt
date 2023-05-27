package com.okankkl.treasuresofwords.View.SelectLangues


import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.okankkl.treasuresofwords.R
import android.annotation.SuppressLint
import android.content.Intent
import com.okankkl.treasuresofwords.Model.LoadingDialog
import com.okankkl.treasuresofwords.View.Main.MainActivity
import com.okankkl.treasuresofwords.databinding.ActivitySelectLanguesBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.muhammed.toastoy.Toastoy

class SelectLanguesActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySelectLanguesBinding
    private lateinit var adapter : SelectedLanguagesAdapter
    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseFirestore
    private var languageList = arrayListOf<String>("English","Turkish","Spanish","German","French")
    private var languageToTranslated = ""
    private var translatedLanguage = ""
    private lateinit var loadingDialog : LoadingDialog

    private var selectedLanguages = arrayListOf<HashMap<String,String>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectLanguesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        loadingDialog = LoadingDialog(this)

        try {
            this.supportActionBar!!.hide()
        } catch (e: NullPointerException) {
        }
        createAdapter()

        fillSpinner(applicationContext)

        binding.btnAdd.setOnClickListener {
            addLanguage(applicationContext)
            createAdapter()
        }


        binding.btnContinue.setOnClickListener {

            if(selectedLanguages.isNotEmpty()){
                updateProfile()
            }
            else{
                Toastoy.showDefaultToast(this,getString(R.string.selected_languages_is_empty))
            }

        }


    }

    fun updateProfile() {
        val currentUser = auth.currentUser
        loadingDialog.startLoadingDialog()
        currentUser?.let {
            val uid = it.uid

            db.collection("User").document(uid).update("languages",selectedLanguages)
                .addOnCompleteListener { task ->
                    if(task.isSuccessful){
                        loadingDialog.dismissDialog()
                        startActivity(Intent(applicationContext,MainActivity::class.java))
                        finish()
                    }
            }.addOnFailureListener { exception ->
                    Toastoy.showDefaultToast(this,exception.localizedMessage)
            }
        }
    }

    fun addLanguage(mContext : Context){
        if(languageToTranslated.isNotEmpty() && translatedLanguage.isNotEmpty()){
            var errorMessage = ""
            if(languageToTranslated != translatedLanguage){
                if(!isContain()){
                    val hashMap = HashMap<String,String>()
                    hashMap.put("languageToTranslated",languageToTranslated)
                    hashMap.put("translatedLanguage",translatedLanguage)
                    selectedLanguages.add(hashMap)
                }
                else{
                    errorMessage = getString(R.string.has_already_error_message)
                }
            }
            else{
                errorMessage = getString(R.string.same_language_error_message)
            }
            if(errorMessage.isNotEmpty()){
                Toastoy.showDefaultToast(this,errorMessage)
            }
        }
    }

    fun isContain() : Boolean {

        selectedLanguages.forEachIndexed { index, item ->
            if(item["languageToTranslated"] == languageToTranslated && item["translatedLanguage"] == translatedLanguage ){
               return true
            }
            else if(item["languageToTranslated"] == translatedLanguage && item["translatedLanguage"] == languageToTranslated){
                return true
            }
        }

        return false
    }

    @SuppressLint("NotifyDataSetChanged")
    fun createAdapter(){
        val layoutManager = LinearLayoutManager(applicationContext)
        binding.recyclerView.layoutManager = layoutManager
        adapter= SelectedLanguagesAdapter(selectedLanguages)
        binding.recyclerView.adapter = adapter

        adapter.deleteClick = {
            selectedLanguages.removeAt(it)
            adapter.notifyDataSetChanged()
        }
    }

    fun fillSpinner(mContext : Context){


        val arrayAdapter = ArrayAdapter<String>(mContext,android.R.layout.simple_spinner_dropdown_item,languageList)

        binding.spinnerLanguageToTranslated.adapter = arrayAdapter
        binding.spinnerLanguageToTranslated.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                languageToTranslated = languageList[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        //val arrayAdapter = ArrayAdapter<String>(mContext, R.layout.simple_spinner_dropdown_item,languageList)

        binding.spinnerTranslatedLangue.adapter = arrayAdapter
        binding.spinnerTranslatedLangue.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                translatedLanguage = languageList[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

    }



}