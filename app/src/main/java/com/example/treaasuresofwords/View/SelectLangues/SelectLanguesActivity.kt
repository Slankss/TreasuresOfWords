package com.example.treaasuresofwords.View.SelectLangues

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import com.example.treaasuresofwords.databinding.ActivitySelectLanguesBinding

class SelectLanguesActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySelectLanguesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectLanguesBinding.inflate(layoutInflater)
        setContentView(binding.root)




    }



}