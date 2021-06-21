package com.example.speechtherapy.bundles.classesActivities.homework

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.view.View
import android.widget.*
import com.example.speechtherapy.R
import com.example.speechtherapy.bundles.adapters.ClassOfStudentsAdapter
import com.example.speechtherapy.bundles.adapters.HomeworkObjectAdapter
import com.example.speechtherapy.bundles.common.helpers.DataListener.OnGetDataListener
import com.example.speechtherapy.bundles.common.helpers.DataListener.OnGetDataListenerQuery
import com.example.speechtherapy.bundles.common.helpers.DataListener.OnGetListListener
import com.example.speechtherapy.bundles.services.ClassService.ClassService
import com.example.speechtherapy.bundles.services.ExerciseService.ExerciseService
import com.example.speechtherapy.bundles.services.HomeworkService.HomeworkService
import com.example.speechtherapy.bundles.services.UserServices.UserServices
import com.example.speechtherapy.bundles.model.dataModel.Exercise
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import java.util.*

class AddToHomework(val context: Activity, val user: FirebaseUser): AdapterView.OnItemSelectedListener {

    private var classesMap: MutableMap<String, String> = mutableMapOf()
    private var homeworkMap: MutableMap<String, MutableList<String>> = mutableMapOf()
    private val classService: ClassService = ClassService()
    private var className: String = ""
    private var homework: String = ""
    private val homeworkService: HomeworkService = HomeworkService()
    private val userService: UserServices = UserServices()
    private val exerciseService: ExerciseService = ExerciseService()

    fun addHomeworkDialog(collectionPath: String,exercise: String = "",  bulk: Boolean = true) {
        val dialog = AlertDialog.Builder(context)
        dialog.setTitle("Assign Homework")
        val view = context.layoutInflater.inflate(R.layout.teacher_add_homework, null)
        val spinnerClasses = view.findViewById<Spinner>(R.id.classesSpinner)
        spinnerClasses.onItemSelectedListener = this
        setClasses(spinnerClasses)
        val spinnerHomework = view.findViewById<Spinner>(R.id.homeworkSpinner)
        spinnerHomework.onItemSelectedListener = this
        spinnerHomework.visibility = View.INVISIBLE
        dialog.setCancelable(false)
        dialog.setView(view)
        val getHomework = view.findViewById<Button>(R.id.getHomeworks)
        getHomework.setOnClickListener {
            homeworkOnClickListener(getHomework, spinnerHomework, view)
        }
        val errorText = view.findViewById<TextView>(R.id.errorText)
        errorText.visibility = View.INVISIBLE
        dialog.setPositiveButton("Assign") { _: DialogInterface, _: Int ->
            assignHomework(collectionPath, exercise, view.findViewById<EditText>(R.id.studentEmail).text.toString(), errorText, bulk)
        }
        dialog.setNegativeButton("Cancel") { dialogInterface: DialogInterface, _: Int -> dialogInterface.cancel() }
        dialog.create()
        dialog.show()
    }

    private fun assignHomework(collectionPath: String, exercise: String, text: String, errorText: TextView, bulk: Boolean){
        if(text == "") {
            assignToClass(collectionPath, exercise, bulk)
        }
        else{
            assignToStudent(collectionPath, exercise, text, errorText, bulk)
        }
    }

    private fun assignToStudent(collectionPath: String, exercise: String, studentEmail: String, error: TextView, bulk: Boolean){
        userService.getUserByEmail(studentEmail, object: OnGetDataListenerQuery{
            override fun onSuccess(querySnapshot: QuerySnapshot?) {
                if (querySnapshot?.documents?.size != 0) {
                    val id = querySnapshot?.documents?.get(0)?.id
                    if (id != null) {
                        error.visibility = View.INVISIBLE
                        addToHomework(collectionPath, exercise, bulk)
                        return
                    }
                }
                error.text = context.resources.getString(R.string.InvalidStudentEmail)
                error.visibility = View.VISIBLE
            }
        })
    }

