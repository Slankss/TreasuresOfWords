package com.example.treaasuresofwords.View.Main.Quiz.MatchingQuiz

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.example.treaasuresofwords.Model.Word
import com.example.treaasuresofwords.R
import com.example.treaasuresofwords.databinding.FragmentMakeQuizBinding
import com.example.treaasuresofwords.databinding.FragmentMatchingQuizBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.random.Random


class MatchingQuizFragment : Fragment() {

    private var _binding : FragmentMatchingQuizBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseFirestore
    private lateinit var viewModel : MatchingQuizFragmentViewModel

    var wordList = arrayListOf<Word>()
    var translateArrayList = arrayListOf<String>()
    var translatedArrayList = arrayListOf<String>()
    var quizState = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        viewModel.wordList.observe(viewLifecycleOwner) { allWordList ->
            if(allWordList.isNotEmpty()){
                wordList = allWordList
                if(!quizState){

                    var numberList = arrayListOf<Int>(0,1,2,3,4,5,6,7,8,9)
                    var randomTranslate = ""
                    do {
                        val random = (0 until allWordList.size).random() // 0 and 10 included
                        randomTranslate = wordList[random].word
                        val randomTranslated = wordList[random].translate

                        if(!translateArrayList.contains(randomTranslate)){
                            translateArrayList.add(randomTranslate)
                            translatedArrayList.add(randomTranslated)
                        }
                    }while( translateArrayList.size < 10)

                    quizState = true
                    translatedArrayList.shuffle()
                    translateArrayList.forEach {
                        Log.d("aaa","Translate : $it")
                    }
                    translatedArrayList.forEach {
                        Log.d("aaa","Translated : $it")
                    }


                }
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentMatchingQuizBinding.inflate(inflater,container,false)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        viewModel = MatchingQuizFragmentViewModel(auth,db)

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    fun makeQuiz(){

    }


}