package com.example.treaasuresofwords.View.LoginAndRegister.Register

import android.R
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.translation.Translator
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.treaasuresofwords.Model.LoadingDialog
import com.example.treaasuresofwords.Model.User
import com.example.treaasuresofwords.View.LoginAndRegister.Verification.VerificationActivity
import com.example.treaasuresofwords.databinding.FragmentRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDateTime


class RegisterFragment : Fragment() {

    private var _binding : FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseFirestore
    private lateinit var viewModel : RegisterViewModel
    private var current_language = "en"
    private var passwordVisibility = false
    private var passwordConfirmVisibility = false
    private lateinit var loadingDialog : LoadingDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingDialog = LoadingDialog(this.requireActivity())

        binding.btnPasswordVisibility.setOnClickListener {
            when(passwordVisibility){
                false -> {
                    binding.editTextPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance())
                    binding.btnPasswordVisibility.setImageResource(com.example.treaasuresofwords.R.drawable.ic_visibility_off)
                    passwordVisibility = true

                }
                else ->{
                    binding.editTextPassword.setTransformationMethod(PasswordTransformationMethod.getInstance())
                    binding.btnPasswordVisibility.setImageResource(com.example.treaasuresofwords.R.drawable.ic_visibility_on)
                    passwordVisibility = false

                }
            }
        }

        binding.btnPasswordConfirmVisibility.setOnClickListener {
            when(passwordConfirmVisibility){
                false -> {
                    binding.editTextPasswordAgain.setTransformationMethod(HideReturnsTransformationMethod.getInstance())
                    binding.btnPasswordConfirmVisibility.setImageResource(com.example.treaasuresofwords.R.drawable.ic_visibility_off)
                    passwordConfirmVisibility = true

                }
                else ->{
                    binding.editTextPasswordAgain.setTransformationMethod(PasswordTransformationMethod.getInstance())
                    binding.btnPasswordConfirmVisibility.setImageResource(com.example.treaasuresofwords.R.drawable.ic_visibility_on)
                    passwordConfirmVisibility = false

                }
            }
        }


        activity?.let {
            it.getSharedPreferences("User_Local_Data", Context.MODE_PRIVATE)
                ?.let {
                     it.getString("current_language","en")?.let { language ->
                         current_language = language
                    }
                }
        }



        binding.btnBack.setOnClickListener {
            val action = RegisterFragmentDirections.actionRegisterFragmentToFirstPageFragment()
            findNavController().navigate(action)
        }

        binding.txtSignIn.setOnClickListener {
            val action = RegisterFragmentDirections.actionRegisterFragmentToLoginFragment()
            findNavController().navigate(action)
        }



        binding.btnRegister.setOnClickListener {
            register()
        }

        viewModel.is_succesfull.observe(viewLifecycleOwner){ isSuccesful ->
            if(isSuccesful){
                activity?.let {
                    startActivity(Intent(it.applicationContext,VerificationActivity::class.java))
                }

            }
        }


        viewModel.is_complete.observe(viewLifecycleOwner) { isComplete ->
            if(isComplete){
                loadingDialog.dismissDialog()
            }
        }



    }


    fun register(){

        binding.apply {
            val email = editTextEmail.text.toString().trim()
            val password = editTextPassword.text.toString().trim()
            val passwordAgain = editTextPasswordAgain.text.toString().trim()

            val result = check(email,password,passwordAgain)

            if(result){
                loadingDialog.startLoadingDialog()
                btnRegister.isClickable = false
                // name,surname,numner is empty
                // empty langauges list
                val emptyList = arrayListOf<HashMap<String,Any>>()
                var user = User("",email,"",false,current_language,emptyList)
                viewModel.register(user,password)
            }

        }


    }


    fun check(email : String,password : String,passwordAgain : String) : Boolean
    {
        if(email.isNotEmpty() && password.isNotEmpty() && passwordAgain.isNotEmpty()){
            if(password == passwordAgain){
                return true // true is mean not empty
            }
        }

        return false
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentRegisterBinding.inflate(inflater,container,false)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        context?.let {
            viewModel = RegisterViewModel(auth,db,it)
        }


        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}