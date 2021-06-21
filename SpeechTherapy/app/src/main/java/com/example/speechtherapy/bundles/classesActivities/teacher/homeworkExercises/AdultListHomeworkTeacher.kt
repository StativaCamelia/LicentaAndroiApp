package com.example.speechtherapy.bundles.classesActivities.teacher.homeworkExercises

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import com.example.speechtherapy.R
import com.example.speechtherapy.bundles.classesActivities.teacher.homeworkExercises.pronunciation.PronunciationResponse
import com.example.speechtherapy.bundles.common.helpers.DataListener.OnGetDataListenerQuery
import com.example.speechtherapy.bundles.interfaces.teacher.HomeworkListTeacher
import com.google.firebase.firestore.QuerySnapshot
import java.io.Serializable

class AdultListHomeworkTeacher: HomeworkListTeacher(), AdapterView.OnItemClickListener {


    private var responseGrammarAdult: MutableList<MutableMap<String, String>> = mutableListOf()
    private var responsePronunciationAdult: MutableList<MutableMap<String, String>> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.homework_exercise)
        exerciseType = this.intent.getStringExtra("type").toString()
        setRefresh()
        getExercises()
    }

    override fun onRestart() {
        super.onRestart()
        val b = Bundle()
        b.putString("homeworkUID", homeworkUID)
        b.putString("userUID", userUID)
        b.putString("studentUID", studentUID)
        finish()
        startActivityWithExtras(this, b)
    }

    private fun getExercises(){
        homeworkUID = this.intent.getStringExtra("homeworkUID") as String
        userUID = this.intent.getStringExtra("userUID") as String
        studentUID = this.intent.getStringExtra("studentUID") as String
        responseService.getResponseByHomeworkAndUser(homeworkUID, studentUID, object: OnGetDataListenerQuery{
            override fun onSuccess(querySnapshot: QuerySnapshot?) {
                val responses = querySnapshot?.documents?.get(0)!!.get("exercises") as MutableList<MutableMap<String, String>>
                for(response in responses){
                    if(response["collectionPath"]?.contains("adult_grammat") == true || response["collectionPath"]?.contains("adult_grammar") == true ){
                        responseGrammarAdult.add(response)
                    }
                    if(response["collectionPath"]?.contains("adult_pronunciation") == true){
                        responsePronunciationAdult.add(response)
                    }
                }
                if(exerciseType == "grammarAdult"){
                    initView(exerciseType, responseGrammarAdult)
                }
                else {
                    responsePronunciationAdult = responsePronunciationAdult.distinctBy { it["name"]}.toMutableList()
                    initView(exerciseType, responsePronunciationAdult)
                    initAction()
                }
            }

        })
    }

    fun initAction(){
        homework.setOnItemClickListener(this)
    }


    fun initView(type: String, exercises:MutableList<MutableMap<String, String>>){
        for(response in exercises){
            if(type == "grammarAdult"){
                response["answer"]?.let{answers.add(it)}
            }
            else{
                answers.add("Click to see more")
            }
            response["name"]?.let { correctAnswers.add(it) }
        }
        homework = findViewById(R.id.grammarAdultHomework)
        val adapter = ExerciseAdapterAdultTeacher(this, correctAnswers, answers)
        homework.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val intent: Intent = Intent(this, PronunciationResponse::class.java)
        val responses = responsePronunciationAdult.filter { it["name"]  == correctAnswers[position]}
        intent.putExtra("responses", responses as Serializable)
        startActivity(intent)
    }

}