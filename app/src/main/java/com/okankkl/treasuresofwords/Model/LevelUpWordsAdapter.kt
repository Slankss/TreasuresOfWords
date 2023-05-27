package com.okankkl.treasuresofwords.Model

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.okankkl.treasuresofwords.R

class LevelUpWordsAdapter(private val context : Activity, private val arrayList : ArrayList<Question>,var quizType : String)
    : ArrayAdapter<Question>(context, R.layout.level_up_word_row,arrayList)
{

    @SuppressLint("ViewHolder", "InflateParams")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val inflater : LayoutInflater = LayoutInflater.from(context)
        val view : View = inflater.inflate(R.layout.level_up_word_row,null)

        val txtCorrectWord = view.findViewById<TextView>(R.id.txtCorrectWord)
        val txtRepeatTime = view.findViewById<TextView>(R.id.txtRepeatTime)
        val txtLevelUp = view.findViewById(R.id.txtLevelUp) as TextView

        txtCorrectWord.text = arrayList[position].translate
        if(quizType == Quiz.REPEAT_QUIZ.name){
            txtRepeatTime.text = "${arrayList[position].level} -> ${arrayList[position].level - 1}"
            txtLevelUp.setTextColor(Color.RED)
            txtLevelUp.setText(context.getString(R.string.level_dropped))
        }
        else{
            txtRepeatTime.text = "${arrayList[position].level} -> ${arrayList[position].level + 1}"
            txtLevelUp.setTextColor(Color.GREEN)
            txtLevelUp.setText(context.getString(R.string.level_up))
        }



        return view
    }

}