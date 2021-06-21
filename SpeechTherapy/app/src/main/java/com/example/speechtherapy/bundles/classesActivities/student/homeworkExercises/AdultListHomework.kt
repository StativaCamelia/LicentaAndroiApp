package com.example.speechtherapy.bundles.classesActivities.student.homeworkExercises

import android.os.Bundle
import com.example.speechtherapy.R
import com.example.speechtherapy.bundles.common.helpers.DataListener.OnGetDataListener
import com.example.speechtherapy.bundles.interfaces.student.HomeworkList
import com.google.firebase.firestore.DocumentSnapshot

class AdultListHomework: HomeworkList() {

    private var exerciseGrammarAdult: MutableList<MutableMap<String, String>> = mutableListOf()
    private var exercisePronunciationAdult: MutableList<MutableMap<String, String>> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.homework_exercise)
        exerciseType = this.intent.getStringExtra("type").toString()
        setRefresh()
        getExercises()
    }

    private fun getExercises(){
        homeworkUID = this.intent.getStringExtra("homeworkUID") as String
        userUID = this.intent.getStringExtra("userUID") as String

        homeworkService.getHomeworkById(homeworkUID, object : OnGetDataListener {
            override fun onSuccess(documentSnapshot: DocumentSnapshot?)
            {
                val exercises = documentSnapshot?.get("exercises") as MutableList<MutableMap<String, String>>
                for(exercise in exercises){
                    if(exercise["collectionPath"]?.contains("adult_grammat") == true || exercise["collectionPath"]?.contains("adult_grammar") == true ){
                        exerciseGrammarAdult.add(exercise)
                    }
                    if(exercise["collectionPath"]?.contains("adult_pronunciation") == true){
                        exercisePronunciationAdult.add(exercise)
                    }
                }
                if(exerciseType == "grammarAdult"){
                    initView(exerciseGrammarAdult)
                }
                else{
                    initView(exercisePronunciationAdult)
                }
                initAction("grammarAdult", "adult")
            }
        })
    }

}