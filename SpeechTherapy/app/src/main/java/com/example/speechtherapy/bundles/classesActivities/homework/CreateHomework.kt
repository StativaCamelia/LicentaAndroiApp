package com.example.speechtherapy.bundles.classesActivities.homework

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.view.View
import android.widget.*
import com.example.speechtherapy.R
import com.example.speechtherapy.bundles.adapters.ClassOfStudentsAdapter
import com.example.speechtherapy.bundles.common.helpers.DataListener.OnGetDataListener
import com.example.speechtherapy.bundles.common.helpers.DataListener.OnGetDataListenerQuery
import com.example.speechtherapy.bundles.common.helpers.DataListener.OnGetListListener
import com.example.speechtherapy.bundles.common.helpers.DateUtils
import com.example.speechtherapy.bundles.model.dataModel.Exercise
import com.example.speechtherapy.bundles.model.dataModel.Homework
import com.example.speechtherapy.bundles.services.ClassService.ClassService
import com.example.speechtherapy.bundles.services.ExerciseService.ExerciseService
import com.example.speechtherapy.bundles.services.HomeworkService.HomeworkService
import com.example.speechtherapy.bundles.services.UserServices.UserServices
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import java.util.*


class CreateHomework(val context: Activity, val user: FirebaseUser): AdapterView.OnItemSelectedListener {

    private var classesMap: MutableMap<String, String> = mutableMapOf()
    private val classService: ClassService = ClassService()
    private var className: String = ""
    private lateinit var errorTime: TextView
    private val homeworkService: HomeworkService = HomeworkService()
    private val userService: UserServices = UserServices()
    private val exerciseService: ExerciseService = ExerciseService()
    private val dateUtils =  DateUtils()

    fun assignHomeworkDialog(collectionPath: String, exercise: String = "", bulk: Boolean = true) {
        val dialog = AlertDialog.Builder(context)
        dialog.setTitle("Assign Homework")

        val view = context.layoutInflater.inflate(R.layout.teacher_assign_homework, null)
        val spinner = view.findViewById<Spinner>(R.id.classesSpinner)
        errorTime = view.findViewById(R.id.errorTime)
        dateUtils.getDate(context, view)
        dateUtils.getTime(context, errorTime, view)
        setClasses(spinner)
        dialog.setCancelable(false)
        spinner.onItemSelectedListener = this
        dialog.setView(view)
        dialog.setCancelable(false)

        val errorText = view.findViewById<TextView>(R.id.errorText)
        errorText.visibility = View.INVISIBLE

        dialog.setPositiveButton("Assign") { _: DialogInterface, _: Int ->
            val homeworkName = view.findViewById<EditText>(R.id.homeworkTitle).text.toString()
            assignHomework(
                collectionPath,
                exercise,
                homeworkName,
                view.findViewById<EditText>(R.id.studentEmail).text.toString(),
                errorText,
                bulk
            )
        }
        dialog.setNegativeButton("Cancel") { dialogInterface: DialogInterface, _: Int -> dialogInterface.cancel() }
        dialog.create()
        dialog.show()
    }

    private fun assignHomework(
        collectionPath: String,
        exercise: String,
        homeworkName: String,
        text: String,
        errorText: TextView,
        bulk: Boolean
    ){
        if(text == "") {
            assignToClass(collectionPath, exercise, homeworkName, bulk)
        }
        else{
            assignToStudent(collectionPath, exercise, homeworkName, text, errorText, bulk)
        }
    }


    private fun assignToStudent(
        collectionPath: String,
        exercise: String,
        homeworkName: String,
        studentEmail: String,
        error: TextView,
        bulk: Boolean
    ){
        userService.getUserByEmail(studentEmail, object : OnGetDataListenerQuery {
            override fun onSuccess(querySnapshot: QuerySnapshot?) {
                if (querySnapshot?.documents?.size == 0) {
                    error.text = context.resources.getString(R.string.InvalidStudentEmail)
                    error.visibility = View.VISIBLE
                } else {
                    error.visibility = View.INVISIBLE
                    val id = querySnapshot?.documents?.get(0)?.id
                    if (id != null) {
                        createHomework(collectionPath, exercise, homeworkName, mutableListOf(id), studentEmail, bulk, false
                        )
                    } else {
                        error.text = context.resources.getString(R.string.InvalidStudentEmail)
                        error.visibility = View.VISIBLE
                    }
                }
            }
        })
    }

    private fun assignToClass(
        collectionPath: String,
        exercise: String,
        homeworkName: String,
        bulk: Boolean
    ){
        classService.getStudentsByClass(
            classesMap[className].toString(),
            object : OnGetDataListener {
                override fun onSuccess(documentSnapshot: DocumentSnapshot?) {
                    for (id in documentSnapshot?.data?.get("students") as MutableList<String>)
                        createHomework(
                            collectionPath,
                            exercise,
                            homeworkName,
                            mutableListOf(id),
                            "",
                            bulk,
                            true
                        )
                }
            })
    }

    private fun createHomework(
        collectionPath: String,
        exercise: String,
        homeworkName: String,
        students: MutableList<String>,
        studentEmail: String,
        bulk: Boolean,
        isClass: Boolean
    ){
        val homework = Homework()
        homework.homeworkName = homeworkName
        if(isClass) {
            homework.className = className
            homework.isClass = true
        }
        else{
            homework.className = studentEmail
            homework.isClass = false
        }
        homework.deadline = dateUtils.getDeadline()
        homework.teacher = user.uid
        homework.students = students
        if(bulk) {
            createBulkHomework(collectionPath, homework)
        }
        else{
            createIndividualHomework(collectionPath, exercise, homework)
        }
    }

    private fun createBulkHomework(collectionPath: String, homework: Homework){
        exerciseService.getExercises(collectionPath, object : OnGetListListener {
            override fun onSuccess(result: MutableList<*>) {
                homework.exercises = result as MutableList<Exercise>
                homeworkService.createHomework(homework)
                Toast.makeText(context, "Homework Assigned", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun createIndividualHomework(
        collectionPath: String,
        exercise: String,
        homework: Homework
    ){
        exerciseService.getExercise(collectionPath, exercise, object : OnGetDataListener {
            override fun onSuccess(documentSnapshot: DocumentSnapshot?) {
                val exerciseObject = Exercise(collectionPath, exercise.toLowerCase(Locale.ROOT), documentSnapshot?.get("uri") as String)
                homework.exercises = mutableListOf(exerciseObject)
                homeworkService.createHomework(homework)
                Toast.makeText(context, "Homework Assigned", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun setClasses(spinner: Spinner){
        classService.getAllClassesByUser(user.uid, object : OnGetDataListenerQuery {
            override fun onSuccess(querySnapshot: QuerySnapshot?) {
                val classes = ClassOfStudentsAdapter().getListOfClasses(querySnapshot)
                if(classes != null) {
                    val names = classes.map { it.className } as MutableList<String>
                    val ids = classes.map { it.uid } as MutableList<String>
                    classesMap = names.zip(ids).toMap() as MutableMap<String, String>
                    val arrayAdapter: ArrayAdapter<String> = ArrayAdapter<String>(
                        context,
                        android.R.layout.simple_spinner_item,
                        names
                    )
                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinner.adapter = arrayAdapter
                }
            }
        })
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        className = parent?.getItemAtPosition(position) as String
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }
}