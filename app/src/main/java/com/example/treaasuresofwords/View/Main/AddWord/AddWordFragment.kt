package com.example.treaasuresofwords.View.Main.AddWord

import android.R
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.example.treaasuresofwords.Model.User
import com.example.treaasuresofwords.View.Main.Word.WordFragmentViewModel
import com.example.treaasuresofwords.databinding.FragmentAddWordBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class AddWordFragment : Fragment() {

    private var _binding : FragmentAddWordBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseFirestore
    private lateinit var viewModel : AddWordFragmentViewModel
    private var currentUser : User? = null
    private var currentLanguages : HashMap<String,String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.userProfile.observe(viewLifecycleOwner){ user ->
            if(user != null){
                currentUser = user
                fillSpinner(view.context)
            }
        }

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAddWordBinding.inflate(inflater,container,false)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        viewModel = AddWordFragmentViewModel(auth,db)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    fun fillSpinner(mContext : Context){

        val languageList = currentUser!!.languages
        val languageStringList = arrayListOf<String>()
        languageList.forEachIndexed { index, hashMap ->
            var text = hashMap["languageToTranslated"].toString() + " -> " + hashMap["translatedLanguage"].toString()
            languageStringList.add(text)
        }

        val arrayAdapter = ArrayAdapter<String>(mContext,
            R.layout.simple_spinner_dropdown_item,languageStringList)

        binding.spinnerSelectLanguage.adapter = arrayAdapter
        binding.spinnerSelectLanguage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                currentLanguages = hashMapOf<String,String>()
                currentLanguages!!.put("languageToTranslated",languageList.get(position)["languageToTranslated"].toString())
                currentLanguages!!.put("translatedLanguage",languageList.get(position)["translatedLanguage"].toString())

                binding.lblLanguageToTranslated.setText(currentLanguages!!["languageToTranslated"].toString())
                binding.lblTranslatedLanguage.setText(currentLanguages!!["translatedLanguage"].toString())


            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

    }

}