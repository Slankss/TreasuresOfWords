package com.example.treaasuresofwords.View.Main.Word

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.treaasuresofwords.Model.Word
import com.example.treaasuresofwords.R
import com.example.treaasuresofwords.databinding.WordRowBinding

class WordAdapter(private val wordList : ArrayList<Word>,private var mContext : Context) :
    RecyclerView.Adapter<WordAdapter.Holder>() {

    var selectClick : (String) -> Unit = {}

    class Holder(val binding : WordRowBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = WordRowBinding.inflate(layoutInflater, parent, false)
        return WordAdapter.Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        Log.w("position",position.toString())
        holder.binding.apply {
            val word = wordList[position]
            if(position == 0){
                wordLinearLayout.setBackgroundColor(Color.TRANSPARENT)
                checkBoxWord.visibility = View.INVISIBLE
                checkBoxWord.isClickable = false
            }
            else{
                if(position % 2 == 0){
                    wordLinearLayout.setBackgroundColor(mContext.getColor(R.color.word_row_second_color))
                }
                else{
                    wordLinearLayout.setBackgroundColor(mContext.getColor(R.color.word_row_first_color))

                }
                txtWord.setText(word.word)
                txtTranlate.setText(word.translate)
            }
        }
    }

    override fun getItemCount(): Int {
        return wordList.size
    }

}