package com.example.treaasuresofwords.View.Main.Word

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.treaasuresofwords.Model.User
import com.example.treaasuresofwords.R
import com.example.treaasuresofwords.databinding.FragmentWordBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class WordFragment : Fragment() {

    private var _binding : FragmentWordBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseFirestore
    private lateinit var viewModel : WordFragmentViewModel
    private var currentUser : User? = null
    private var currentLanguages : HashMap<String,String>? = null
    private lateinit var adapter : WordAdapter

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

        binding.btnGoAdd.setOnClickListener {
            val action = WordFragmentDirections.actionWordFragmentToAddWordFragment()
            findNavController().navigate(action)
        }

        viewModel.wordList.observe(viewLifecycleOwner) { wordList ->
            //wordList.forEachIndexed { index, word ->
            //    Log.w("aaa",word.word)
            //}
            val layoutManager = LinearLayoutManager(view.context)
            binding.recyclerView.layoutManager = layoutManager
            adapter= WordAdapter(wordList,view.context)
            binding.recyclerView.adapter = adapter
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        viewModel = WordFragmentViewModel(auth,db)
        _binding = FragmentWordBinding.inflate(inflater,container,false)

        return binding.root
    }

    fun fillSpinner(mContext : Context){

        val languageList = currentUser!!.languages
        val languageStringList = arrayListOf<String>()
        languageList.forEachIndexed { index, hashMap ->
            var text = hashMap["languageToTranslated"].toString() + " -> " + hashMap["translatedLanguage"].toString()
            languageStringList.add(text)
        }

        val arrayAdapter = ArrayAdapter<String>(mContext,android.R.layout.simple_spinner_dropdown_item,languageStringList)

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

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}