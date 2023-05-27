package com.okankkl.treasuresofwords.View.LoginAndRegister.PasswordReset

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.okankkl.treasuresofwords.R
import com.okankkl.treasuresofwords.databinding.FragmentPasswordResetBinding
import com.google.firebase.auth.FirebaseAuth
import java.time.LocalDateTime


class PasswordResetFragment : Fragment() {

    private var _binding : FragmentPasswordResetBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth : FirebaseAuth
    private var email = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSenResetEmail.setOnClickListener {
            email = binding.editTextEmail.text.toString().trim()
            if(email.isNotEmpty()){
                checkSharedPreferences()
            }
            else{
                activity?.let {
                    Toast.makeText(it.applicationContext,it.getString(R.string.email_is_empty),Toast.LENGTH_SHORT).show()
                }
            }

        }



    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun checkSharedPreferences(){
        val localDate = LocalDateTime.now()
        val day_total_second = localDate.hour*3600 + localDate.minute*60 + localDate.second

        activity?.let { cActivity ->
            val preferences = cActivity.getSharedPreferences("User_Password_reset_Time", Context.MODE_PRIVATE)
            if(preferences == null){
                val new_preferences = cActivity.getSharedPreferences("User_Password_reset_Time",
                    Context.MODE_PRIVATE)
                val editor = new_preferences.edit()
                editor.putInt("password_reset_time",day_total_second)
                editor.apply()
            }
            else{
                val localDate = LocalDateTime.now()
                val day_total_second = localDate.hour*3600 + localDate.minute*60 + localDate.second

                val total_time = preferences.getInt("password_reset_time", 0)
                val differences = day_total_second - total_time

                if(day_total_second - total_time > 60){
                    setResetPassword()
                    val editor = preferences.edit()
                    editor.putInt("password_reset_time",day_total_second)
                    editor.apply()
                }
                else{
                    val remainingTime = 60 - differences
                    val text = cActivity.getString(R.string.reset_password_time_message)+ remainingTime + cActivity.getString(R.string.second)
                    Toast.makeText(cActivity.applicationContext,text,Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun setResetPassword(){
       auth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
           if(task.isSuccessful){
               activity?.let {
                   Toast.makeText(it.applicationContext,it.getString(R.string.reset_password_toast_message),Toast.LENGTH_LONG).show()
               }
           }
       }.addOnFailureListener { exception ->
           activity?.let {
               Toast.makeText(it.applicationContext,exception.localizedMessage,Toast.LENGTH_LONG).show()
           }

       }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentPasswordResetBinding.inflate(inflater,container,false)
        auth = FirebaseAuth.getInstance()
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}