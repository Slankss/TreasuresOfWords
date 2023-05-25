package com.example.treaasuresofwords.View.Main.AdminPanel

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.example.treaasuresofwords.R
import com.example.treaasuresofwords.databinding.ActivityAdminPanelBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AdminPanelActivity : AppCompatActivity() {

    private lateinit var binding : ActivityAdminPanelBinding
    private lateinit var viewModel : AdminPanelViewModel
    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseFirestore

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminPanelBinding.inflate(layoutInflater)
        setContentView(binding.root)

        try {
            this.supportActionBar!!.hide()
        } catch (e: NullPointerException) {
        }

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        viewModel = AdminPanelViewModel(auth,db,this)

        setWordLevel()


    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setWordLevel(){

        binding.btnSetZeroLevel.setOnClickListener {
            createAlertDialog(0)
        }

        binding.btnSetOneLevel.setOnClickListener {
            createAlertDialog(1)
        }

        binding.btnSetSecondLevel.setOnClickListener {
            createAlertDialog(2)
        }

        binding.btnSetThirdLevel.setOnClickListener {
            createAlertDialog(3)
        }

        binding.btnSetFourthLevel.setOnClickListener {
            createAlertDialog(4)
        }

        binding.btnSetFifthLevel.setOnClickListener {
            createAlertDialog(5)
        }


    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createAlertDialog(wordLevel : Int){

        val builder = AlertDialog.Builder(this)
            .setTitle("Sifirlama")
            .setMessage("Emin misun?")
            .setPositiveButton("Evet"){ dialog,which ->
                viewModel.setWorlLevel(wordLevel)
            }
            .setNegativeButton("Hayır"){ dialog,which ->
                dialog.dismiss()
            }

        val dialog = builder.create()
        dialog.show()

    }

}