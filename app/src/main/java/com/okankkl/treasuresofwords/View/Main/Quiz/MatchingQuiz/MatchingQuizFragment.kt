package com.okankkl.treasuresofwords.View.Main.Quiz.MatchingQuiz

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.okankkl.treasuresofwords.Model.Question
import com.okankkl.treasuresofwords.Model.Quiz
import com.okankkl.treasuresofwords.Model.QuizBriefDialog
import com.okankkl.treasuresofwords.Model.Word
import com.okankkl.treasuresofwords.R
import com.okankkl.treasuresofwords.databinding.FragmentMatchingQuizBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.*


class MatchingQuizFragment : Fragment() {

    private var _binding : FragmentMatchingQuizBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseFirestore
    private lateinit var viewModel : QuizViewModel

    private lateinit var translateAdapter : TranslateAdapter
    private lateinit var translatedAdapter : TranslatedAdapter
    private lateinit var quizBriefDialog: QuizBriefDialog

    var wordList = arrayListOf<HashMap<String,Any>>()
    var translateArrayList = arrayListOf<Question>()
    var translatedArrayList = arrayListOf<Question>()
    var quizState = false

    var currentTranslateIndex : Int? = null
    var currentTranslatedIndex : Int? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        quizBriefDialog = QuizBriefDialog(this.requireActivity())

        viewModel.wordList.observe(viewLifecycleOwner) { allWordList ->
            if(allWordList.isNotEmpty()){
                wordList = allWordList
                if(!quizState){
                    var randomTranslate = ""
                    do {
                        val random = (0 until allWordList.size).random() // 0 and 10 included

                        val word = wordList[random].get("word") as Word
                        val position = wordList[random].get("position") as Int
                        randomTranslate = word.word
                        val level = word.repeatTime
                        val randomTranslated = word.translate

                        var isContains = false
                        for(item in translateArrayList){
                            if(item.translate == randomTranslate){
                                isContains = true
                                break
                            }
                        }
                        if(!isContains){
                            val question = Question(randomTranslate,randomTranslated,null,position,level)
                            val question2 = Question(randomTranslate,randomTranslated,null,position,level)
                            translateArrayList.add(question)
                            translatedArrayList.add(question2)
                        }
                    }while( translateArrayList.size < 10)

                    quizState = true
                    translatedArrayList.shuffle()

                    val translateLayoutManager = LinearLayoutManager(view.context)
                    binding.translateRecyclerView.layoutManager = translateLayoutManager
                    translateAdapter= TranslateAdapter(translateArrayList,view.context)
                    binding.translateRecyclerView.adapter = translateAdapter

                    val translatedLayoutManager = LinearLayoutManager(view.context)
                    binding.translatedRecyclerView.layoutManager = translatedLayoutManager
                    translatedAdapter= TranslatedAdapter(translatedArrayList,view.context)
                    binding.translatedRecyclerView.adapter = translatedAdapter

                    translateAdapter.buttonClick  = {
                        currentTranslateIndex = it
                        matching()
                    }

                    translatedAdapter.buttonClick = {
                        currentTranslatedIndex = it
                        matching()
                    }
                }
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("NotifyDataSetChanged")
    fun matching(){

        if(currentTranslateIndex != null && currentTranslatedIndex != null){

            var translateIndex = 0
            var translatedIndex = 0

            val translateWord = translateArrayList[currentTranslateIndex!!]
            val translatedWord = translatedArrayList[currentTranslatedIndex!!]

            var state = translateWord.translate == translatedWord.translate
                    && translateWord.translated == translatedWord.translated

            translateArrayList.get(currentTranslateIndex!!).state = state
            translateIndex = currentTranslateIndex!! + 1
            if(state){
                translatedArrayList.get(currentTranslatedIndex!!).state = state
                translatedIndex = currentTranslatedIndex!! + 1
            }
            else{
                var index = 1
                for(item in translatedArrayList){
                    if(item.translate == translateWord.translate){
                        item.state = state
                        translatedIndex = index
                        break
                    }
                    index++
                }
            }


            translateAdapter.setToMatchingList(translateIndex-1,translatedIndex)
            translatedAdapter.setToMatchingList(translatedIndex-1,translateIndex)

            binding.translateRecyclerView.post {
                translateAdapter.notifyDataSetChanged()
                translateAdapter.selectedPosition = -1
            }
            binding.translatedRecyclerView.post {
                translatedAdapter.notifyDataSetChanged()
                translatedAdapter.selectedPosition = -1
            }

            currentTranslateIndex = null
            currentTranslatedIndex = null

            var result = finishCheck()
            if(result){
                update()
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
        viewModel = QuizViewModel(auth,db)

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    fun finishCheck() : Boolean{

        var result = true

        for(item in translateArrayList){
            if(item.state == null){
                result = false
                break
            }
        }

        return result
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun update(){


        val updatedList = arrayListOf<Question>()
        var correctNumber = 0
        var correctWordList = arrayListOf<String>()

        translateArrayList.forEach { question ->
            if(question.state != null && question.state == true){
                updatedList.add(question)
                correctWordList.add(question.translate)
                correctNumber++
            }
        }

        var wrongNumber = 10 - correctNumber
        if(correctNumber >= 1){
            viewModel.update(updatedList)
        }


        createDialog(correctNumber,wrongNumber,updatedList)
    }

    fun createDialog(correctNumber : Int,wrongNumber : Int,updatedList : ArrayList<Question>){

        val correctString = "$correctNumber ${getString(R.string.correct)}"
        val wrongString = "$wrongNumber ${getString(R.string.wrong)}"

        quizBriefDialog.startLoadingDialog(correctString,wrongString,updatedList, Quiz.MATCHING_QUIZ.name)

        quizBriefDialog.backQuizPageClick = {
            val action = MatchingQuizFragmentDirections.actionMatchingQuizFragmentToQuizFragment()
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