package com.example.treaasuresofwords.View.Main.Home

import android.animation.AnimatorListenerAdapter
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.TranslateAnimation
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.treaasuresofwords.R
import com.example.treaasuresofwords.View.LoginAndRegister.LoginAndRegisterActivity
import com.example.treaasuresofwords.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.muhammed.toastoy.Toastoy


class HomeFragment : Fragment() {

    private var _binding : FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth : FirebaseAuth

    private var allWordNumber = 3000
    private var learningWordNumber = 2500
    private var isTopMenuOpen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        binding.circularProgressBar.apply {
            progressMax = allWordNumber.toFloat()
            setProgressWithAnimation(learningWordNumber.toFloat(),2000)
            progressBarWidth = 20f
            backgroundProgressBarWidth = 20f
            roundBorder = true
            backgroundProgressBarColor = context.getColor(R.color.progress_bar_all_color)
            progressBarColor = context.getColor(R.color.progress_bar_progress_color)

        }

        val activeText = ContextCompat.getColor(view.context,R.color.word_filter_active_text_color)
        var inActiveText = ContextCompat.getColor(view.context,R.color.words_filter_text_color)

        binding.radioBtnThisWeek.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                binding.radioBtnThisWeek.setTextColor(activeText)
            }
            else{
                binding.radioBtnThisWeek.setTextColor(inActiveText)
            }
        }

        binding.radioBtnThisMonth.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                binding.radioBtnThisMonth.setTextColor(activeText)
            }
            else{
                binding.radioBtnThisMonth.setTextColor(inActiveText)
            }
        }

        binding.radioBtnThreeMonths.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                binding.radioBtnThreeMonths.setTextColor(activeText)
            }
            else{
                binding.radioBtnThreeMonths.setTextColor(inActiveText)
            }
        }

        binding.radioBtnAllTime.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                binding.radioBtnAllTime.setTextColor(activeText)
            }
            else{
                binding.radioBtnAllTime.setTextColor(inActiveText)
            }
        }


        var topMenuWidth  = 400
        var translationWidth = -topMenuWidth
        binding.btnOpenTopMenu.setOnClickListener {

            when(isTopMenuOpen){
                false -> {
                    translationWidth = -topMenuWidth
                    binding.topMenu.animate().translationX(translationWidth.toFloat()).setDuration(500)
                    isTopMenuOpen = true
                }
                true -> {
                    translationWidth = topMenuWidth
                    binding.topMenu.animate().translationX(translationWidth.toFloat()).setDuration(500)
                    isTopMenuOpen = false
                }
            }
        }
        binding.mainLayout.setOnClickListener {
            if(translationWidth < 0){
                translationWidth = topMenuWidth
                binding.topMenu.animate().translationX(translationWidth.toFloat()).setDuration(500)
                isTopMenuOpen = false
            }
        }

        binding.btnLogout.setOnClickListener {
            activity?.let {
            val dialogBuilder = AlertDialog.Builder(it)
            dialogBuilder.setMessage(R.string.sign_out_message)
                .setPositiveButton(R.string.yes, DialogInterface.OnClickListener { dialog, id ->
                    auth.signOut()
                    startActivity(Intent(it.applicationContext,LoginAndRegisterActivity::class.java))
                    it.finish()
                })
                .setNegativeButton(R.string.no, DialogInterface.OnClickListener {
                        dialog, id -> dialog.cancel()
                })
            val alert = dialogBuilder.create()
            alert.setTitle(R.string.log_out)
            alert.show()
            }
        }
    }


    fun changeFilterBackground(mContext : Context,activeTextView : TextView){
        /*
        val active = R.drawable.words_filter_active_background
        var inActive = R.drawable.words_filter__inactive_background

        val activeText = ContextCompat.getColor(mContext,R.color.word_filter_active_text_color)
        var inActiveText = ContextCompat.getColor(mContext,R.color.words_filter_text_color)

        binding.apply {

            btnFilterThisWeek.setBackgroundResource(inActive)
            btnFilterThisMonth.setBackgroundResource(inActive)
            btnFilterThreeMonths.setBackgroundResource(inActive)
            btnFilterAllTime.setBackgroundResource(inActive)

            btnFilterThisWeek.setTextColor(inActiveText)
            btnFilterThisMonth.setTextColor(inActiveText)
            btnFilterThreeMonths.setTextColor(inActiveText)
            btnFilterAllTime.setTextColor(inActiveText)


            activeTextView.setTextColor(activeText)
            activeTextView.setBackgroundResource(active)
        }

         */
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        auth = FirebaseAuth.getInstance()
        _binding = FragmentHomeBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}