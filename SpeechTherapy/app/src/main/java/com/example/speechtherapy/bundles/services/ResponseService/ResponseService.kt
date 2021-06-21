package com.example.speechtherapy.bundles.services.ResponseService

import android.content.ContentValues
import android.util.Log
import com.example.speechtherapy.bundles.common.helpers.DataListener.OnGetDataListener
import com.example.speechtherapy.bundles.common.helpers.DataListener.OnGetDataListenerQuery
import com.example.speechtherapy.bundles.model.dataModel.Response
import com.example.speechtherapy.bundles.services.HomeworkService.HomeworkService
import com.example.speechtherapy.bundles.services.UserServices.Authentification
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*

class ResponseService {
    val authService: Authentification = Authentification()
    private val homeworkService = HomeworkService()
    private var collection = "responses"
    private val db = Firebase.firestore

    fun updateResponse(
        type: String,
        userUID: String,
        homeworkUID: String,
        response: MutableMap<String, Any>
    ){
        getResponseByUserAndHomework(userUID, homeworkUID, object : OnGetDataListenerQuery {
            override fun onSuccess(querySnapshot: QuerySnapshot?) {
                if (querySnapshot != null) {
                    if (querySnapshot.size() > 0) {
                        val id = querySnapshot.documents[0].id
                        addToResponse(id, response)
                    } else {
                        createResponse(userUID, homeworkUID, response)
                    }
                    if (type == "grammar") {
                        val exercise = HashMap<String, Any>()
                        exercise["collectionPath"] = response["collectionPath"].toString()
                        exercise["name"] = response["name"].toString()
                        exercise["uri"] = response["uri"].toString()
                        homeworkService.removeExercise(homeworkUID, exercise)
                    }
                }

            }
        })
    }

    fun addToResponse(responseUID: String, exercise: MutableMap<String, Any>){
        val addUserToArrayMap: MutableMap<String, Any> = HashMap()
        addUserToArrayMap["exercises"] = FieldValue.arrayUnion(exercise)
            db.collection(collection).document(responseUID).update(addUserToArrayMap)
    }

    fun createResponse(user: String, homework: String, exercise: MutableMap<String, Any>){
        val response = Response()
        response.studentUID = user
        response.homeworkUID = homework
        response.exercises.add(exercise)
        val id = db.collection(collection).document().id
        db.collection(collection).document(id).set(response, SetOptions.merge())
            .addOnSuccessListener { Log.d(
                ContentValues.TAG,
                "DocumentSnapshot successfully written!"
            ) }
            .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error writing document", e)
                throw e
            }
    }



    private fun getResponseByUserAndHomework(
        user: String,
        homework: String,
        listener: OnGetDataListenerQuery
    ){
        db.collection(collection).whereEqualTo("studentUID", user).whereEqualTo(
            "homeworkUID",
            homework
        ).get()
            .addOnSuccessListener { result ->
                listener.onSuccess(result)
            }
    }

    fun getGrammarResponseById(responseUID: String, listener: OnGetDataListener){
        db.collection(collection).document(responseUID).get().addOnSuccessListener {
                result ->
            listener.onSuccess(result)
        }
    }

    fun getResponseByHomeworkAndUser(homeworkUID: String, studentUID: String, listener: OnGetDataListenerQuery){
        db.collection(collection).whereEqualTo("homeworkUID", homeworkUID).whereEqualTo("studentUID", studentUID).get().addOnSuccessListener {
                result ->
            listener.onSuccess(result)
        }
    }

}