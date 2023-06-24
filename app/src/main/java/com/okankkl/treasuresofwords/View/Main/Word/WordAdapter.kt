package com.okankkl.treasuresofwords.View.Main.Word

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.okankkl.treasuresofwords.Model.Word
import com.okankkl.treasuresofwords.databinding.WordRowBinding

class WordAdapter(private var mContext : Context,var show : Boolean) :
    RecyclerView.Adapter<WordAdapter.Holder>() {

    var wordList = ArrayList<Word>()
    var selectWord = arrayListOf<Word>()
    var allWordSelected : (Boolean) -> Unit = {}

    class Holder(val binding : WordRowBinding) : RecyclerView.ViewHolder(binding.root)

    fun setData(wordList : ArrayList<Word>){
        this.wordList = wordList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = WordRowBinding.inflate(layoutInflater, parent, false)
        return WordAdapter.Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.binding.apply {
            val word = wordList[position]

            checkBoxWord.isChecked = when(selectWord.contains(word)){
                true -> true
                false -> false
            }

            txtWord.setText(word.word)
            val translate = when(show){
                true -> word.translate
                false -> "****"
            }
            txtTranlate.setText(translate)

            checkBoxWord.setOnCheckedChangeListener { buttonView, isChecked ->
                    if(isChecked){
                        if(!selectWord.contains(word)){
                            selectWord.add(word)
                        }
                    }
                    else{
                        selectWord.remove(word)
                    }

                    if(selectWord.size == wordList.size){
                        allWordSelected(true)
                    }
                    else{
                        allWordSelected(false)
                    }
            }
        }
    }

    fun getSelectedWord() : ArrayList<Word>{
        return selectWord
    }

    @SuppressLint("NotifyDataSetChanged")
    fun selectAll(state : Boolean){
        selectWord.clear()
        if(state){
            selectWord.addAll(wordList)
        }

        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun changeShow(showValue : Boolean){
        show = showValue
        notifyDataSetChanged()
    }



    override fun getItemCount(): Int {
        return wordList.size
    }

}