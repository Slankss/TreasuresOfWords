package com.okankkl.treasuresofwords.View.Main.Quiz.MatchingQuiz

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.okankkl.treasuresofwords.Model.Question
import com.okankkl.treasuresofwords.R
import com.okankkl.treasuresofwords.databinding.MatchingRowBinding


class TranslateAdapter(private var translateArrayList : ArrayList<Question>,var mContext : Context)
    : RecyclerView.Adapter<TranslateAdapter.HolderView>() {

    var buttonClick : (Int) -> Unit = {}
    var selectedPosition = -1
    var matchingList = arrayListOf<Int?>()
    var lastClicked : RadioButton? = null

    init {
        for(i in 0..9){
            matchingList.add(null)
        }

    }

    fun setToMatchingList(position: Int,index: Int){
        matchingList[position] = index
    }

    class HolderView(val binding : MatchingRowBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderView {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = MatchingRowBinding.inflate(layoutInflater, parent, false)
        return HolderView(binding)
    }

    override fun onBindViewHolder(holder: HolderView, position: Int) {

        var current = translateArrayList[position]

        holder.binding.apply {

            txtPosition.text = (position+1).toString()
            var text = when(matchingList[position]){
                null -> current.translate
                else -> {
                    current.translate +"  ("+(matchingList[position])+")"
                }
            }

            var state : Boolean? = translateArrayList[position].state

            radioButton.text = text


            if(state != null){
                if(state){
                    radioButton.isChecked = false
                    radioButton.isClickable = false
                    radioButton.background = ContextCompat.getDrawable(mContext, R.drawable.matchin_correct_card)
                }
                else{
                    radioButton.isChecked = false
                    radioButton.isClickable = false
                    radioButton.background = ContextCompat.getDrawable(mContext, R.drawable.matching_wrong_card)
                }
            }
            else{
                radioButton.isClickable = true
                radioButton.background =  ContextCompat.getDrawable(mContext, R.drawable.matching_card)
            }
            radioButton.isChecked = selectedPosition == position

            radioButton.setOnCheckedChangeListener { buttonView, isChecked ->
                if(isChecked){
                    selectedPosition = position

                    if(lastClicked == null){
                        lastClicked = radioButton
                    }
                    else{
                        lastClicked!!.isChecked = false
                        lastClicked = radioButton
                    }
                    buttonClick(position)
                }
            }
        }
    }


    override fun getItemCount(): Int {
        return translateArrayList.size
    }


}