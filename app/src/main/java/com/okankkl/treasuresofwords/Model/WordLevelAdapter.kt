package com.okankkl.treasuresofwords.Model

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.okankkl.treasuresofwords.R
import com.okankkl.treasuresofwords.R.layout.word_level_row

class WordLevelAdapter( private val context : Activity,private val wordList : List<Word>)
    : ArrayAdapter<Word>(context, word_level_row,wordList)
{
     @SuppressLint("ViewHolder", "InflateParams", "MissingInflatedId")
     override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

         val inflater = LayoutInflater.from(context)
         val view = inflater.inflate(word_level_row,null)
         val txtWordLevel = view.findViewById<TextView>(R.id.txtWordLevel)
         txtWordLevel.text = wordList[position].word

         return view
     }

}