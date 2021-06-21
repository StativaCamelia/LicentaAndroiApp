package com.example.speechtherapy.bundles.services.ClassService

import android.content.ContentValues
import android.util.Log
import com.example.speechtherapy.bundles.common.helpers.DataListener.OnCreateData
import com.example.speechtherapy.bundles.common.helpers.DataListener.OnGetDataListener
import com.example.speechtherapy.bundles.common.helpers.DataListener.OnGetDataListenerQuery
import com.example.speechtherapy.bundles.services.UserServices.Authentification
import com.example.speechtherapy.bundles.services.UserServices.UserServices
import com.example.speechtherapy.bundles.model.dataModel.ClassOfStudents
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class ClassService {

    private val db = Firebase.firestore
    val authService: Authentification = Authentification()
    var collection = "classes"
    var userService: UserServices = UserServices()

    fun createClass(newClass: ClassOfStudents){
            val teacherUID = newClass.teacher
            val id = db.collection("collection_name").document().id
            db.collection(collection).document(id).set(newClass, SetOptions.merge())
                .addOnSuccessListener { Log.d(
                    ContentValues.TAG,
                    "DocumentSnapshot successfully written!"
                ) }
                .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error writing document", e)
                        throw e
                }
            userService.addClass(teacherUID, id)
    }

    fun getAllClassesByUser(userUID: String, listener: OnGetDataListenerQuery){
        db.collection(collection).whereEqualTo("teacher", userUID).get().addOnSuccessListener { result -> listener.onSuccess(
            result
        )
        }
    }

    fun getAllStudentClasses(userUID: String, listener: OnGetDataListenerQuery){
        db.collection(collection).whereArrayContains("students", userUID).get().addOnSuccessListener { result -> listener.onSuccess(
            result
        )
        }
    }

    fun getStudentsByClass(uidClass: String, listener: OnGetDataListener){
        db.collection(collection).document(uidClass).get().addOnSuccessListener { result -> listener.onSuccess(result)
        }
    }

    fun delete(uid: String){
        db.collection(collection).document(uid).delete()
        userService.classDeleted(uid)
    }

    fun deleteStudentFromClass(classUid: String, studentUid: String){
        db.collection(collection).document(classUid).update("students", FieldValue.arrayRemove(studentUid))
        userService.deleteFromClass(studentUid, classUid)
    }

    @Throws(ConflictException::class)
    fun addStudentToClass(uidClass: String, studentUid: String, listener: OnCreateData){
        val addUserToArrayMap: MutableMap<String, Any> = HashMap()
        addUserToArrayMap["students"] = FieldValue.arrayUnion(studentUid)
        getStudentsByClass(uidClass, object: OnGetDataListener{
            override fun onSuccess(documentSnapshot: DocumentSnapshot?) {
                if(documentSnapshot?.data?.get("students") != null) {
                    val students = documentSnapshot.data?.get("students") as List<*>
                    for (student in students) {
                        if (student == studentUid) {
                            listener.onFailure(ConflictException("Student already in class"))
                            return
                        }
                    }
                }
                db.collection(collection).document(uidClass).update(addUserToArrayMap)
                userService.addClass(studentUid, uidClass)

                listener.onSuccess()
            }
        })
    }

}