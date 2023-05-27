package com.okankkl.treasuresofwords.Model

import android.annotation.SuppressLint
import android.app.AlertDialog
import androidx.fragment.app.FragmentActivity
import com.okankkl.treasuresofwords.R

class LoadingDialog(var activity : FragmentActivity) {



    private lateinit var dialog : AlertDialog

    @SuppressLint("InflateParams")
    fun startLoadingDialog(){
        val builder = AlertDialog.Builder(activity)

        val inflater = activity.layoutInflater
        builder.setCancelable(false)
        builder.setView(inflater.inflate(R.layout.loading_dialog,null))

        dialog = builder.create()
        dialog.show()

    }

    fun dismissDialog(){
        dialog.dismiss()
    }


}