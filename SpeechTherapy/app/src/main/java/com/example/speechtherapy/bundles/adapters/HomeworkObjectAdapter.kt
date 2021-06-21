package com.example.speechtherapy.bundles.adapters

import com.example.speechtherapy.bundles.model.dataModel.Exercise
import com.example.speechtherapy.bundles.model.dataModel.Homework
import com.google.firebase.firestore.QuerySnapshot

class HomeworkObjectAdapter {

    fun getHomeworkList(querySnapshot: QuerySnapshot?): List<Homework>?{
        if(querySnapshot != null) {
            if (querySnapshot.size() > 0) {
                val homeworks = mutableListOf<Homework>()
                for (homeworkItem in querySnapshot) {
                    val homework = Homework()
                    homework.uid = homeworkItem.id
                    homework.className = homeworkItem.data["className"].toString()
                    homework.isClass = homeworkItem.data["class"] as Boolean
                    homework.createdAt = homeworkItem.data["createdAt"].toString()
                    homework.deadline = homeworkItem.data["deadline"].toString()
                    homework.exercises = homeworkItem.data["exercises"] as MutableList<Exercise>
                    homework.homeworkName = homeworkItem.data["homeworkName"].toString()
                    homework.status = homeworkItem.data["status"].toString()
                    homework.students = homeworkItem.data["students"] as MutableList<String>
                    homework.teacher = homeworkItem.data["teacher"].toString()
                    homeworks.add(homework)
                }
                return homeworks
            }
            return null
        }
        return null
    }
}