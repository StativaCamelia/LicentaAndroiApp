package com.example.speechtherapy.bundles.grammarActivities.adult

import com.example.speechtherapy.R
import com.example.speechtherapy.bundles.grammarActivities.wordsTypeMenu.WordsTypeViewModel

class AdultWordsTypeData {
    companion object {
        fun <ArrayList> getListViewModelList(): ArrayList {
            val listViewModelArrayList = ArrayList<WordsTypeViewModel>()
            listViewModelArrayList.add(WordsTypeViewModel(1, "Restaurant", R.drawable.adult_restaurant, "adult_grammat_restaurant"))
            listViewModelArrayList.add(WordsTypeViewModel(2, "Resume", R.drawable.adult_resume, "adult_grammar_resume"))
            listViewModelArrayList.add(WordsTypeViewModel(3, "Job Interview", R.drawable.adult_job, "adult_grammar_job"))
            return listViewModelArrayList as ArrayList
        }
    }
}