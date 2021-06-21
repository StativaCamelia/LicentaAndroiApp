package com.example.speechtherapy.bundles.classesActivities.teacher.homeworkExercises

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.speechtherapy.R
import java.util.ArrayList

class ExerciseAdapterAdultTeacher(val contextMain: Activity, val correctAnswer: MutableList<String>, val answer: ArrayList<String>): ArrayAdapter<String>(contextMain, R.layout.student_class_item_view, correctAnswer){

    @SuppressLint("InflateParams", "ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var viewRow = contextMain.layoutInflater.inflate(R.layout.grammar_response_adapter,null, true)
        viewRow.findViewById<TextView>(R.id.correctAnswer).text = correctAnswer[position]
        var actualAnswer = viewRow.findViewById<TextView>(R.id.actualAnswer)
        actualAnswer.text = answer[position]
        if(correctAnswer[position] == answer[position]){
            actualAnswer.setTextColor(Color.parseColor("#adebad"))
        }
        else{
            actualAnswer.setTextColor(Color.parseColor("#ff704d"))
        }
        return viewRow
    }

}