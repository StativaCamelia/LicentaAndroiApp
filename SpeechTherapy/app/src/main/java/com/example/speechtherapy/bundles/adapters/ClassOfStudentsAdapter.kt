package com.example.speechtherapy.bundles.adapters

import com.example.speechtherapy.bundles.model.dataModel.ClassOfStudents
import com.google.firebase.firestore.QuerySnapshot

class ClassOfStudentsAdapter {

    fun getListOfClasses(querySnapshot: QuerySnapshot?): List<ClassOfStudents>? {
        if (querySnapshot != null) {
            if (querySnapshot.size() > 0) {
                val classes = mutableListOf<ClassOfStudents>()
                for (classItem in querySnapshot) {
                    val classOfStudents = ClassOfStudents()
                    classOfStudents.teacher = classItem.data["teacher"].toString()
                    classOfStudents.students = classItem.data["students"] as List<String>
                    classOfStudents.className = classItem.data["className"].toString()
                    classOfStudents.uid = classItem.id.toString()
                    classes.add(classOfStudents)
                }
                return classes
            }
            return null
        }
        return null
    }

    fun getClassesNames(classes: List<ClassOfStudents>?): MutableList<String>?
    {
        if(classes != null) {
            return classes.map { it.className } as MutableList<String>
        }
        return null
    }

    fun getClassesMap(classes: List<ClassOfStudents>?): MutableMap<String, String>? {
        if (classes != null) {
            val names = ClassOfStudentsAdapter().getClassesNames(classes)
            val ids = classes?.map { it.uid } as MutableList<String>
            if (names != null) {
                return names.zip(ids).toMap() as MutableMap<String, String>
            }
        }
        return null
    }

}