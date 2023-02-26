package com.example.treaasuresofwords.View.Main.Settings

import android.app.AlertDialog
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.treaasuresofwords.R
import com.example.treaasuresofwords.databinding.FragmentSettingsBinding
import com.google.firebase.auth.FirebaseAuth
import com.muhammed.toastoy.Toastoy
import java.util.*


class SettingsFragment : Fragment() {

    private var _binding : FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth : FirebaseAuth
    private lateinit var dialogBuilder : AlertDialog.Builder
    private lateinit var dialog : AlertDialog
    private lateinit var editTextNewPassword : EditText
    private lateinit var editTextNewPasswordAgain : EditText
    private lateinit var btnUpdatePassword : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fillSpinner(view.context)
        createPopup()

        binding.btnChangePassword.setOnClickListener {
            dialog.show()
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSettingsBinding.inflate(inflater,container,false)
        auth = FirebaseAuth.getInstance()

        return binding.root
    }

    fun fillSpinner(mContext : Context){

        val languageList = arrayListOf<String>()

        activity?.let {

            it.getSharedPreferences("User_Local_Data", Context.MODE_PRIVATE)
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
        }



        val arrayAdapter = ArrayAdapter<String>(mContext,android.R.layout.simple_spinner_dropdown_item,languageList)

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
        activity?.let {
            // ingilizce -> en
            // türkçe -> tr
            val locale = Locale(language)
            Locale.setDefault(locale)
            val configuration = Configuration()
            configuration.locale = locale
            it.baseContext.resources.updateConfiguration(configuration,it.applicationContext.resources.displayMetrics)

            binding.apply {
                lblChangeLanguage.setText(getString(R.string.change_language))

            }

            val preferences = it.getSharedPreferences("User_Local_Data",Context.MODE_PRIVATE)
            val editor = preferences.edit()
            editor.putString("current_language",language)
            editor.apply()

            // aldık dili bunu shared preferences ta tutabiliriz

        }

    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    fun createPopup()
    {
        context?.let { mContext ->
            dialogBuilder = AlertDialog.Builder(mContext)
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
                            Toast.makeText(mContext,getString(R.string.passwords_not_match),Toast.LENGTH_SHORT).show()
                        }

                    }
                    else{
                        Toast.makeText(mContext,getString(R.string.change_password_character_error),Toast.LENGTH_SHORT).show()
                    }

                }
            }

            dialog.dismiss()

        }
    }

    fun updatePassword(password : String){

        var currentUser = auth.currentUser
        currentUser?.let { current ->
            current.updatePassword(password).addOnCompleteListener { taskComplete ->
                if(taskComplete.isSuccessful){
                    context?.let {
                        Toastoy.showSuccessToast(it,getString(R.string.change_password_message))
                        dialog.dismiss()
                        editTextNewPassword.setText("")
                        editTextNewPasswordAgain.setText("")
                    }
                }
            }.addOnFailureListener { exception ->
                context?.let {
                    Toast.makeText(it,exception.localizedMessage,Toast.LENGTH_SHORT).show()
                    Log.w("aaa",exception.localizedMessage)
                }


            }
        }

    }


}