package com.example.speechtherapy.bundles.classesActivities.teacher

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.speechtherapy.R

class ClassAdapter(val contextMain: Activity, val uids: List<String>, val names: List<String>,val  totals:List<Int> ): ArrayAdapter<String>(contextMain, R.layout.teacher_class_item_view, uids) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var viewRow = contextMain.layoutInflater.inflate(R.layout.teacher_class_item_view, null, true)
        viewRow.findViewById<TextView>(R.id.className).text = names[position]
        viewRow.findViewById<TextView>(R.id.totalStudents).text = totals[position].toString()
        return viewRow
    }

}