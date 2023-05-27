package com.okankkl.treasuresofwords.View.Main.Quiz.Repeat

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.navigation.fragment.findNavController
import com.okankkl.treasuresofwords.Model.Question
import com.okankkl.treasuresofwords.Model.Quiz
import com.okankkl.treasuresofwords.Model.QuizBriefDialog
import com.okankkl.treasuresofwords.Model.Word
import com.okankkl.treasuresofwords.R
import com.okankkl.treasuresofwords.View.Main.Quiz.MatchingQuiz.QuizViewModel
import com.okankkl.treasuresofwords.databinding.FragmentRepeatQuizBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class RepeatQuizFragment : Fragment() {

    private var _binding : FragmentRepeatQuizBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseFirestore
    private lateinit var viewModel : QuizViewModel
    private lateinit var quizBriefDialog: QuizBriefDialog
    var currentLanguage = "en"

    var wordList = arrayListOf<HashMap<String,Any>>()
    var questionList = arrayListOf<Question>()
    var quizState = false
    var currentQuestionIndex = 0
    var selectedAnswer = ""
    var questionLimit = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.let {

            it.getSharedPreferences("User_Local_Data", Context.MODE_PRIVATE)
                ?.let {
                    val current_language = it.getString("current_language","en")
                    current_language?.let { current ->
                        currentLanguage = current
                    }
                }
        }

        quizBriefDialog = QuizBriefDialog(this.requireActivity())

        viewModel.learnedList.observe(viewLifecycleOwner) { list ->
            Log.w("ARB","OBSERVE")
            Log.w("ARB","learned wordlist size = ${list.size}")
            if(list.isNotEmpty()){
                wordList = list
                Log.w("ARAA","learned wordlist not empty")
                if(!quizState){
                    var randomWord : Word
                    var i=1
                    do {
                        Log.w("ARABAM3","WHİLE GİRDİ ($i)")
                        i++
                        val random = (0 until list.size).random() // 0 and 10 included
                        randomWord = wordList[random].get("word") as Word
                        val position = wordList[random].get("position") as Int
                        val answerList = arrayListOf<String>()
                        answerList.add(randomWord.translate)

                        do{
                            val randomAnswer = (0 until list.size).random()
                            val answer = wordList[randomAnswer].get("word") as Word
                            if(!answerList.contains(answer.translate)){
                                answerList.add(answer.translate)
                            }
                        }while(answerList.size < 4)
                        answerList.shuffle()

                        val question = Question(randomWord.word,randomWord.translate,null,position
                            ,randomWord.repeatTime,answerList)


                        questionList.add(question)
                        wordList.removeAt(random)

                    }while( questionList.size < questionLimit)

                    quizState = true
                    createQuestion()
                }
            }
        }

        binding.apply {
            radioBtnAnswer1.setOnCheckedChangeListener { buttonView, isChecked ->
                if(isChecked){
                    selectedAnswer = radioBtnAnswer1.text.toString()
                }
            }


            radioBtnAnswer2.setOnCheckedChangeListener { buttonView, isChecked ->
                if(isChecked){
                    selectedAnswer = radioBtnAnswer2.text.toString()
                }
            }


            radioBtnAnswer3.setOnCheckedChangeListener { buttonView, isChecked ->
                if(isChecked){
                    selectedAnswer = radioBtnAnswer3.text.toString()
                }
            }


            radioBtnAnswer4.setOnCheckedChangeListener { buttonView, isChecked ->
                if(isChecked){
                    selectedAnswer = radioBtnAnswer4.text.toString()
                }
            }

            btnAnswer.setOnClickListener {
                answerQuestion()
            }
        }


    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentRepeatQuizBinding.inflate(inflater,container,false)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        viewModel = QuizViewModel(auth,db)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    @SuppressLint("SetTextI18n")
    fun createQuestion(){

        val question = questionList[currentQuestionIndex]
        var questionHeader = getString(R.string.question_header)


        binding.apply {
            var text = when(currentLanguage) {
                "tr" -> "'${question.translate}'\t$questionHeader "
                else -> {
                    "$questionHeader\t'${question.translate}'"
                }
            }
            txtQuestion.text = text
            radioBtnAnswer1.text = question.answerList[0]
            radioBtnAnswer2.text = question.answerList[1]
            radioBtnAnswer3.text = question.answerList[2]
            radioBtnAnswer4.text = question.answerList[3]
            txtQuestionNumber.text = "${currentQuestionIndex+1}/$questionLimit"
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun answerQuestion(){

        if(selectedAnswer.isNotEmpty()){
            questionList[currentQuestionIndex].state = questionList[currentQuestionIndex].translated == selectedAnswer
            Log.w("tag",questionList[currentQuestionIndex].translated)
            Log.w("tag",selectedAnswer)

            currentQuestionIndex++

            binding.apply {
                radioGroup.clearCheck()
            }

            if(currentQuestionIndex == questionLimit){
                update()
            }
            else{
                createQuestion()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun update(){

        val updatedList = arrayListOf<Question>()
        var wrongNumber = 0
        var correctNumber = 0
        var correctWordList = arrayListOf<String>()

        questionList.forEach { question ->
            if(question.state != null && question.state == false){
                updatedList.add(question)
                correctWordList.add(question.translate)
                wrongNumber++
            }
        }

        correctNumber = 10 - wrongNumber
        if(wrongNumber >= 1){
            viewModel.updateRepeatQuiz(updatedList)
        }


        createDialog(correctNumber,wrongNumber,updatedList)
    }

    fun createDialog(correctNumber : Int,wrongNumber : Int,updatedList : ArrayList<Question>){

        val correctString = "$correctNumber ${getString(R.string.correct)}"
        val wrongString = "$wrongNumber ${getString(R.string.wrong)}"

        quizBriefDialog.startLoadingDialog(correctString,wrongString,updatedList, Quiz.REPEAT_QUIZ.name)

        quizBriefDialog.backQuizPageClick = {
            val action = RepeatQuizFragmentDirections.actionRepeatQuizFragmentToQuizFragment()
            findNavController().navigate(action)
            quizBriefDialog.dismissDialog()
        }

        quizBriefDialog.newQuizClick = {
            /*
            var frg: Fragment? = null
            frg = getSupportFragmentManager().findFragmentByTag("Your_Fragment_TAG")
            val ft: FragmentTransaction = getSupportFragmentManager().beginTransaction()
            ft.detach(frg)
            ft.attach(frg)
            ft.commit()

             */
        }
    }



}