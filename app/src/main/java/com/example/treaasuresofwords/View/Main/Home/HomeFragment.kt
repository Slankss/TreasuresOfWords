package com.example.treaasuresofwords.View.Main.Home

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.treaasuresofwords.R
import com.example.treaasuresofwords.View.LoginAndRegister.LoginAndRegisterActivity
import com.example.treaasuresofwords.View.Main.Settings.SettingsActivity
import com.example.treaasuresofwords.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.muhammed.toastoy.Toastoy
import java.sql.Date
import java.time.LocalDate
import java.time.ZoneId
import java.util.*


class HomeFragment : Fragment() {

    private var _binding : FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseFirestore
    private var isTopMenuOpen = false
    private lateinit var viewModel : HomeFragmentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val currentUser = auth.currentUser

        currentUser?.let {
            val email = it.email
            binding.txtUserEmail.setText(email)
        }

        binding.circularProgressBar.apply {
            progressBarWidth = 20f
            backgroundProgressBarWidth = 20f
            roundBorder = true
            backgroundProgressBarColor = context.getColor(R.color.progress_bar_all_color)
            progressBarColor = context.getColor(R.color.progress_bar_progress_color)

        }

        viewModel.wordList.observe(viewLifecycleOwner){
            it?.let {
                binding.txtAllWord.setText(it.size.toString())

                if(it.size > 0){
                    binding.circularProgressBar.progressMax = it.size.toFloat()
                }
                else{
                    binding
                }

            }
        }

        viewModel.levelNumbers.observe(viewLifecycleOwner){
            binding.apply {
                zeroLevel.text = it["zeroLevel"].toString()
                oneLevel.text = it["oneLevel"].toString()
                twoLevel.text = it["twoLevel"].toString()
                threeLevel.text = it["threeLevel"].toString()
                fourLevel.text = it["fourLevel"].toString()
                fiveLevel.text = it["fiveLevel"].toString()
            }
        }

        viewModel.learnedWord.observe(viewLifecycleOwner) {
            if(it != null ){
                binding.txtLearnedWords.setText(it.toString())
                if(it > 0){
                    binding.circularProgressBar.setProgressWithAnimation(it.toFloat(),1000)
                }

            }
        }



        val activeText = ContextCompat.getColor(view.context,R.color.word_filter_active_text_color)
        var inActiveText = ContextCompat.getColor(view.context,R.color.words_filter_text_color)

        binding.radioBtnThisWeek.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                binding.radioBtnThisWeek.setTextColor(activeText)
                var calendar = Calendar.getInstance()
                calendar.add(Calendar.DAY_OF_YEAR,-7)
                var date = calendar.time
                val localDate = converDate(date)

                viewModel.getWordList(localDate)
            }
            else{
                binding.radioBtnThisWeek.setTextColor(inActiveText)
            }
        }

        binding.radioBtnThisMonth.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                binding.radioBtnThisMonth.setTextColor(activeText)
                var calendar = Calendar.getInstance()
                calendar.add(Calendar.MONTH,-1)
                var date = calendar.time
                val localDate = converDate(date)

                viewModel.getWordList(localDate)
            }
            else{
                binding.radioBtnThisMonth.setTextColor(inActiveText)
            }
        }

        binding.radioBtnThreeMonths.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                binding.radioBtnThreeMonths.setTextColor(activeText)
                var calendar = Calendar.getInstance()
                calendar.add(Calendar.MONTH,-3)
                var date = calendar.time
                val localDate = converDate(date)

                viewModel.getWordList(localDate)
            }
            else{
                binding.radioBtnThreeMonths.setTextColor(inActiveText)
            }
        }

        binding.radioBtnAllTime.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                binding.radioBtnAllTime.setTextColor(activeText)
                viewModel.getWordList(null)
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
                    changeRadioButtonClickable(false)
                }
                true -> {
                    translationWidth = topMenuWidth
                    binding.topMenu.animate().translationX(translationWidth.toFloat()).setDuration(500)
                    isTopMenuOpen = false
                    changeRadioButtonClickable(true)
                }
            }
        }
        binding.mainLayout.setOnClickListener {
            if(translationWidth < 0){
                translationWidth = topMenuWidth
                binding.topMenu.animate().translationX(translationWidth.toFloat()).setDuration(500)
                isTopMenuOpen = false
                changeRadioButtonClickable(true)
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

        binding.btnGoSettings.setOnClickListener {
            activity?.let {
                startActivity(Intent(it.applicationContext,SettingsActivity::class.java))
                it.finish()
            }

        }

        binding.btnGoProfile.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToProfileFragment()
            findNavController().navigate(action)
        }
    }

    fun changeRadioButtonClickable(state : Boolean){
        binding.radioBtnThisWeek.isClickable = state
        binding.radioBtnThisMonth.isClickable = state
        binding.radioBtnThreeMonths.isClickable = state
        binding.radioBtnAllTime.isClickable = state
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun converDate(dateToConvert: java.util.Date): LocalDate? {
        return dateToConvert.toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        viewModel = HomeFragmentViewModel(auth,db)
        _binding = FragmentHomeBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onResume() {
        super.onResume()

        binding.apply {
            radioBtnAllTime.text = getString(R.string.all_time)
            radioBtnThisMonth.text = getString(R.string.this_month)
            radioBtnThisWeek.text = getString(R.string.this_week)
            radioBtnThreeMonths.text = getString(R.string.three_months)

            lblAllWord.text = getString(R.string.all_words)
            lblLearnedWords.text = getString(R.string.learned_words)

            lblZeroLevel.text = getString(R.string.zero_level)
            lblOneLevel.text = getString(R.string.one_level)
            lblTwoLevel.text = getString(R.string.two_level)
            lblThreeLevel.text = getString(R.string.three_level)
            lblFourLevel.text = getString(R.string.four_level)
            lblFiveLevel.text = getString(R.string.five_level)

            btnGoSettings.text = getString(R.string.settings)
            btnLogout.text = getString(R.string.log_out)

        }

    }


}