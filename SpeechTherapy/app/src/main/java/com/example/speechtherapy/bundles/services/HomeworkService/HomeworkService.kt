package com.example.speechtherapy.bundles.services.HomeworkService

import android.content.ContentValues
import android.util.Log
import com.example.speechtherapy.bundles.common.helpers.DataListener.OnGetDataListener
import com.example.speechtherapy.bundles.common.helpers.DataListener.OnGetDataListenerQuery
import com.example.speechtherapy.bundles.services.UserServices.Authentification
import com.example.speechtherapy.bundles.model.dataModel.Exercise
import com.example.speechtherapy.bundles.model.dataModel.Homework
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*


class HomeworkService {

    val authService: Authentification = Authentification()
    private var collection = "homeworks"
    private val db = Firebase.firestore

    fun createHomework(homework: Homework){
        val id = db.collection(collection).document().id
        db.collection(collection).document(id).set(homework, SetOptions.merge())
            .addOnSuccessListener { Log.d(
                ContentValues.TAG,
                "DocumentSnapshot successfully written!"
            ) }
            .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error writing document", e)
                throw e
            }
    }

    fun getHomeworkByStudent(studentEmail: String, teacherUID: String, listener: OnGetDataListenerQuery){
        db.collection(collection).whereEqualTo("className", studentEmail).whereEqualTo("teacher", teacherUID).get().addOnSuccessListener {
                result -> listener.onSuccess(result)
        }
    }


    fun addExerciseToHomework(homeworkUID: String, exercises: MutableList<Exercise>){
        val addUserToArrayMap: MutableMap<String, Any> = HashMap()
        for(exercise in exercises){
            addUserToArrayMap["exercises"] = FieldValue.arrayUnion(exercise)
            db.collection(collection).document(homeworkUID).update(addUserToArrayMap)
        }
    }

    fun getHomeworksByClassAndStatus(className: String, status: String, listener: OnGetDataListenerQuery){
        db.collection(collection).whereEqualTo("class", true).whereEqualTo("className", className).whereEqualTo("status", status).get()
            .addOnSuccessListener { result ->
                listener.onSuccess(result)
            }
    }

    fun getHomeworkByStudentAndStatus(studentId: String, status: String, listener: OnGetDataListenerQuery){
        if(status != ""){
        db.collection(collection).whereArrayContains("students", studentId).whereEqualTo("status", status).get()
            .addOnSuccessListener { result ->
                listener.onSuccess(result)
            }
        }
        else{
            db.collection(collection).whereArrayContains("students", studentId).get()
                .addOnSuccessListener { result ->
                    listener.onSuccess(result)
                }
        }
    }

    fun removeExercise(homeworkUID: String, exercise: HashMap<String, Any>){
        val removeUserFromArrayMap: MutableMap<String, Any> = HashMap()
        removeUserFromArrayMap["exercises"] = FieldValue.arrayRemove(exercise)
        db.collection(collection).document(homeworkUID).update( removeUserFromArrayMap)
    }

    fun getHomeworksByClasses(user: String, listener: OnGetDataListenerQuery){
        db.collection(collection).whereEqualTo("class", true).whereEqualTo("teacher", user).get()
            .addOnSuccessListener { result ->
                listener.onSuccess(result)
            }
    }

    fun getHomeworksByClass(query: String, user: String, listener: OnGetDataListenerQuery){
        db.collection(collection).whereEqualTo("class", true).whereEqualTo("teacher", user).whereEqualTo("className", query).get()
                .addOnSuccessListener { result ->
                    listener.onSuccess(result)
                }
    }

    fun getHomeworksByStudents(user: String, listener: OnGetDataListenerQuery){
            db.collection(collection).whereEqualTo("class", false).whereEqualTo("teacher", user).get().addOnSuccessListener {
                    result -> listener.onSuccess(result)
            }
    }

    fun updateHomework(homeworkUID: String, postValues: Map<String, String>){
        db.collection(collection).document(homeworkUID).set(postValues, SetOptions.merge())
            .addOnSuccessListener { Log.d(
                ContentValues.TAG,
                "DocumentSnapshot successfully updated!"
            ) }
            .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error writing document", e) }
    }

    fun deleteHomework(homeworkUID: String){
        db.collection(collection).document(homeworkUID).delete()
    }

    fun getHomeworkById(homeworkUID: String, listener: OnGetDataListener){
        db.collection(collection).document(homeworkUID).get().addOnSuccessListener{result -> listener.onSuccess(result)
    }
    }
}