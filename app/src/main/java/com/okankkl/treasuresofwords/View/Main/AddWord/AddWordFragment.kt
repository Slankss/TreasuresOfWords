package com.okankkl.treasuresofwords.View.Main.AddWord

//noinspection SuspiciousImport
import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.okankkl.treasuresofwords.Model.LoadingDialog
import com.okankkl.treasuresofwords.Model.User
import com.okankkl.treasuresofwords.Model.Word
import com.okankkl.treasuresofwords.R
import com.okankkl.treasuresofwords.databinding.FragmentAddWordBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions
import java.time.LocalDateTime
import java.util.*


class AddWordFragment : Fragment() {

    private var _binding : FragmentAddWordBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseFirestore
    private lateinit var viewModel : AddWordFragmentViewModel
    private var currentUser : User? = null
    private lateinit var loadingDialog : LoadingDialog
    private var languageModelsDowloaded = false
    private var options : FirebaseTranslatorOptions? = null
    private var englishTurkishTranslator : FirebaseTranslator? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SuspiciousIndentation")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        loadingDialog = LoadingDialog(this.requireActivity())


        /*
        options = FirebaseTranslatorOptions.Builder()
            .setSourceLanguage(FirebaseTranslateLanguage.EN)
            .setTargetLanguage(FirebaseTranslateLanguage.TR)
            .build()

        options?.let {
            englishTurkishTranslator  = FirebaseNaturalLanguage.getInstance().getTranslator(it)
        }



        englishTurkishTranslator?.let {
            it.downloadModelIfNeeded()
                .addOnCompleteListener { task->
                    if(task.isSuccessful){
                        languageModelsDowloaded = true
                    }
                }
                .addOnFailureListener {

                }
        }



        binding.btnTranslate.setOnClickListener {
            var word = binding.editTextWord.text.toString()
            if(languageModelsDowloaded){
                if(word.isNotBlank()){
                    translate(word)
                }
            }
            else{
                var errorMsg = getString(R.string.models_not_ready)
                Toast.makeText(context,errorMsg,Toast.LENGTH_SHORT).show()
            }

        }


         */



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

                    loadingDialog.startLoadingDialog()

                    val currentDate = LocalDateTime.now()
                    val day = currentDate.dayOfMonth.toString()
                    val month = currentDate.monthValue.toString()
                    val year = currentDate.year.toString()

                    val dayString = when(day.length){
                        1 -> "0$day"
                        else -> { day}
                    }
                    val monthString = when(month.length){
                        1 -> "0$month"
                        else -> { month}
                    }
                    val dateString = "$dayString-$monthString-$year"

                    val word = Word(lower_word,lower_translate,0,dateString,"",0)

                    viewModel.addWord(word)
                }
            }

        }

        viewModel.isSuccesfull.observe(viewLifecycleOwner) { isSuccesfull ->
            if(isSuccesfull){
                binding.editTextWord.setText("")
                binding.editTextTranslate.setText("")
                binding.editTextWord.requestFocus()
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


    /*
    fun translate(translate: String){

        if(englishTurkishTranslator != null && options != null){
            binding.translateProgressBar.visibility = View.VISIBLE
            binding.btnTranslate.isClickable = false
            englishTurkishTranslator!!.translate(translate).addOnSuccessListener { translatedText ->
                binding.editTextTranslate.setText(translatedText)
                binding.translateProgressBar.visibility = View.GONE
                binding.btnTranslate.isClickable = true
            }.addOnFailureListener { e ->
                Log.w("ARABAM",e.localizedMessage)
            }
        }
        else{
            var errorMsg = getString(R.string.models_not_ready)
            Toast.makeText(context,errorMsg,Toast.LENGTH_SHORT).show()
        }


    }



     */


}