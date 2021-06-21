package com.example.speechtherapy.bundles.speechRecognisionActivity.wordList

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import com.example.speechtherapy.R
import com.example.speechtherapy.bundles.classesActivities.homework.AssignHomework
import com.example.speechtherapy.bundles.common.BaseActivity
import com.example.speechtherapy.bundles.common.helpers.DataListener.OnGetDataListener
import com.example.speechtherapy.bundles.common.helpers.DataListener.OnGetListListener
import com.example.speechtherapy.bundles.common.helpers.InternetConnection
import com.example.speechtherapy.bundles.services.ExerciseService.ExerciseService
import com.example.speechtherapy.bundles.services.UserServices.UserServices
import com.example.speechtherapy.bundles.model.dataModel.Exercise
import com.example.speechtherapy.bundles.speechRecognisionActivity.ExerciseRecordingActivity
import com.example.speechtherapy.bundles.speechRecognisionActivity.adultExerciseSpeech.AdultSpeech
import com.example.speechtherapy.bundles.speechRecognisionActivity.childSpeech.ChildSpeechVerify
import com.google.firebase.firestore.DocumentSnapshot
import java.util.*
import kotlin.collections.ArrayList


class WordsList: BaseActivity() {

    private lateinit var listView: ListView
    private lateinit var listViewAdapter: WordViewModelAdapter
    private lateinit var internetConn: InternetConnection
    private lateinit var dataStore: String
    private val exerciseService: ExerciseService = ExerciseService()
    private lateinit var assignHomework: AssignHomework
    private val userServices: UserServices = UserServices()
    val listViewModelArrayList = ArrayList<WordViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.child_words_list)
        dataStore = intent.extras?.getString("dataStore").toString()
        internetConn = InternetConnection(this, this)
        assignHomework = AssignHomework(this, user)
        initView()
        initAction()
        setActivityName()
    }

    private fun setActivityName() {
        setScreenTitle("Exercise pronunciation")
    }

    override fun onStart() {
        super.onStart()
        super.auth()
    }

    private fun initView() {
        listView = findViewById(R.id.listView)
        getData()
    }

    private fun getData() {
        if (internetConn.checkConnection()) {
            exerciseService.getExercises(dataStore, object : OnGetListListener {
                override fun onSuccess(result: MutableList<*>) {
                    val result = result as MutableList<Exercise>
                    for (i in 0..result.size - 1) {
                        listViewModelArrayList.add(WordViewModel(i, result[i].name, result[i].uri))
                    }
                    listViewAdapter = WordViewModelAdapter(this@WordsList, listViewModelArrayList, dataStore)
                    listView.adapter = listViewAdapter
                }
            })
        }
        else{
            this.let { internetConn.showDialog() }
        }
    }

    private fun initAction() {
        listView.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                onClickItem(position)
            }
        userServices.getUserById(user.uid, object : OnGetDataListener {
            override fun onSuccess(documentSnapshot: DocumentSnapshot?) {
                if(documentSnapshot?.data?.get("type") == "teacher"){
                    listView.onItemLongClickListener = AdapterView.OnItemLongClickListener(){ adapterView: AdapterView<*>, view1: View, position: Int, l: Long ->
                        onLongClickItem(position)
                        return@OnItemLongClickListener true
                    }
                }
            }
        })
    }

    fun onLongClickItem(position: Int){
        listViewModelArrayList[position].title?.let {
            assignHomework.assignHomeworkDialog(dataStore,
                it,  false)
        }
    }


    private fun startRecordingActivity(
        activity: ExerciseRecordingActivity,
        dataStore: String,
        position: Int
    ){
        val intent = Intent(applicationContext, activity.javaClass)
        val b = Bundle()
        b.putString("dataStore", dataStore)
        b.putInt("item", position)
        intent.putExtras(b)
        startActivity(intent)
    }

    private fun onClickItem(position: Int) {
        if(dataStore == "adult_pronunciation")
            startRecordingActivity(AdultSpeech(), dataStore, position)
        else
            startRecordingActivity(ChildSpeechVerify(), dataStore, position)
    }

    override fun onBackPressed() {
        val count = supportFragmentManager.backStackEntryCount
        if (count == 0) {
            listView.visibility = View.VISIBLE
            super.onBackPressed()
        } else {
            for (fragment in supportFragmentManager.fragments) {
                supportFragmentManager.beginTransaction().remove(fragment).commit()
            }
            listView.visibility = View.VISIBLE
            super.onBackPressed()
        }
    }

}