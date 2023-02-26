package com.example.treaasuresofwords.View.Main.Profile

import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract.Profile
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import com.example.treaasuresofwords.Model.LoadingDialog
import com.example.treaasuresofwords.Model.User
import com.example.treaasuresofwords.R
import com.example.treaasuresofwords.View.LoginAndRegister.LoginAndRegisterActivity
import com.example.treaasuresofwords.View.Main.Home.ProfileFragmentViewModel
import com.example.treaasuresofwords.databinding.FragmentProfileBinding
import com.example.treaasuresofwords.databinding.FragmentWordBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.muhammed.toastoy.Toastoy


class ProfileFragment : Fragment() {

    private var _binding : FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseFirestore
    private lateinit var viewModel : ProfileFragmentViewModel
    private var currentUser : User? = null
    private lateinit var loadingDialog: LoadingDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingDialog = LoadingDialog(this.requireActivity())

        binding.apply {

            btnUpdate.visibility = View.GONE

            viewModel.user.observe(viewLifecycleOwner){ user ->
                currentUser = user
                editTextUsername.setText(user.username)
                editTextPhone.setText(user.number)

                if(user.username == editTextUsername.text.toString().trim() &&
                    user.number == editTextPhone.text.toString().trim()
                        ){
                    btnUpdate.visibility = View.GONE
                }

            }

            editTextUsername.doOnTextChanged { text, start, before, count ->
                btnUpdate.visibility = View.VISIBLE
            }

            editTextPhone.doOnTextChanged { text, start, before, count ->
                btnUpdate.visibility = View.VISIBLE
            }

            btnUpdate.setOnClickListener {
                if(currentUser != null){
                    val username = editTextUsername.text.toString()
                    val phone = editTextPhone.text.toString()
                    if(phone.trim().isEmpty()  || phone.length == 10){
                        loadingDialog.startLoadingDialog()
                        currentUser!!.username = username
                        currentUser!!.number = phone
                        viewModel.updateProfile(currentUser!!)
                    }
                    else{
                        Toast.makeText(view.context,getString(R.string.phone_length),Toast.LENGTH_SHORT).show()
                    }

                }
            }


            viewModel.isComplete.observe(viewLifecycleOwner){ isComplete ->
                if(isComplete){
                    Toastoy.showSuccessToast(view.context,getString(R.string.profile_updated))
                    viewModel.isComplete.value = false
                }
            }

            viewModel.isSuccesfull.observe(viewLifecycleOwner){ isSuccesfull ->
                if(isSuccesfull){
                    loadingDialog.dismissDialog()
                    viewModel.isSuccesfull.value = false
                }
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
        viewModel = ProfileFragmentViewModel(auth,db)
        _binding = FragmentProfileBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}