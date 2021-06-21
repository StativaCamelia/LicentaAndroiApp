package com.example.speechtherapy.bundles.classesActivities.student

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.telephony.MbmsDownloadSession
import android.view.View
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import com.example.speechtherapy.R
import com.example.speechtherapy.bundles.classesActivities.teacher.ClassesActivityTeacher
import com.example.speechtherapy.bundles.common.BaseActivity
import com.example.speechtherapy.bundles.common.helpers.DataListener.OnCreateData
import com.example.speechtherapy.bundles.common.helpers.DataListener.OnGetDataListenerQuery
import com.example.speechtherapy.bundles.services.ClassService.ClassService
import com.example.speechtherapy.bundles.services.ClassService.ConflictException
import com.google.firebase.firestore.QuerySnapshot

class ClassesActivityStudent: BaseActivity(), View.OnClickListener {

    private lateinit var noClass: TextView
    private lateinit var scanQr: Button
    private lateinit var classes: ListView
    private var classService: ClassService = ClassService()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.student_activity_classes)
        initViews()
        getClasses()
    }

    private fun initViews() {
        noClass = findViewById(R.id.noClass)
        noClass.visibility = View.INVISIBLE
        scanQr = findViewById(R.id.scanClassQr)
        scanQr.setOnClickListener(this)
        classes = findViewById(R.id.classesList)
        setLongClick()
    }

    private fun setLongClick() {
        classes.setOnItemLongClickListener { _, _, position, _ ->
            val id: String = classes.getItemAtPosition(position).toString()
            val alertD = AlertDialog.Builder(this)
            alertD.setTitle("Leave class")
            alertD.setMessage("Do you want to leave this class?")
            alertD.setNegativeButton("No") { dialogInterface: DialogInterface, _: Int ->
                dialogInterface.cancel()
            }
            alertD.setPositiveButton("Yes") { _: DialogInterface, _: Int ->
                classService.deleteStudentFromClass(id, user.uid)
                Toast.makeText(this, "Class deleted", Toast.LENGTH_LONG).show()
                startNewActivity(ClassesActivityTeacher())
            }
            alertD.create()
            alertD.show()
            return@setOnItemLongClickListener true
        }

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.scanClassQr -> readQR()
        }
    }

    private fun readQR() {
        try {
            val intent = Intent("com.google.zxing.client.android.SCAN")
            intent.putExtra("SCAN_MODE", "QR_CODE_MODE") // "PRODUCT_MODE for bar codes
            startActivityForResult(intent, 0)
        } catch (e: Exception) {
            val marketUri: Uri = Uri.parse("market://details?id=com.google.zxing.client.android")
            val marketIntent = Intent(Intent.ACTION_VIEW, marketUri)
            startActivity(marketIntent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                val contents = data?.getStringExtra("SCAN_RESULT")
                addStudentToClass(contents)
            }
            if (resultCode == MbmsDownloadSession.RESULT_CANCELLED) {
            }
        }
    }

    private fun addStudentToClass(classUID: String?) {
        if (classUID != null) {
            classService.addStudentToClass(classUID, user.uid, object : OnCreateData {
                override fun onSuccess() {
                    Toast.makeText(
                        this@ClassesActivityStudent,
                        "Student successfully added",
                        Toast.LENGTH_LONG
                    ).show()
                }
                override fun onFailure(e: ConflictException) {
                    Toast.makeText(
                        this@ClassesActivityStudent,
                        "Already in this class",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
        }

    }

    private fun getClasses() {
        classService.getAllStudentClasses(user.uid, object : OnGetDataListenerQuery {
            override fun onSuccess(querySnapshot: QuerySnapshot?) {
                val names = mutableListOf<String>()
                val uids = mutableListOf<String>()
                var index = 0
                if (querySnapshot != null) {
                    if (querySnapshot.size() > 0) {
                        for (classItem in querySnapshot) {
                            uids.add(index, classItem.id)
                            names.add(index, classItem.data["className"].toString())
                            index += 1
                        }
                        classes.adapter =
                            ClassAdapterStudent(this@ClassesActivityStudent, uids, names)
                    } else {
                        noClass.visibility = View.VISIBLE
                    }
                }
            }
        })
    }

}