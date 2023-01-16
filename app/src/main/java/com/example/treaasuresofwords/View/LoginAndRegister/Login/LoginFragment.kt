package com.example.treaasuresofwords.View.LoginAndRegister.Login

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import com.example.treaasuresofwords.R
import com.example.treaasuresofwords.View.LoginAndRegister.Register.RegisterFragmentDirections
import com.example.treaasuresofwords.View.SelectLangues.SelectLanguesActivity
import com.example.treaasuresofwords.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth
import kotlin.math.log


class LoginFragment : Fragment() {

    private var _binding : FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth : FirebaseAuth
    private lateinit var viewModel : LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

        viewModel.isSuccesfull.observe(viewLifecycleOwner){ isSuccesfull ->
            if(isSuccesfull){
                activity?.let {
                    startActivity(Intent(it.applicationContext,SelectLanguesActivity::class.java))
                    it.finish()
                }

            }
        }


    }

    fun login(){

        val email = binding.editTextEmail.text.toString().trim()
        val password = binding.editTextPassword.text.toString().trim()

        if(email.isNotEmpty() && password.isNotEmpty()){
            viewModel.login(email,password)

        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        auth = FirebaseAuth.getInstance()
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