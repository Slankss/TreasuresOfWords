package com.example.treaasuresofwords.View.Main.Quiz

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.navigation.fragment.findNavController
import com.example.treaasuresofwords.R
import com.example.treaasuresofwords.View.Main.Quiz.MatchingQuiz.MatchingQuizFragmentDirections
import com.example.treaasuresofwords.databinding.FragmentQuizBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class QuizFragment : Fragment() {

    private var _binding : FragmentQuizBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseFirestore
    private lateinit var viewModel : QuizFragmentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    @SuppressLint("ResourceAsColor")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        viewModel.wordList.observe(viewLifecycleOwner) { wordList ->
            if(wordList.size < 10){
              changeVisibility(false)
            }
            else if(wordList.size in 10..19){
              changeVisibility(true)
            }
            else if (wordList.size > 20){
                changeVisibility(true)
            }

        }

        binding.btnMatching.setOnClickListener {

            val action = QuizFragmentDirections.actionQuizFragmentToMatchingQuizFragment()
            findNavController().navigate(action)

        }

        binding.btnTenQuestion.setOnClickListener {
            val action = QuizFragmentDirections.actionQuizFragmentToMakeQuizFragment(10)
            findNavController().navigate(action)
        }

        binding.btnTwelweQuestion.setOnClickListener {
            val action = QuizFragmentDirections.actionQuizFragmentToMakeQuizFragment(20)
            findNavController().navigate(action)
        }

        binding.btnReset.setOnClickListener {
            viewModel.reset()
        }

    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentQuizBinding.inflate(inflater,container,false)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        viewModel = QuizFragmentViewModel(auth,db)
        return binding.root
    }

    fun changeVisibility(state : Boolean){
        binding.btnMatching.isClickable = state
        binding.txtMatchingError.visibility = when(state){
            true -> View.GONE
            false -> View.VISIBLE
        }
        binding.btnTenQuestion.isClickable = state
        binding.txtTenQuestionError.visibility = when(state){
            true -> View.GONE
            false -> View.VISIBLE
        }
        binding.btnTwelweQuestion.isClickable = state
        binding.txtTwelweQuestionError.visibility = when(state){
            true -> View.GONE
            false -> View.VISIBLE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}