package com.example.speechtherapy.bundles.classesActivities.student.homeworkExercises.pronunciation

import android.os.Bundle
import com.example.speechtherapy.R

open class PronunciationHomeworkActivity: HomeworkRecordingActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val userType = this.intent.extras!!.getString("type").toString()
        val position = this.intent.extras!!.getInt("item")
        val names = this.intent.extras!!.getStringArrayList("names") as ArrayList<String>
        val descriptions = this.intent.extras!!.getStringArrayList("descriptions") as ArrayList<String>
        val titles = this.intent.extras!!.getStringArrayList("titles") as ArrayList<String>
        val homeworkUID = this.intent.extras!!.getString("homeworkUID") as String
        val userUID = this.intent.extras!!.getString("userUID") as String
        val collections = this.intent.extras!!.getStringArrayList("collections") as ArrayList<String>
        if (savedInstanceState == null) {
            if(userType == "adult") {
                val fragment = AdultTextFragmentHomework(
                    collections[position],
                    homeworkUID,
                    userUID,
                    position,
                    collections,
                    names,
                    descriptions,
                    titles
                )
                supportFragmentManager.beginTransaction().replace(R.id.frameLayout, fragment)
                    .commit()
            }else{
                val fragment = ChildTextFragmentHomework(collections[position], homeworkUID, userUID, position,collections, names, descriptions, titles)
                supportFragmentManager.beginTransaction().replace(R.id.frameLayout, fragment).commit()
            }
        }
    }
}