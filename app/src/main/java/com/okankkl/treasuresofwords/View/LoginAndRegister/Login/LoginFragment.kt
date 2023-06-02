package com.okankkl.treasuresofwords.View.LoginAndRegister.Login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.okankkl.treasuresofwords.Model.LoadingDialog
import com.okankkl.treasuresofwords.R
import com.okankkl.treasuresofwords.View.LoginAndRegister.Verification.VerificationActivity
import com.okankkl.treasuresofwords.View.Main.MainActivity
import com.okankkl.treasuresofwords.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class LoginFragment : Fragment() {

    private var _binding : FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth : FirebaseAuth
    private lateinit var viewModel : LoginViewModel
    private lateinit var db : FirebaseFirestore
    private lateinit var loadingDialog : LoadingDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //binding.editTextEmail.setText("dogu_nigar_fb@hotmail.com")
        //binding.editTextPassword.setText("predatoor")

        loadingDialog = LoadingDialog(this.requireActivity())

        binding.btnBack.setOnClickListener {

            val action = LoginFragmentDirections.actionLoginFragmentToFirstPageFragment()
            findNavController().navigate(action)
        }

        binding.txtRegister.setOnClickListener {
            val action = LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
            findNavController().navigate(action)
        }

        binding.btnSignIn.setOnClickListener {
            login()
        }

        binding.btnForgotPassword.setOnClickListener {
            val action = LoginFragmentDirections.actionLoginFragmentToPasswordResetFragment()
            findNavController().navigate(action)
        }

        binding.editTextEmail.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus){
                binding.textEmailInputLayout.error = null
            }
        }

        binding.editTextPassword.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus){
                binding.passwordInputLayout.error = null
            }
        }

        viewModel.isSuccesfull.observe(viewLifecycleOwner){ isSuccesfull ->
            if(isSuccesfull){
                activity?.let {
                    val currentUser = auth.currentUser
                    currentUser?.let { current ->
                        if(current.isEmailVerified){
                            // email verified go main page
                            startActivity(Intent(it.applicationContext,MainActivity::class.java))
                        }
                        else{
                            // email not verified go verification page
                            startActivity(Intent(it.applicationContext,VerificationActivity::class.java))
                        }

                    }

                }

            }
        }

        viewModel.isComplete.observe(viewLifecycleOwner){ isComplete ->
            if(isComplete){
                loadingDialog.dismissDialog()
            }
        }


    }



    fun login(){

        val email = binding.editTextEmail.text.toString().trim()
        val password = binding.editTextPassword.text.toString().trim()

        if(email.isNotEmpty() && password.isNotEmpty()){
            loadingDialog.startLoadingDialog()
            viewModel.login(email,password)

        }

        binding.apply {
            textEmailInputLayout.error = null
            passwordInputLayout.error = null
            if(email.isEmpty()){
                textEmailInputLayout.error = getString(R.string.empty_message)
            }

            if(password.isEmpty()){
                passwordInputLayout.error = getString(R.string.empty_message)
            }
        }


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        context?.let {
            viewModel = LoginViewModel(auth,it)
        }
        _binding = FragmentLoginBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}