    private fun assignToClass(collectionPath: String, exercise: String, bulk: Boolean){
        classService.getStudentsByClass(
            classesMap[className].toString(),
            object : OnGetDataListener {
                override fun onSuccess(documentSnapshot: DocumentSnapshot?) {
                    addToHomework(collectionPath, exercise, bulk)
                }
            })
    }

    private fun addToHomework(collectionPath: String, exercise: String, bulk: Boolean){
        if(bulk) {
            addBulkHomework(collectionPath)
        }
        else{
            addIndividualHomework(collectionPath, exercise)
        }
    }

    private fun addBulkHomework(collectionPath: String){
        exerciseService.getExercises(collectionPath, object : OnGetListListener {
            override fun onSuccess(result: MutableList<*>) {
                for(homework in homeworkMap[homework]!!) {
                    homeworkService.addExerciseToHomework(homework, result as MutableList<Exercise>)
                }
                Toast.makeText(context, "Homework Assigned", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun addIndividualHomework(collectionPath: String, exercise: String){
        exerciseService.getExercise(collectionPath, exercise, object:OnGetDataListener{
            override fun onSuccess(documentSnapshot: DocumentSnapshot?) {
                val exerciseObject = Exercise(collectionPath, exercise.toLowerCase(Locale.ROOT), documentSnapshot?.get("uri") as String)
                val exercises = mutableListOf(exerciseObject)
                for(homework in homeworkMap[homework]!!) {
                    homeworkService.addExerciseToHomework(homework, exercises)
                }
                Toast.makeText(context, "Homework Assigned", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun homeworkOnClickListener(getHomeworks: Button, spinnerHomework: Spinner, view: View){
        val studentEmail = view.findViewById<EditText>(R.id.studentEmail).text.toString()
        spinnerHomework.visibility = View.VISIBLE
        if(studentEmail == ""){
            showHomeworksByClass(spinnerHomework)
        }
        else{
            showHomeworksByStudent(spinnerHomework, studentEmail)
        }
        getHomeworks.visibility = View.INVISIBLE
    }

    fun setAdapter(querySnapshot: QuerySnapshot?, spinner: Spinner){
        val homeworks = HomeworkObjectAdapter().getHomeworkList(querySnapshot)
        if(homeworks != null) {
            var homeworkNames = homeworks.map { it.homeworkName } as MutableList<String>
            val homeworkUID = homeworks.map { it.uid } as MutableList<String>
            homeworkNames.zip(homeworkUID).forEach {pair ->
                if(homeworkMap.containsKey(pair.component1())){
                    homeworkMap[pair.component1()]!!.add(pair.component2())
                }
                else{
                    homeworkMap[pair.component1()] = mutableListOf(pair.component2())
                }
            }
            homeworkNames = homeworkNames.distinct() as MutableList<String>
            val arrayAdapter: ArrayAdapter<String> = ArrayAdapter<String>(
                context, android.R.layout.simple_spinner_item,
                homeworkNames
            )
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = arrayAdapter
        }
    }

    private fun showHomeworksByClass(spinner: Spinner){
            homeworkService.getHomeworksByClass(className, user.uid, object: OnGetDataListenerQuery{
                override fun onSuccess(querySnapshot: QuerySnapshot?) {
                      setAdapter(querySnapshot, spinner)
                }
            })
    }

    private fun showHomeworksByStudent(spinner: Spinner, studentEmail: String){
                homeworkService.getHomeworkByStudent(studentEmail, user.uid, object: OnGetDataListenerQuery{
                    override fun onSuccess(querySnapshot: QuerySnapshot?) {
                       setAdapter(querySnapshot, spinner)
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
        when(parent?.id){
            R.id.classesSpinner -> className = parent.getItemAtPosition(position) as String
            R.id.homeworkSpinner -> homework = parent.getItemAtPosition(position) as String
        }

    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }
}