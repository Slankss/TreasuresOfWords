package com.example.treaasuresofwords.View.Main.Quiz.QuestionQuiz

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.annotation.RequiresApi
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.treaasuresofwords.Model.Question
import com.example.treaasuresofwords.Model.QuizBriefDialog
import com.example.treaasuresofwords.Model.Word
import com.example.treaasuresofwords.R
import com.example.treaasuresofwords.View.Main.Quiz.MatchingQuiz.MatchingQuizFragmentDirections
import com.example.treaasuresofwords.View.Main.Quiz.MatchingQuiz.QuizViewModel
import com.example.treaasuresofwords.View.Main.Quiz.MatchingQuiz.TranslateAdapter
import com.example.treaasuresofwords.View.Main.Quiz.MatchingQuiz.TranslatedAdapter
import com.example.treaasuresofwords.databinding.FragmentMakeQuizBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class MakeQuizFragment : Fragment() {

    private var _binding : FragmentMakeQuizBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseFirestore
    private lateinit var viewModel : QuizViewModel
    private lateinit var quizBriefDialog: QuizBriefDialog
    private var lastClicked : RadioButton? = null
    var currentLanguage = "en"

    var wordList = arrayListOf<HashMap<String,Any>>()
    var questionList = arrayListOf<Question>()
    var quizState = false
    var currentQuestionIndex = 0
    var selectedAnswer = ""

    private val args : MakeQuizFragmentArgs by navArgs()
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

        questionLimit = args.questionLimit

        quizBriefDialog = QuizBriefDialog(this.requireActivity())

        viewModel.wordList.observe(viewLifecycleOwner) { allWordList ->
            if(allWordList.isNotEmpty()){
                wordList = allWordList
                if(!quizState){
                    var randomWord : Word
                    do {
                        val random = (0 until allWordList.size).random() // 0 and 10 included
                        randomWord = wordList[random].get("word") as Word
                        val position = wordList[random].get("position") as Int
                        val answerList = arrayListOf<String>()
                        answerList.add(randomWord.translate)
                        var level = randomWord.repeatTime

                        do{
                            val randomAnswer = (0 until allWordList.size).random()
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
                    if(lastClicked != null){
                        lastClicked!!.isChecked = false
                        lastClicked = radioBtnAnswer1
                    }
                    else{
                        lastClicked = radioBtnAnswer1
                    }
                    selectedAnswer = radioBtnAnswer1.text.toString()

                }
            }


            radioBtnAnswer2.setOnCheckedChangeListener { buttonView, isChecked ->
                if(isChecked){
                    if(lastClicked != null){
                        lastClicked!!.isChecked = false
                        lastClicked = radioBtnAnswer2
                    }
                    else{
                        lastClicked = radioBtnAnswer2
                    }
                    selectedAnswer = radioBtnAnswer2.text.toString()
                }
            }


            radioBtnAnswer3.setOnCheckedChangeListener { buttonView, isChecked ->
                if(isChecked){
                    if(lastClicked != null){
                        lastClicked!!.isChecked = false
                        lastClicked = radioBtnAnswer3
                    }
                    else{
                        lastClicked = radioBtnAnswer3
                    }
                    selectedAnswer = radioBtnAnswer3.text.toString()
                }
            }


            radioBtnAnswer4.setOnCheckedChangeListener { buttonView, isChecked ->
                if(isChecked){
                    if(lastClicked != null){
                        lastClicked!!.isChecked = false
                        lastClicked = radioBtnAnswer4
                    }
                    else{
                        lastClicked = radioBtnAnswer4
                    }
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
        _binding = FragmentMakeQuizBinding.inflate(inflater,container,false)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        viewModel = QuizViewModel(auth,db)
        return binding.root
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
                txtQuestionNumber.text = "$currentQuestionIndex/$questionLimit"
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun answerQuestion(){

        if(selectedAnswer.isNotEmpty()){
            questionList[currentQuestionIndex].state = questionList[currentQuestionIndex].translated == selectedAnswer
            Log.w("tag",questionList[currentQuestionIndex].translated)
            Log.w("tag",selectedAnswer)

            currentQuestionIndex++
            lastClicked = null
            binding.apply {
                radioBtnAnswer1.isChecked = false
                radioBtnAnswer2.isChecked = false
                radioBtnAnswer3.isChecked = false
                radioBtnAnswer4.isChecked = false
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
        var correctNumber = 0
        var correctWordList = arrayListOf<String>()

        questionList.forEach { question ->
            if(question.state != null && question.state == true){
                updatedList.add(question)
                correctWordList.add(question.translate)
                correctNumber++
            }
        }

        var wrongNumber = questionLimit - correctNumber
        if(correctNumber >= 1){
            viewModel.update(updatedList)
        }


        createDialog(correctNumber,wrongNumber,updatedList)
    }

    fun createDialog(correctNumber : Int,wrongNumber : Int,updatedList : ArrayList<Question>){

        val correctString = "$correctNumber ${getString(R.string.correct)}"
        val wrongString = "$wrongNumber ${getString(R.string.wrong)}"

        quizBriefDialog.startLoadingDialog(correctString,wrongString,updatedList)

        quizBriefDialog.backQuizPageClick = {
            val action = MakeQuizFragmentDirections.actionMakeQuizFragmentToQuizFragment()
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