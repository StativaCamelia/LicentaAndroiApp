package com.example.speechtherapy.bundles.classesActivities.teacher

import android.app.Activity
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.speechtherapy.R
import com.example.speechtherapy.bundles.common.helpers.DataListener.OnGetDataListener
import com.example.speechtherapy.bundles.model.dataModel.Homework
import com.example.speechtherapy.bundles.services.UserServices.UserServices
import com.google.firebase.firestore.DocumentSnapshot

class HomeworkAdapter(val context: Activity, val uids: MutableList<String>, val homeworks: MutableList<Homework>) : ArrayAdapter<String>(context, R.layout.teacher_homework_item,uids ) {

    val userServices = UserServices()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = context.layoutInflater.inflate(R.layout.teacher_homework_item, null, true)
        userServices.getUserById(homeworks[position].students[0], object: OnGetDataListener{
            override fun onSuccess(documentSnapshot: DocumentSnapshot?) {
                view.findViewById<TextView>(R.id.homeworkName).text = homeworks[position].homeworkName
                view.findViewById<TextView>(R.id.homeworkAssignedTo).text = documentSnapshot?.get("email") as CharSequence?
                view.findViewById<TextView>(R.id.homeworkDeadline).text = homeworks[position].deadline
                view.findViewById<TextView>(R.id.homeworkCreatedAt).text = homeworks[position].createdAt
                when(homeworks[position].status){
                    "on_going" -> view.setBackgroundColor(Color.parseColor("#99d6ff"))
                    "delivered" -> view.setBackgroundColor(Color.parseColor("#adebad"))
                    "delivered_late" -> view.setBackgroundColor(Color.parseColor("#ff704d"))
                }
            }
        })
        return view
    }


}