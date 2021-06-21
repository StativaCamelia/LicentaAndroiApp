package com.example.speechtherapy.bundles.classesActivities.student.homeworkExercises

import android.annotation.SuppressLint
import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.speechtherapy.R
import java.util.ArrayList

class ExerciseAdapterAdult(val contextMain: Activity, val names: ArrayList<String>, val uris: ArrayList<String>?): ArrayAdapter<String>(contextMain, R.layout.student_class_item_view, names){

    @SuppressLint("InflateParams")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var viewRow = contextMain.layoutInflater.inflate(R.layout.base_words_type_list_item, null, true)
        viewRow.findViewById<TextView>(R.id.titleItem).text = names[position]
        return viewRow
    }

}