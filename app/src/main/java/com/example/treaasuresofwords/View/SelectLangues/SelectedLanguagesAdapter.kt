package com.example.treaasuresofwords.View.SelectLangues

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.treaasuresofwords.databinding.SelectedLanguageRowBinding

class SelectedLanguagesAdapter(val selectedLanguages : ArrayList<HashMap<String,String>>) : RecyclerView.Adapter<SelectedLanguagesAdapter.ViewHolder>() {

    var deleteClick : (Int) -> Unit = {}

    class ViewHolder(val binding : SelectedLanguageRowBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = SelectedLanguageRowBinding.inflate(layoutInflater, parent, false)
        return SelectedLanguagesAdapter.ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {
            val current = selectedLanguages[position]

            var text = current["languageToTranslated"] + " -> " + current["translatedLanguage"]
            txtSelectedLanguage.setText(text)

            txtSelectedLanguage

            this.btnDelete.setOnClickListener {
                deleteClick(position)
            }
        }
    }

    override fun getItemCount(): Int {
        return selectedLanguages.size
    }
}