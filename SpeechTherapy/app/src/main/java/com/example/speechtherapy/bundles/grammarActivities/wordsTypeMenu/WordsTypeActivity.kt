package com.example.speechtherapy.bundles.grammarActivities.wordsTypeMenu

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import com.example.speechtherapy.R
import com.example.speechtherapy.bundles.classesActivities.homework.AssignHomework
import com.example.speechtherapy.bundles.common.BaseActivity
import com.example.speechtherapy.bundles.common.helpers.DataListener.OnGetDataListener
import com.example.speechtherapy.bundles.services.UserServices.UserServices
import com.example.speechtherapy.bundles.grammarActivities.adult.AdultGrammarTest
import com.example.speechtherapy.bundles.grammarActivities.adult.AdultWordsTypeData
import com.example.speechtherapy.bundles.grammarActivities.child.ChildGrammarTest
import com.example.speechtherapy.bundles.grammarActivities.child.ChildrenWordsTypeData
import com.google.firebase.firestore.DocumentSnapshot

class WordsTypeActivity : BaseActivity() {

    private lateinit var listView: ListView
    private lateinit var listViewAdapter: WordsTypeViewModelAdapter
    private lateinit var assignHomework: AssignHomework
    private val userServices: UserServices = UserServices()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.base_activity_words_type)
        user = authService.getUser()!!
        assignHomework = AssignHomework(this, user)
        initlizeViews()
        initView()
        initAction()
    }

    override fun onStart() {
        super.onStart()
        super.auth()
    }

    private fun initView() {
        listView = findViewById(R.id.listViewWords)
        val b = intent.extras
        val type = b?.getString("type").toString()
        listViewAdapter = if(type == "adult") {
            WordsTypeViewModelAdapter(this, AdultWordsTypeData.getListViewModelList())
        } else{
            WordsTypeViewModelAdapter(this, ChildrenWordsTypeData.getListViewModelList())
        }
        listView.adapter = listViewAdapter
    }

    private fun initAction() {
        listView.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                onClickItem(position)
            }
        userServices.getUserById(user.uid, object : OnGetDataListener {
            override fun onSuccess(documentSnapshot: DocumentSnapshot?) {
                if (documentSnapshot?.data?.get("type") == "teacher") {
                    listView.onItemLongClickListener = AdapterView.OnItemLongClickListener { _: AdapterView<*>, view1: View, position: Int, l: Long ->
                        onLongClickItem(position)
                        return@OnItemLongClickListener true
                    }
                }
            }
        })

    }

    fun onLongClickItem(position: Int){
        val collectionPath = listViewAdapter.getType(position)
        if (collectionPath != null) {
            assignHomework.assignHomeworkDialog(collectionPath)
        }
    }

    private fun onClickItem(position: Int) {
        val bundle = Bundle()
        val typeStorage = listViewAdapter.getType(position)
        bundle.putInt("position", position)
        bundle.putString("collectionPath", typeStorage)
        val activity: Activity
        val b = intent.extras
        val type = b?.getString("type").toString()
        activity = if(type == "adult") {
            AdultGrammarTest()
        } else {
            ChildGrammarTest()
        }
        val intent = Intent(applicationContext, activity::class.java)
        intent.putExtras(b!!)
        intent.putExtras(bundle)
        startActivity(intent)
    }



    private fun initlizeViews() {
        setScreenTitle("Words Types")
    }

}