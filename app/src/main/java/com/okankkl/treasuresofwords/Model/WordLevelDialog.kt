package com.okankkl.treasuresofwords.Model

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.FragmentActivity
import com.okankkl.treasuresofwords.R

class WordLevelDialog(val activity : FragmentActivity) {

    private lateinit var dialog : AlertDialog
    @SuppressLint("InflateParams", "MissingInflatedId")
    fun startDialog(words : List<Word>){

        val builder = AlertDialog.Builder(activity)

        val inflater = activity.layoutInflater

        val contactPopup = inflater.inflate(R.layout.word_level_popup,null)

        val wordListView = contactPopup.findViewById<ListView>(R.id.wordListView)

        wordListView.adapter = WordLevelAdapter(activity,words)

        builder.setView(contactPopup)

        dialog = builder.create()


        dialog.show()
    }

    fun dismissDialog(){
        dialog.dismiss()
    }
}