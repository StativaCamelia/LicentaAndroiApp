package com.example.speechtherapy.bundles.classesActivities.homework

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import com.google.firebase.auth.FirebaseUser

class AssignHomework(val context: Activity, val user: FirebaseUser)
{
    private var createHomework: CreateHomework = CreateHomework(context, user)
    private var addToHomework: AddToHomework = AddToHomework(context, user)

    fun assignHomeworkDialog(collectionPath: String,exercise: String = "",  bulk: Boolean = true) {
        val dialog = AlertDialog.Builder(context)
        dialog.setTitle("Assing Homework")
        dialog.setMessage("Do you want to create a new homework or to add exercises to an old one?")
        dialog.setNeutralButton("Create") { _: DialogInterface, _: Int ->
            createHomework.assignHomeworkDialog(collectionPath, exercise, bulk)
        }
        dialog.setPositiveButton("Add") { _: DialogInterface, _: Int ->
            addToHomework.addHomeworkDialog(collectionPath, exercise, bulk)
        }

        dialog.create()
        dialog.show()
    }
}