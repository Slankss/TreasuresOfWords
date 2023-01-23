package com.example.treaasuresofwords.Model

import android.app.Activity
import android.app.AlertDialog
import androidx.fragment.app.FragmentActivity
import com.example.treaasuresofwords.R

class LoadingDialog(var activity : FragmentActivity) {



    private lateinit var dialog : AlertDialog

    fun startLoadingDialog(){
        var builder = AlertDialog.Builder(activity)

        var inflater = activity.layoutInflater
        builder.setCancelable(false)
        builder.setView(inflater.inflate(R.layout.loading_dialog,null))

        dialog = builder.create()
        dialog.show()

    }

    fun dismissDialog(){
        dialog.dismiss()
    }


}