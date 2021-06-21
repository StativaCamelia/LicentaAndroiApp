package com.example.speechtherapy.bundles.classesActivities.student.homeworkExercises

import android.os.Bundle
import com.example.speechtherapy.R
import com.example.speechtherapy.bundles.common.helpers.DataListener.OnGetDataListener
import com.example.speechtherapy.bundles.interfaces.student.HomeworkList
import com.google.firebase.firestore.DocumentSnapshot

class ChildListHomework: HomeworkList() {

    private var exerciseGrammarChild: MutableList<MutableMap<String, String>> = mutableListOf()
    private var exercisePronunciationChild: MutableList<MutableMap<String, String>> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.homework_exercise)
        exerciseType = this.intent.getStringExtra("type").toString()
        setRefresh()
        getExercises()
    }

    override fun onDestroy() {
        super.onDestroy()
        val b = Bundle()
        b.putString("homeworkUID", homeworkUID)
        b.putString("userUID", userUID)
    }

    private fun getExercises(){
        homeworkUID = this.intent.getStringExtra("homeworkUID") as String
        userUID = this.intent.getStringExtra("userUID") as String
        homeworkService.getHomeworkById(homeworkUID, object : OnGetDataListener {
            override fun onSuccess(documentSnapshot: DocumentSnapshot?)
            {
                val exercises = documentSnapshot?.get("exercises") as MutableList<MutableMap<String, String>>
                for(exercise in exercises){
                    if(exercise["collectionPath"]?.contains("children_grammar") == true){
                        exerciseGrammarChild.add(exercise)
                    }
                    else if(exercise["collectionPath"]?.contains("children") == true){
                        exercisePronunciationChild.add(exercise)
                    }
                }
                if(exerciseType == "grammarChild"){
                    initView(exerciseGrammarChild)
                }
                else{
                    initView(exercisePronunciationChild)
                }
                initAction("grammarChild", "child")
            }

        })
    }


}