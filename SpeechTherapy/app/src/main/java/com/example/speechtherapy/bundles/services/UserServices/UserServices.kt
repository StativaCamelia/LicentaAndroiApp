package com.example.speechtherapy.bundles.services.UserServices

import android.content.ContentValues
import android.util.Log
import com.example.speechtherapy.bundles.common.helpers.DataListener.OnGetDataListener
import com.example.speechtherapy.bundles.common.helpers.DataListener.OnGetDataListenerQuery
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class UserServices{

    private val db = Firebase.firestore
    val authService: Authentification = Authentification()

    fun updateUser(userUID: String?, postValues: Map<String, String?>){
        if (userUID != null) {
            db.collection("users").document(userUID)
                .set(postValues, SetOptions.merge())
                .addOnSuccessListener { Log.d(
                    ContentValues.TAG,
                    "DocumentSnapshot successfully written!"
                ) }
                .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error writing document", e) }
        }
    }

    fun addClass(userUID: String, classUid: String){
        val addUserToArrayMap: MutableMap<String, Any> = HashMap()
        addUserToArrayMap["class"] = FieldValue.arrayUnion(classUid)
        db.collection("users").document(userUID).update(addUserToArrayMap)
    }

    fun getCurrentUser(): FirebaseUser {
        return authService.getUser()!!
    }

    fun getUserById(userUID: String, listener: OnGetDataListener){
        db.collection("users").document(userUID).get().addOnSuccessListener {
            result -> listener.onSuccess(result)
        }
    }

    fun getUserByEmail(email: String, listener: OnGetDataListenerQuery){
        db.collection("users").whereEqualTo("email", email).get().addOnSuccessListener {
            result -> listener.onSuccess(result)
        }
    }

    fun classDeleted(classUID: Any){
        db.collection("users").whereArrayContains("class", classUID).get().addOnSuccessListener {
                result ->
                var id: String
                for(user in result.documents){
                    id = user.id
                    db.collection("users").document(id).update("class", FieldValue.arrayRemove(classUID))
                }
        }
        }

    fun deleteFromClass(studentUID: String, classUID: String){
        db.collection("users").document(studentUID).update("class", FieldValue.arrayRemove(classUID))
    }
}