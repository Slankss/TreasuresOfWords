package com.okankkl.treasuresofwords.View.LoginAndRegister

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.okankkl.treasuresofwords.R
import com.okankkl.treasuresofwords.databinding.FragmentFirstPageBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*


class FirstPageFragment : Fragment() {

    private var _binding : FragmentFirstPageBinding? = null
    private val binding get() = _binding!!
    private lateinit var dialog : BottomSheetDialog
    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.let {

            it.getSharedPreferences("User_Local_Data", Context.MODE_PRIVATE)
                ?.let {
                    val current_language = it.getString("current_language","en")
                    current_language?.let { current ->
                        changeLanguage(current)
                    }
                }
        }


        binding.txtChangeLanguage.setOnClickListener {
            showBottomSheet(view.context)
        }
        binding.btnGoLogin.setOnClickListener {
            val action = FirstPageFragmentDirections.actionFirstPageFragmentToLoginFragment()
            findNavController().navigate(action)
        }

        binding.btnGoRegister.setOnClickListener { btn ->

            val action = FirstPageFragmentDirections.actionFirstPageFragmentToRegisterFragment()
            findNavController().navigate(action)

        }



    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentFirstPageBinding.inflate(inflater,container,false)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        return binding.root
    }

    @SuppressLint("MissingInflatedId")
    fun showBottomSheet(mContext : Context){
        val dialogView = layoutInflater.inflate(R.layout.bottom_sheet,null)
        dialog = BottomSheetDialog(mContext,R.style.BottomSheetDialogTheme)
        dialog.setContentView(dialogView)
        val change_tr = dialogView.findViewById(R.id.change_tr) as TextView
        val change_en = dialogView.findViewById(R.id.change_en) as TextView
        val change_language = dialogView.findViewById(R.id.txt_change_language) as TextView

        dialog.show()

        change_en.setOnClickListener {
            changeLanguage("en")
            activity?.let {
                change_tr.setText(it.getString(R.string.turkish))
                change_en.setText(it.getString(R.string.english))
                change_language.setText(it.getString(R.string.change_language))
            }

        }

        change_tr.setOnClickListener {
            changeLanguage("tr")
            activity?.let {
                change_tr.setText(it.getString(R.string.turkish))
                change_en.setText(it.getString(R.string.english))
                change_language.setText(it.getString(R.string.change_language))

            }
        }


    }


    fun changeLanguage(language : String){
        activity?.let {
            // ingilizce -> en
            // türkçe -> tr
            val locale = Locale(language)
            Locale.setDefault(locale)
            val configuration = Configuration()
            configuration.locale = locale
            it.baseContext.resources.updateConfiguration(configuration,it.applicationContext.resources.displayMetrics)

            binding.apply {
                txtBeginningText.setText(it.getString(R.string.beginning_text))
                txtChangeLanguage.setText(it.getString(R.string.change_language))
                btnGoLogin.setText(it.getString(R.string.login))
                btnGoRegister.setText(it.getString(R.string.register))
            }

            val preferences = it.getSharedPreferences("User_Local_Data",Context.MODE_PRIVATE)
            val editor = preferences.edit()
            editor.putString("current_language",language)
            editor.apply()

            // aldık dili bunu shared preferences ta tutabiliriz

        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}