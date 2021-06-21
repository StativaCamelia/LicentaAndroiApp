package com.example.speechtherapy.bundles.classesActivities.homework

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.example.speechtherapy.R
import com.example.speechtherapy.bundles.classesActivities.student.homeworkExercises.AdultListHomework
import com.example.speechtherapy.bundles.classesActivities.student.homeworkExercises.ChildListHomework
import com.example.speechtherapy.bundles.classesActivities.teacher.homeworkExercises.AdultListHomeworkTeacher
import com.example.speechtherapy.bundles.classesActivities.teacher.homeworkExercises.ChildListHomeworkTeacher
import com.example.speechtherapy.bundles.common.BaseActivity
import com.example.speechtherapy.bundles.common.helpers.DataListener.OnGetDataListener
import com.example.speechtherapy.bundles.common.helpers.DateUtils
import com.example.speechtherapy.bundles.services.HomeworkService.HomeworkService
import com.example.speechtherapy.bundles.services.UserServices.UserServices
import com.google.firebase.firestore.DocumentSnapshot
import java.util.*

class ExerciseType: BaseActivity(), View.OnClickListener {

    private lateinit var adultGrammar: Button
    private lateinit var adultPronuciation: Button
    private lateinit var childGrammar: Button
    private lateinit var childPronunciation: Button
    private val userServices: UserServices = UserServices()
    private lateinit var submitButton: Button
    private lateinit var homeworkUID : String
    private lateinit var studentUID: String
    private val homeworkService: HomeworkService = HomeworkService()
    private lateinit var userType: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.student_exercise_type)
        homeworkUID = this.intent.getStringExtra("homeworkUID").toString()
        initView()
        initActions()
        getStudent()
    }


    fun initView(){
        adultGrammar = findViewById(R.id.adultGrammar)
        adultPronuciation = findViewById(R.id.adultPronunciation)
        childGrammar = findViewById(R.id.childGrammar)
        childPronunciation = findViewById(R.id.childPronunciation)
        submitButton = findViewById(R.id.submitHomework)
        hideSubmitButton()
    }

    fun hideSubmitButton(){
        userServices.getUserById(user.uid, object : OnGetDataListener {
            override fun onSuccess(documentSnapshot: DocumentSnapshot?) {
                userType = documentSnapshot?.data?.get("type").toString()
                if(userType == "teacher"){
                    submitButton.visibility = View.INVISIBLE
                }
            }
        })
    }


    private fun initActions(){
        adultGrammar.setOnClickListener(this)
        adultPronuciation.setOnClickListener(this)
        childGrammar.setOnClickListener(this)
        childPronunciation.setOnClickListener(this)
        submitButton.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.adultGrammar -> displayExercises("grammarAdult",AdultListHomeworkTeacher() , AdultListHomework())
            R.id.adultPronunciation -> displayExercises("pronunciationAdult",AdultListHomeworkTeacher() , AdultListHomework())
            R.id.childGrammar -> displayExercises("grammarChild", ChildListHomeworkTeacher(), ChildListHomework())
            R.id.childPronunciation ->  displayExercises("pronunciationChild", ChildListHomeworkTeacher(), ChildListHomework())
            R.id.submitHomework -> submitHomework()
        }
    }

    private fun getStudent(){
        homeworkService.getHomeworkById(homeworkUID, object : OnGetDataListener {
            override fun onSuccess(documentSnapshot: DocumentSnapshot?) {
                val students = documentSnapshot?.get("students") as List<String>
                studentUID = students[0]
            }
        })
    }

    private fun submitHomework(){
        homeworkService.getHomeworkById(homeworkUID, object : OnGetDataListener {
            override fun onSuccess(documentSnapshot: DocumentSnapshot?) {
                if(DateUtils().verifyDate(documentSnapshot?.get("deadline") as String)){
                    homeworkService.updateHomework(homeworkUID, mapOf("status" to "delivered"))
                }
                else{
                    homeworkService.updateHomework(homeworkUID, mapOf("status" to "delivered_late"))
                }
                Toast.makeText(this@ExerciseType, "Homework Submitted", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun displayExercises(type: String, activityTeacher: BaseActivity, activityStudent: BaseActivity){
        val b = Bundle()
        b.putString("type", type)
        b.putString("homeworkUID", homeworkUID)
        b.putString("studentUID", studentUID)
        b.putString("userUID", user.uid)
        userServices.getUserById(user.uid, object : OnGetDataListener {
            override fun onSuccess(documentSnapshot: DocumentSnapshot?) {
                if(documentSnapshot?.data?.get("type") == "teacher"){
                    startActivityWithExtras(activityTeacher, b)
                }
                else{
                    startActivityWithExtras(activityStudent, b)
                }

            }
        })
    }

}