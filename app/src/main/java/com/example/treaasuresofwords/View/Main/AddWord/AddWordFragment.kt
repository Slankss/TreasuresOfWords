package com.example.treaasuresofwords.View.Main.AddWord

//noinspection SuspiciousImport
import android.R.layout.simple_spinner_dropdown_item
import android.annotation.SuppressLint
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
import com.example.treaasuresofwords.Model.LoadingDialog
import com.example.treaasuresofwords.Model.User
import com.example.treaasuresofwords.Model.Word
import com.example.treaasuresofwords.R
import com.example.treaasuresofwords.View.Main.Word.WordAdapter
import com.example.treaasuresofwords.databinding.FragmentAddWordBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import kotlin.collections.HashMap


class AddWordFragment : Fragment() {

    private var _binding : FragmentAddWordBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseFirestore
    private lateinit var viewModel : AddWordFragmentViewModel
    private var currentUser : User? = null
    private var currentLanguages : HashMap<String,String>? = null
    private lateinit var loadingDialog : LoadingDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    @SuppressLint("SuspiciousIndentation")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingDialog = LoadingDialog(this.requireActivity())

        viewModel.userProfile.observe(viewLifecycleOwner){ user ->
            if(user != null){
                currentUser = user
                fillSpinner(view.context)
            }
        }

        binding.apply {

            editTextWord.setOnFocusChangeListener { v, hasFocus ->
                if(hasFocus){
                    wordInputLayout.error = null
                }
            }

            editTextTranslate.setOnFocusChangeListener { v, hasFocus ->
                if(hasFocus){
                    translateInputLayout.error = null
                }
            }

            btnWordAdd.setOnClickListener {
            val word = binding.editTextWord.text.toString().trim()
            val translate = binding.editTextTranslate.text.toString().trim()

                if(check(word,translate)){
                    val lower_word = word.lowercase()
                    val lower_translate = translate.lowercase()
                    if(currentLanguages != null){
                        loadingDialog.startLoadingDialog()
                        val languageToTranslated = currentLanguages!!["languageToTranslated"].toString()
                        val translatedLanguage = currentLanguages!!["translatedLanguage"].toString()
                        val word = Word(languageToTranslated,translatedLanguage,lower_word,lower_translate,0)

                        viewModel.addWord(word)
                    }

                }
            }

        }

        viewModel.isSuccesfull.observe(viewLifecycleOwner) { isSuccesfull ->
            if(isSuccesfull){
                val action = AddWordFragmentDirections.actionAddWordFragmentToWordFragment()
                findNavController().navigate(action)
            }
        }

        viewModel.isComplete.observe(viewLifecycleOwner){ isComplete ->
            if(isComplete){
                loadingDialog.dismissDialog()
            }
        }
    }

    fun check(word : String,translate : String) : Boolean{

        binding.wordInputLayout.error = null
        binding.translateInputLayout.error = null

        if(word.isNotEmpty() && translate.isNotEmpty()){
            return true
        }

        if(word.isEmpty()){ binding.wordInputLayout.error = getString(R.string.empty_message) }
        if(translate.isEmpty()) { binding.translateInputLayout.error = getString(R.string.empty_message) }

        return false
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAddWordBinding.inflate(inflater,container,false)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        context?.let {
            viewModel = AddWordFragmentViewModel(auth,db,it)
        }

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
            simple_spinner_dropdown_item,languageStringList)

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