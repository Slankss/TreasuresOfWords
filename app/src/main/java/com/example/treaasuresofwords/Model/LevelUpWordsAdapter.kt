package com.example.treaasuresofwords.Model

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.treaasuresofwords.R

class LevelUpWordsAdapter(private val context : Activity, private val arrayList : ArrayList<Question>) : ArrayAdapter<Question>(context,
R.layout.level_up_word_row,arrayList)
{

    @SuppressLint("ViewHolder", "InflateParams")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val inflater : LayoutInflater = LayoutInflater.from(context)
        val view : View = inflater.inflate(R.layout.level_up_word_row,null)

        val txtCorrectWord = view.findViewById<TextView>(R.id.txtCorrectWord)
        val txtRepeatTime = view.findViewById<TextView>(R.id.txtRepeatTime)

        txtCorrectWord.text = arrayList[position].translate
        txtRepeatTime.text = "${arrayList[position].level} -> ${arrayList[position].level + 1}"


        return view
    }

}