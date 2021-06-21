package com.example.speechtherapy.bundles.grammarActivities.child

import com.example.speechtherapy.R
import com.example.speechtherapy.bundles.grammarActivities.wordsTypeMenu.WordsTypeViewModel

class ChildrenWordsTypeData {
    companion object {
        fun <ArrayList> getListViewModelList(): ArrayList {
            val listViewModelArrayList = ArrayList<WordsTypeViewModel>()
            listViewModelArrayList.add(WordsTypeViewModel(1, "Domenistic Animals", R.drawable.child_farm_animals, "children_grammar_farm_animal"))
            listViewModelArrayList.add(WordsTypeViewModel(2, "Wild Animals", R.drawable.child_wild_animals, "children_grammar_wild_animal"))
            listViewModelArrayList.add(WordsTypeViewModel(3, "In the bathroom", R.drawable.child_bathroom, "children_grammar_bathroom"))
            listViewModelArrayList.add(WordsTypeViewModel(4, "In the kitchen", R.drawable.child_kitchen, "children_grammar_kitchen"))
            listViewModelArrayList.add(WordsTypeViewModel(5, "Clothing", R.drawable.child_clothing, "children_grammar_clothing"))
            listViewModelArrayList.add(WordsTypeViewModel(6, "Vegetables", R.drawable.child_vegetables, "children_grammar_vegetables"))
            listViewModelArrayList.add(WordsTypeViewModel(7, "Fruits", R.drawable.child_fruits, "children_grammar_fruits"))
            return listViewModelArrayList as ArrayList
        }
    }
}