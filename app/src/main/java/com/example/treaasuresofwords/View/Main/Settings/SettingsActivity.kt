package com.example.treaasuresofwords.View.Main.Settings

import android.app.AlertDialog
import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.example.treaasuresofwords.R
import com.example.treaasuresofwords.databinding.ActivitySettingsBinding
import com.google.firebase.auth.FirebaseAuth
import com.muhammed.toastoy.Toastoy
import java.util.*

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySettingsBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var dialogBuilder : AlertDialog.Builder
    private lateinit var dialog : AlertDialog
    private lateinit var editTextNewPassword : EditText
    private lateinit var editTextNewPasswordAgain : EditText
    private lateinit var btnUpdatePassword : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            this.supportActionBar!!.hide()
        } catch (e: NullPointerException) {
        }

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        auth = FirebaseAuth.getInstance()
        setContentView(binding.root)

        fillSpinner()
        createPopup()

        binding.btnChangePassword.setOnClickListener {
            dialog.show()
        }

    }

    fun fillSpinner(){

        val languageList = arrayListOf<String>()

            getSharedPreferences("User_Local_Data", Context.MODE_PRIVATE)
                ?.let {
                    val current_language = it.getString("current_language","en")
                    current_language?.let { current ->
                        if(current_language == "en"){
                            languageList.add(getString(R.string.english))
                            languageList.add(getString(R.string.turkish))
                        }
                        else{
                            languageList.add(getString(R.string.turkish))
                            languageList.add(getString(R.string.english))
                        }
                    }
                }




        val arrayAdapter = ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,languageList)

        binding.spinnerLanguage.adapter = arrayAdapter
        binding.spinnerLanguage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if(languageList[position] == getString(R.string.english)){
                    changeLanguage("en")
                }
                else{
                    changeLanguage("tr")
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
    }

    fun changeLanguage(language : String){

            // ingilizce -> en
            // türkçe -> tr
            val locale = Locale(language)
            Locale.setDefault(locale)
            val configuration = Configuration()
            configuration.locale = locale
            baseContext.resources.updateConfiguration(configuration,this.applicationContext.resources.displayMetrics)

            binding.apply {
                lblChangeLanguage.setText(getString(R.string.change_language))
                btnChangePassword.setText(getString(R.string.change_language))
            }

            val preferences = getSharedPreferences("User_Local_Data", Context.MODE_PRIVATE)
            val editor = preferences.edit()
            editor.putString("current_language",language)
            editor.apply()

            // aldık dili bunu shared preferences ta tutabiliriz

    }

    fun createPopup()
    {
            dialogBuilder = AlertDialog.Builder(this)
            var contactPopup = getLayoutInflater().inflate(R.layout.password_change_popup,null)

            editTextNewPassword = contactPopup.findViewById(R.id.editTextNewPassword)
            editTextNewPasswordAgain = contactPopup.findViewById(R.id.editTextNewPasswordAgain)
            btnUpdatePassword = contactPopup.findViewById(R.id.btnChangePassword)

            dialogBuilder.setView(contactPopup)

            dialog = dialogBuilder.create()
            dialog.setOnDismissListener {
            }

            btnUpdatePassword.setOnClickListener {
                val password = editTextNewPassword.text.toString().trim()
                var passwordConfirm = editTextNewPasswordAgain.text.toString().trim()

                if(password.isNotEmpty() && passwordConfirm.isNotEmpty()){
                    if(password.length >= 6){
                        if(password == passwordConfirm){
                            updatePassword(password)
                        }
                        else{
                            Toast.makeText(this.applicationContext,getString(R.string.passwords_not_match), Toast.LENGTH_SHORT).show()
                        }

                    }
                    else{
                        Toast.makeText(this.applicationContext,getString(R.string.change_password_character_error),
                            Toast.LENGTH_SHORT).show()
                    }

                }
            }
            dialog.dismiss()


    }

    fun updatePassword(password : String){

        var currentUser = auth.currentUser
        currentUser?.let { current ->
            current.updatePassword(password).addOnCompleteListener { taskComplete ->
                if(taskComplete.isSuccessful){
                    Toastoy.showSuccessToast(this,getString(R.string.change_password_message))
                    dialog.dismiss()
                    editTextNewPassword.setText("")
                    editTextNewPasswordAgain.setText("")
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(this,exception.localizedMessage, Toast.LENGTH_SHORT).show()
                Log.w("aaa",exception.localizedMessage)
            }
        }

    }

}