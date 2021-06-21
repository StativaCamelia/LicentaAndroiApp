package com.example.speechtherapy.bundles.menuActivities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.example.speechtherapy.R
import com.example.speechtherapy.bundles.classesActivities.homework.AssignHomework
import com.example.speechtherapy.bundles.common.BaseActivity
import com.example.speechtherapy.bundles.common.helpers.DataListener.OnGetDataListener
import com.example.speechtherapy.bundles.services.UserServices.UserServices
import com.example.speechtherapy.bundles.grammarActivities.wordsTypeMenu.WordsTypeActivity
import com.example.speechtherapy.bundles.speechRecognisionActivity.wordList.WordsList
import com.google.firebase.firestore.DocumentSnapshot


class ChildMenu : BaseActivity(), View.OnClickListener, View.OnLongClickListener {

    private lateinit var usernameField: TextView
    private lateinit var wordsButton: CardView
    private lateinit var phrasesButton: CardView
    private lateinit var grammarButton: CardView
    private lateinit var animalsButton: CardView
    private lateinit var numbersButton: CardView
    private val userServices: UserServices = UserServices()
    private lateinit var assignHomework: AssignHomework

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.child_activity_menu)
        user = authService.getUser()!!
        assignHomework = AssignHomework(this, user)
        initlizeViews()
        initView()
        initAction()
    }

    @SuppressLint("SetTextI18n")
    private fun initView() {
        usernameField = findViewById(R.id.username)
        usernameField.text = "Welcome, " + user.displayName
        grammarButton = findViewById(R.id.childGrammar)
        animalsButton = findViewById(R.id.childAnimals)
        numbersButton = findViewById(R.id.childNumbers)
        wordsButton = findViewById(R.id.childWords)
        phrasesButton = findViewById(R.id.childTime)
    }

    private fun initAction() {
        grammarButton.setOnClickListener(this)
        wordsButton.setOnClickListener(this)
        phrasesButton.setOnClickListener(this)
        animalsButton.setOnClickListener(this)
        numbersButton.setOnClickListener(this)
        userServices.getUserById(user.uid, object : OnGetDataListener {
            override fun onSuccess(documentSnapshot: DocumentSnapshot?) {
                if (documentSnapshot?.data?.get("type") == "teacher") {
                    setLongClick()
                }
            }
        })
    }

    private fun setLongClick() {
        grammarButton.setOnLongClickListener(this)
        wordsButton.setOnLongClickListener(this)
        phrasesButton.setOnLongClickListener(this)
        animalsButton.setOnLongClickListener(this)
        numbersButton.setOnLongClickListener(this)
    }

    private fun initlizeViews() {
        setScreenTitle("Menu")
    }

    private fun startWordList(activity: WordsList, dataStore: String) {
        val intent = Intent(applicationContext, activity.javaClass)
        val b = Bundle()
        b.putString("dataStore", dataStore)
        intent.putExtras(b)
        startActivity(intent)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.childWords -> {
                startWordList(WordsList(), "children_common")
            }
            R.id.childTime -> {
                startWordList(WordsList(), "children_time")
            }
            R.id.childGrammar -> {
                val extras = Bundle()
                extras.putString("type", "child")
                super.startActivityWithExtras(WordsTypeActivity(), extras)
            }
            R.id.childAnimals -> {
                startWordList(WordsList(), "children_animals")
            }
            R.id.childNumbers -> {
                startWordList(WordsList(), "children_digits")
            }
        }
    }

    override fun onLongClick(v: View?): Boolean {
        when (v?.id) {
            R.id.childWords -> {
                assignHomework.assignHomeworkDialog("children_common")
            }
            R.id.childTime -> {
                assignHomework.assignHomeworkDialog("children_phrases")
            }
            R.id.childAnimals -> {
                assignHomework.assignHomeworkDialog("children_animals")
            }
            R.id.childNumbers -> {
                assignHomework.assignHomeworkDialog("children_digits")
            }
        }
        return true
    }







}