package com.okankkl.treasuresofwords.View.LoginAndRegister.Register

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.okankkl.treasuresofwords.Model.LoadingDialog
import com.okankkl.treasuresofwords.Model.User
import com.okankkl.treasuresofwords.R
import com.okankkl.treasuresofwords.View.LoginAndRegister.Verification.VerificationActivity
import com.okankkl.treasuresofwords.databinding.FragmentRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class RegisterFragment : Fragment() {

    private var _binding : FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseFirestore
    private lateinit var viewModel : RegisterViewModel
    private var current_language = "en"
    private lateinit var loadingDialog : LoadingDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingDialog = LoadingDialog(this.requireActivity())


        activity?.let {
            it.getSharedPreferences("User_Local_Data", Context.MODE_PRIVATE)
                ?.let {
                     it.getString("current_language","en")?.let { language ->
                         current_language = language
                    }
                }
        }

        binding.editTextEmail.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus){
                binding.TextEmailInputLayout.error = null
            }
        }

        binding.editTextPassword.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus){
                binding.passwordInputLayout.error = null
            }
        }

        binding.editTextPasswordAgain.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus){
                binding.passwordAgainInputLayout.error = null
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
                val user = User("",email,"",false,current_language,"user")
                viewModel.register(user,password)
            }

        }


    }


    fun check(email : String,password : String,passwordAgain : String) : Boolean
    {
        binding.TextEmailInputLayout.error = null
        binding.passwordInputLayout.error = null
        binding.passwordAgainInputLayout.error = null

        if(email.isNotEmpty() && password.isNotEmpty() && passwordAgain.isNotEmpty()){
            if(password == passwordAgain){
                return true // true is mean not empty
            }
        }

        binding.apply {

            if(email.isEmpty()){ TextEmailInputLayout.error = getString(R.string.empty_message) }
            if(password.isEmpty()) { passwordInputLayout.error = getString(R.string.empty_message) }
            if(passwordAgain.isEmpty()) { passwordAgainInputLayout.error = getString(R.string.empty_message)}
            if(password != passwordAgain) {
                passwordAgainInputLayout.error = getString(R.string.empty_message)
                activity?.let {
                    Toast.makeText(it,getString(R.string.passwords_not_match),Toast.LENGTH_SHORT).show()
                }
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