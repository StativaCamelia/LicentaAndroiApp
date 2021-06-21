package com.example.speechtherapy.bundles.classesActivities.teacher

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.speechtherapy.R
import com.example.speechtherapy.bundles.common.BaseActivity
import com.example.speechtherapy.bundles.common.helpers.DataListener.OnGetDataListenerQuery
import com.example.speechtherapy.bundles.services.ClassService.ClassService
import com.example.speechtherapy.bundles.model.dataModel.ClassOfStudents
import com.google.firebase.firestore.QuerySnapshot

class ClassesActivityTeacher: BaseActivity(), View.OnClickListener {

    private val classService: ClassService = ClassService()
    private lateinit var createClass: Button
    private lateinit var classes: ListView
    private lateinit var noClass: TextView
    private lateinit var swipeRefresh: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.teacher_activity_class)
        initViews()
        getClasses()
        setRefresh()
    }

    private fun setRefresh(){
        swipeRefresh = findViewById(R.id.swiperefresh)
        swipeRefresh.setOnRefreshListener {
            swipeRefresh.isRefreshing = false
        }
    }

    override fun onRestart() {
        super.onRestart()
        finish()
        startNewActivity(this)
    }

    private fun getClasses(){
        classService.getAllClassesByUser(user.uid, object : OnGetDataListenerQuery {
            override fun onSuccess(querySnapshot: QuerySnapshot?) {
                val names = mutableListOf<String>()
                val total = mutableListOf<Int>()
                val uids = mutableListOf<String>()
                var index = 0
                if (querySnapshot != null) {
                    if (querySnapshot.size() > 0) {
                        for (classItem in querySnapshot) {
                            uids.add(index, classItem.id)
                            names.add(index, classItem.data["className"].toString())
                            if (classItem.data["students"] != null) {
                                val students = classItem.data["students"] as List<*>
                                total.add(index, students.size)
                            } else {
                                total.add(index, 0)
                            }
                            index += 1
                        }
                        classes.adapter = ClassAdapter(this@ClassesActivityTeacher, uids, names, total
                        )
                    } else {
                        noClass.visibility = View.VISIBLE
                    }
                }

            }
        })
    }

    private fun initViews(){
        noClass = findViewById(R.id.noClass)
        noClass.visibility = View.INVISIBLE
        createClass = findViewById(R.id.createClass)
        createClass.setOnClickListener(this)
        classes = findViewById(R.id.classesList)
        setSimpleClick()
        setLongClick()
    }

    private fun setSimpleClick(){
        classes.setOnItemClickListener { _, _, position, _ ->
            val  b =Bundle()
            b.putString("classId", classes.getItemAtPosition(position).toString())
            startActivityWithExtras(StudentsActivityTeacher(), b)
        }
    }

    private fun setLongClick(){
        classes.setOnItemLongClickListener { _, _, position, _ ->
            val id: String = classes.getItemAtPosition(position).toString()
            val alertD = AlertDialog.Builder(this)
            alertD.setTitle("Delete class")
            alertD.setMessage("Do you want to delete this class?")
            alertD.setNegativeButton("No") { dialogInterface: DialogInterface, _: Int ->
                dialogInterface.cancel()
            }
            alertD.setPositiveButton("Yes") { _: DialogInterface, _: Int ->
                classService.delete(id)
                Toast.makeText(this, "Class deleted", Toast.LENGTH_LONG).show()
                startNewActivity(ClassesActivityTeacher())
            }
            alertD.create()
            alertD.show()
            return@setOnItemLongClickListener true
        }

    }
    override fun onClick(v: View?) {
        when(v?.id){
            R.id.createClass -> getClassName()
        }
    }

    private fun createClass(className: String){
        val newClass = ClassOfStudents()
        newClass.className = className
        newClass.teacher = user.uid
        classService.createClass(newClass)
        startNewActivity(ClassesActivityTeacher())
    }

    private fun getClassName(){
        val dialog = AlertDialog.Builder(this)
        val viewDialog = layoutInflater.inflate(R.layout.dialog_create_class, null)
        dialog.setView(viewDialog)
        dialog.setTitle("Create class")
        dialog.setNegativeButton("Cancel") { dialogInterface: DialogInterface, _: Int ->
            dialogInterface.cancel()
        }
        dialog.setPositiveButton("Create") { _: DialogInterface, _: Int ->
            val className = viewDialog.findViewById<EditText>(R.id.className).text.toString()
            createClass(className)
        }
        dialog.create()
        dialog.show()
    }
}