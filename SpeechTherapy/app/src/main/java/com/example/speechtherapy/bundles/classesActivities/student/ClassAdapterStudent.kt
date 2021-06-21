package com.example.speechtherapy.bundles.classesActivities.student

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.speechtherapy.R
import com.example.speechtherapy.bundles.common.helpers.DataListener.OnGetDataListenerQuery
import com.example.speechtherapy.bundles.services.HomeworkService.HomeworkService
import com.google.firebase.firestore.QuerySnapshot

class ClassAdapterStudent(val contextMain: Activity, val uids: List<String>, val names: List<String>): ArrayAdapter<String>(contextMain, R.layout.student_class_item_view, uids) {

    private val homeworkService: HomeworkService = HomeworkService()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var viewRow = contextMain.layoutInflater.inflate(R.layout.student_class_item_view, null, true)
        viewRow.findViewById<TextView>(R.id.className).text = names[position]
        homeworkService.getHomeworksByClassAndStatus(names[position], "on_going", object:OnGetDataListenerQuery{
            override fun onSuccess(querySnapshot: QuerySnapshot?) {
                viewRow.findViewById<TextView>(R.id.ongoingHomework).text = querySnapshot?.size().toString()
            }


        })
        homeworkService.getHomeworksByClassAndStatus(names[position], "finished", object:OnGetDataListenerQuery{
            override fun onSuccess(querySnapshot: QuerySnapshot?) {
                viewRow.findViewById<TextView>(R.id.homeworkCompleted).text = querySnapshot?.size().toString()
            }

        })

        return viewRow
    }

}