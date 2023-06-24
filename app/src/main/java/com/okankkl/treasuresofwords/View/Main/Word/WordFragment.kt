package com.okankkl.treasuresofwords.View.Main.Word

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.widget.doOnTextChanged
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.okankkl.treasuresofwords.Model.User
import com.okankkl.treasuresofwords.R
import com.okankkl.treasuresofwords.databinding.FragmentWordBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.okankkl.treasuresofwords.Model.Word


class WordFragment : Fragment() {

    private var _binding : FragmentWordBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseFirestore
    private lateinit var viewModel : WordFragmentViewModel
    private var currentUser : User? = null
    private lateinit var adapter : WordAdapter
    private var languageToTranslated : String? = null
    private var translatedLanguage : String? = null
    private var show = false
    private var wordList = ArrayList<Word>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layoutManager = LinearLayoutManager(view.context)
        binding.recyclerView.layoutManager = layoutManager
        adapter= WordAdapter(view.context,show)
        adapter.setData(wordList)
        binding.recyclerView.adapter = adapter

        binding.btnChaneVisibility.setOnClickListener {
            show = !show
            if(show){
                binding.btnChaneVisibility.setImageResource(R.drawable.ic_visibility_off)
            }
            else{
                binding.btnChaneVisibility.setImageResource(R.drawable.ic_visibility_on)
            }
            adapter.changeShow(show)
        }

        binding.btnGoAdd.setOnClickListener {
            val action = WordFragmentDirections.actionWordFragmentToAddWordFragment()
            findNavController().navigate(action)
        }

        viewModel.wordList.observe(viewLifecycleOwner) { observedWordList ->
            adapter.setData(observedWordList)
            adapter.notifyDataSetChanged()

            adapter.allWordSelected = { isAllWordSelected ->
                binding.checkBoxAllWords.isChecked = when(isAllWordSelected){
                    true -> true
                    false -> false
                }
            }

        }

        binding.checkBoxAllWords.setOnClickListener {
            val isChecked = binding.checkBoxAllWords.isChecked
            adapter.selectAll(isChecked)

        }

        binding.btnDelete.setOnClickListener {
            val selectArray = adapter.getSelectedWord()
            if(selectArray.isNotEmpty()){
                activity?.let {
                    val dialogBuilder = AlertDialog.Builder(it)
                    dialogBuilder.setMessage(R.string.word_delete_message)
                        .setPositiveButton(R.string.yes, DialogInterface.OnClickListener { dialog, id ->
                            viewModel.deleteWord(selectArray)
                        })
                        .setNegativeButton(R.string.no, DialogInterface.OnClickListener {
                                dialog, id -> dialog.cancel()
                        })
                    val alert = dialogBuilder.create()
                    alert.setTitle(R.string.word_delete)
                    alert.show()
                }
            }
        }

        binding.editTextSearch.doOnTextChanged { text, start, before, count ->
            if(text != null && text.trim().isNotEmpty()){
                viewModel.searchWord(text.toString())
            }
            else{
                viewModel.getWordList()
            }
        }



    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        context?.let {
            viewModel = WordFragmentViewModel(auth,db,it)
        }

        _binding = FragmentWordBinding.inflate(inflater,container,false)

        return binding.root
    }




    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}