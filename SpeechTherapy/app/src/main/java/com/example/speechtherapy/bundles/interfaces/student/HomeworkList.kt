package com.example.speechtherapy.bundles.interfaces.student

import android.os.Bundle
import android.widget.AdapterView
import android.widget.ListView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.speechtherapy.R
import com.example.speechtherapy.bundles.classesActivities.student.homeworkExercises.ExerciseAdapterChild
import com.example.speechtherapy.bundles.classesActivities.student.homeworkExercises.grammar.GrammarHomeworkActivity
import com.example.speechtherapy.bundles.classesActivities.student.homeworkExercises.pronunciation.PronunciationHomeworkActivity
import com.example.speechtherapy.bundles.common.BaseActivity
import com.example.speechtherapy.bundles.services.HomeworkService.HomeworkService

open class HomeworkList: BaseActivity() {

     lateinit var homework : ListView
     var names: ArrayList<String> = arrayListOf()
     var descriptions: ArrayList<String> = arrayListOf()
     var titles: ArrayList<String> = arrayListOf()
     var collections: ArrayList<String> = arrayListOf()
     lateinit var homeworkUID: String
     lateinit var userUID: String
     val homeworkService: HomeworkService = HomeworkService()
     private lateinit var swipeRefresh: SwipeRefreshLayout
     lateinit var exerciseType: String

    fun setRefresh(){
        swipeRefresh = findViewById(R.id.swiperefresh)
        swipeRefresh.setOnRefreshListener {
            swipeRefresh.isRefreshing = false
        }
    }

    fun onClickItem(type: String, position: Int, activity: BaseActivity){
        val b = Bundle()
        b.putInt("item", position)
        b.putString("type", type)
        b.putStringArrayList("names", names)
        b.putStringArrayList("descriptions", descriptions)
        b.putStringArrayList("titles", titles)
        b.putStringArrayList("collections", collections)
        b.putString("homeworkUID", homeworkUID)
        b.putString("userUID", userUID)
        startActivityWithExtras(activity, b)
    }

    fun initAction(exerciseType: String, userType: String){
        homework.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            if(this.exerciseType == exerciseType){
                onClickItem(userType, position, GrammarHomeworkActivity())
            }
            else{
                onClickItem(userType, position, PronunciationHomeworkActivity())
            }
        }
    }

    fun initView(exercises:MutableList<MutableMap<String, String>>){
        var position = 0
        for(exercise in exercises){
            position += 1
            names.add("Exercise $position")
            exercise["name"]?.let { titles.add(it) }
            exercise["uri"]?.let{ descriptions.add(it)}
            exercise["collectionPath"]?.let{ collections.add(it)}
        }
        homework = findViewById(R.id.grammarAdultHomework)
        val adapter = ExerciseAdapterChild(this, names, descriptions)
        homework.adapter = adapter
        adapter.notifyDataSetChanged()
    }

}