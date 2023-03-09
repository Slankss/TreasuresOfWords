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
                binding.btnMatching.isClickable = false
                binding.txtMatchingError.visibility = View.VISIBLE
                binding.btnTenQuestion.isClickable = false
                binding.txtTenQuestionError.visibility = View.VISIBLE
                binding.btnTwelweQuestion.isClickable = false
                binding.txtTwelweQuestionError.visibility = View.VISIBLE
            }
            else if(wordList.size in 10..19){
                binding.btnMatching.isClickable = true
                binding.txtMatchingError.visibility = View.GONE
                binding.btnTenQuestion.isClickable = true
                binding.txtTenQuestionError.visibility = View.GONE
                binding.btnTwelweQuestion.isClickable = false
                binding.txtTwelweQuestionError.visibility = View.VISIBLE
            }
            else if (wordList.size > 20){
                binding.btnMatching.isClickable = true
                binding.txtMatchingError.visibility = View.GONE
                binding.btnTenQuestion.isClickable = true
                binding.txtTenQuestionError.visibility = View.GONE
                binding.btnTwelweQuestion.isClickable = true
                binding.txtTwelweQuestionError.visibility = View.GONE
            }

        }

        binding.btnMatching.setOnClickListener {

            val action = QuizFragmentDirections.actionQuizFragmentToMatchingQuizFragment()
            findNavController().navigate(action)

        }

        binding.btnTenQuestion.setOnClickListener {
            val action = QuizFragmentDirections.actionQuizFragmentToMakeQuizFragment()
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}