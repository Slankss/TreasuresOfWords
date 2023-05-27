package com.okankkl.treasuresofwords.View.Main.Quiz

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.navigation.fragment.findNavController
import com.okankkl.treasuresofwords.databinding.FragmentQuizBinding
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

        var stateMathing = false
        var stateTenQuestion = false
        var stateTwelweQuestion = false

        viewModel.wordList.observe(viewLifecycleOwner) { wordList ->
            stateMathing = wordList.size >= 15
            stateTenQuestion = wordList.size >= 15
            stateTwelweQuestion = wordList.size >= 25
            changeVisibility(stateMathing,stateTenQuestion,stateTwelweQuestion)

        }

        viewModel.learnedWordList.observe(viewLifecycleOwner) { learnedWordList ->
            if(learnedWordList.size < 15){
                binding.btnRepeat.isClickable = false
                binding.txtRepeatError.visibility = View.VISIBLE
            }
            else{
                binding.btnRepeat.isClickable = true
                binding.txtRepeatError.visibility = View.GONE
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

        binding.btnRepeat.setOnClickListener {
            val action = QuizFragmentDirections.actionQuizFragmentToRepeatQuizFragment()
            findNavController().navigate(action)
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

    fun changeVisibility(stateMathing : Boolean,stateTenQuestion : Boolean,stateTwelweQuestion : Boolean){
        binding.btnMatching.isClickable = stateMathing
        binding.txtMatchingError.visibility = when(stateMathing){
            true -> View.GONE
            false -> View.VISIBLE
        }
        binding.btnTenQuestion.isClickable = stateTenQuestion
        binding.txtTenQuestionError.visibility = when(stateTenQuestion){
            true -> View.GONE
            false -> View.VISIBLE
        }
        binding.btnTwelweQuestion.isClickable = stateTwelweQuestion
        binding.txtTwelweQuestionError.visibility = when(stateTwelweQuestion){
            true -> View.GONE
            false -> View.VISIBLE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}