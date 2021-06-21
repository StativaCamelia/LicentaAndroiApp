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
import com.example.speechtherapy.bundles.contactActivity.MapsActivity
import com.example.speechtherapy.bundles.grammarActivities.wordsTypeMenu.WordsTypeActivity
import com.example.speechtherapy.bundles.informationActivity.ArpabetChart
import com.example.speechtherapy.bundles.informationActivity.InformationActivity
import com.example.speechtherapy.bundles.speechRecognisionActivity.adultCustomSpeechVerify.CustomSpeechVerify
import com.example.speechtherapy.bundles.speechRecognisionActivity.adultExerciseSpeech.AdultSpeech
import com.example.speechtherapy.bundles.speechRecognisionActivity.wordList.WordsList
import com.google.firebase.firestore.DocumentSnapshot

class AdultMenu : BaseActivity(), View.OnClickListener, View.OnLongClickListener {

    private lateinit var usernameField: TextView
    private lateinit var informationButton: CardView
    private lateinit var contactButton: CardView
    private lateinit var phonems: CardView
    private lateinit var exerciseRecording: CardView
    private lateinit var grammarButton: CardView
    private val userServices: UserServices = UserServices()
    private lateinit var assignHomework: AssignHomework

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.adult_activity_menu)
        super.auth()
        user = authService.getUser()!!
        assignHomework = AssignHomework(this, user)
        initlizeViews()
        initView()
        initAction()
    }

    @SuppressLint("SetTextI18n")
    fun initView() {
        usernameField = findViewById(R.id.username)
        informationButton = findViewById(R.id.adultInfo)
        contactButton = findViewById(R.id.adultContact)
        phonems = findViewById(R.id.arpabetSounds)
        exerciseRecording = findViewById(R.id.adultWords)
        grammarButton = findViewById(R.id.adultGrammar)
        usernameField.text = "Welcome, " + user.displayName
    }

    fun initAction() {
        exerciseRecording.setOnClickListener(this)
        informationButton.setOnClickListener(this)
        contactButton.setOnClickListener(this)
        phonems.setOnClickListener(this)
        grammarButton.setOnClickListener(this)
        userServices.getUserById(user.uid, object : OnGetDataListener {
            override fun onSuccess(documentSnapshot: DocumentSnapshot?) {
                if(documentSnapshot?.data?.get("type") == "teacher"){
                    setLongClick()
                }
            }
        })
    }

    private fun setLongClick() {
        exerciseRecording.setOnLongClickListener(this)
        phonems.setOnLongClickListener(this)
        grammarButton.setOnLongClickListener(this)
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
            R.id.image_back_button -> {
               onBackPressed()
            }
            R.id.adultInfo->{
                super.startNewActivity(InformationActivity())
            }
            R.id.adultContact->{
                super.startNewActivity(MapsActivity())
            }
            R.id.arpabetSounds->{
                super.startNewActivity(ArpabetChart())
            }
            R.id.adultWords->{
                startWordList(WordsList(), "adult_pronunciation")
            }
            R.id.adultGrammar->{
                val extras = Bundle()
                extras.putString("type", "adult")
                super.startActivityWithExtras(WordsTypeActivity(), extras)
            }
        }
    }

    override fun onLongClick(v: View?): Boolean {
        when (v?.id) {
            R.id.adultWords->{
                assignHomework.assignHomeworkDialog("adult_pronunciation")
            }
        }
        return true
    }


}