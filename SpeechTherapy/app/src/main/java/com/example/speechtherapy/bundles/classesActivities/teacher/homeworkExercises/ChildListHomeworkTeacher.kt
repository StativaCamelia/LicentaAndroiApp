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


class ChildListHomeworkTeacher: HomeworkListTeacher(), AdapterView.OnItemClickListener {

    private var descriptions: ArrayList<String> = arrayListOf()
    private var responseGrammarChild: MutableList<MutableMap<String, String>> = mutableListOf()
    private var responsePronunciationChild: MutableList<MutableMap<String, String>> = mutableListOf()

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
        userUID = this.intent.getStringExtra("userUID") as String
        studentUID = this.intent.getStringExtra("studentUID") as String
        homeworkUID = this.intent.getStringExtra("homeworkUID") as String
        responseService.getResponseByHomeworkAndUser(homeworkUID, studentUID, object :
            OnGetDataListenerQuery {
            override fun onSuccess(querySnapshot: QuerySnapshot?) {
                val exercises = querySnapshot?.documents?.get(0)!!
                    .get("exercises") as MutableList<MutableMap<String, String>>
                for (exercise in exercises) {
                    if (exercise["collectionPath"]?.contains("children_grammar") == true) {
                        responseGrammarChild.add(exercise)
                    } else if (exercise["collectionPath"]?.contains("children") == true) {
                        responsePronunciationChild.add(exercise)
                    }
                }
                if (exerciseType == "grammarChild") {
                    initView(exerciseType, responseGrammarChild)
                } else {
                    initView(
                        exerciseType,
                        responsePronunciationChild.distinctBy { it["name"] }.toMutableList()
                    )
                    initAction()
                }
            }

        })
    }

    private fun initAction(){
       homework.setOnItemClickListener(this)
    }


    fun initView(type: String, exercises: MutableList<MutableMap<String, String>>){
        for(response in exercises){
            if(type == "grammarChild"){
                response["answer"]?.let{answers.add(it)}
            }
            else{
                answers.add("Click to see more")
            }
            response["name"]?.let { correctAnswers.add(it) }
            response["uri"]?.let{ descriptions.add(it)}
        }
        homework = findViewById(R.id.grammarAdultHomework)
        val adapter = ExerciseAdapterChildTeacher(
            this,
            correctAnswers.distinct().toMutableList(),
            answers,
            descriptions
        )
        homework.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val intent: Intent = Intent(this, PronunciationResponse::class.java)
        val responses = responsePronunciationChild.filter { it["name"]  == correctAnswers[position]}
        intent.putExtra("responses", responses as Serializable)
        startActivity(intent)
    }
}