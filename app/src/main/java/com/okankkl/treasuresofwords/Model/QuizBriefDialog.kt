package com.okankkl.treasuresofwords.Model

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.okankkl.treasuresofwords.R

class QuizBriefDialog(var activity : FragmentActivity) {

    var backQuizPageClick : () -> Unit = {}
    var newQuizClick : () -> Unit = {}


    private lateinit var dialog : AlertDialog

    @SuppressLint("InflateParams")
    fun startLoadingDialog(correctNumber : String,wrongNumber : String,levelUpList : ArrayList<Question>,quizType : String){
        val builder = AlertDialog.Builder(activity)

        val inflater = activity.layoutInflater
        builder.setCancelable(false)

        val contactPopup = inflater.inflate(R.layout.quiz_brief_popup,null)

        val txtCorrectNumber = contactPopup.findViewById<TextView>(R.id.txtCorrectNumber)
        val txtWrongNumber = contactPopup.findViewById<TextView>(R.id.txtWrongNumber)
        val levelUpWordsListView = contactPopup.findViewById<ListView>(R.id.levelUpWordsListView)
        val btnGoQuizPage = contactPopup.findViewById<Button>(R.id.btnBackQuizPage)
        val btnNewQuiz = contactPopup.findViewById<Button>(R.id.btnNewQuiz)

        //val adapter = ArrayAdapter<String>(activity,android.R.layout.simple_list_item_1,levelUpList)
        levelUpWordsListView.adapter = LevelUpWordsAdapter(activity,levelUpList,quizType)
        levelUpWordsListView.isClickable = false

        btnGoQuizPage.setOnClickListener {
            backQuizPageClick()
        }

        btnNewQuiz.setOnClickListener {
            newQuizClick()
        }

        txtCorrectNumber.text = correctNumber
        txtWrongNumber.text = wrongNumber

        builder.setView(contactPopup)

        dialog = builder.create()


        dialog.show()

    }

    fun dismissDialog(){
        dialog.dismiss()
    }

}