package com.example.treaasuresofwords.View.Main.Quiz.MatchingQuiz

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
import com.example.treaasuresofwords.Model.Question
import com.example.treaasuresofwords.Model.QuizBriefDialog
import com.example.treaasuresofwords.Model.Word
import com.example.treaasuresofwords.R
import com.example.treaasuresofwords.databinding.FragmentMatchingQuizBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.muhammed.toastoy.Toastoy
import kotlinx.coroutines.*


class MatchingQuizFragment : Fragment() {

    private var _binding : FragmentMatchingQuizBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseFirestore
    private lateinit var viewModel : MatchingQuizFragmentViewModel

    private lateinit var translateAdapter : TranslateAdapter
    private lateinit var translatedAdapter : TranslatedAdapter
    private lateinit var quizBriefDialog: QuizBriefDialog

    var wordList = arrayListOf<Word>()
    var translateArrayList = arrayListOf<Question>()
    var translatedArrayList = arrayListOf<Question>()
    var quizState = false

    var currentTranslateIndex : Int? = null
    var currentTranslatedIndex : Int? = null
    var resultList= ArrayList<HashMap<String,Any>>()


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
                        randomTranslate = wordList[random].word
                        var level = wordList[random].repeatTime
                        val randomTranslated = wordList[random].translate

                        var isContains = false
                        for(item in translateArrayList){
                            if(item.translate == randomTranslate){
                                isContains = true
                                break
                            }
                        }
                        if(!isContains){
                            val question = Question(randomTranslate,randomTranslated,null,random,level)
                            val question2 = Question(randomTranslate,randomTranslated,null,random,level)
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

    fun createAdapter(){

    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("NotifyDataSetChanged")
    fun matching(){

        if(currentTranslateIndex != null && currentTranslatedIndex != null){

            val translateWord = translateArrayList[currentTranslateIndex!!]
            val translatedWord = translatedArrayList[currentTranslatedIndex!!]

            var state = translateWord.translate == translatedWord.translate
                    && translateWord.translated == translatedWord.translated

            translateArrayList.get(currentTranslateIndex!!).state = state
            if(state){
                translatedArrayList.get(currentTranslatedIndex!!).state = state
            }
            else{
                for(item in translatedArrayList){
                    if(item.translate == translateWord.translate){
                        item.state = state
                        break
                    }
                }
            }

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
        viewModel = MatchingQuizFragmentViewModel(auth,db)

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
        viewModel.update(updatedList)

        createDialog(correctNumber,wrongNumber,updatedList)
    }

    fun createDialog(correctNumber : Int,wrongNumber : Int,updatedList : ArrayList<Question>){

        val correctString = "$correctNumber ${getString(R.string.correct)}"
        val wrongString = "$wrongNumber ${getString(R.string.wrong)}"

        quizBriefDialog.startLoadingDialog(correctString,wrongString,updatedList)

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