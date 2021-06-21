package com.example.speechtherapy.bundles.registerActivities.profile

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.example.speechtherapy.R
import com.example.speechtherapy.bundles.classesActivities.student.ClassesActivityStudent
import com.example.speechtherapy.bundles.classesActivities.student.HomeworkListStudent
import com.example.speechtherapy.bundles.registerActivities.VoiceCloneSample


class ProfileActivityStudent: BaseProfileActivity(),  View.OnClickListener {

    private lateinit var language: TextView
    private lateinit var email: TextView
    private lateinit var userTypeContainer: TextView
    private lateinit var classes: Button
    private lateinit var homeworks: Button
    private lateinit var voiceClone: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.student_profile_activity)
        initViews()
        super.setUserData(email, userTypeContainer, profilePhoto)
    }

    private fun initViews(){
        homeworks = findViewById(R.id.seeHomeworks)
        voiceClone = findViewById(R.id.voiceClone)
        language = findViewById(R.id.language)
        val pref = this.getSharedPreferences("prefs", 0)
        language.text = pref.getString("language", "en").toString()
        userTypeContainer = findViewById(R.id.userType)
        profilePhoto = findViewById(R.id.profilePhoto)
        email = findViewById(R.id.userEmail)
        classes = findViewById(R.id.seeClasses)
        initActions()
    }

    fun initActions(){
        homeworks.setOnClickListener(this)
        language.setOnClickListener(this)
        registerForContextMenu(language)
        registerForContextMenu(profilePhoto)
        profilePhoto.setOnClickListener(this)
        voiceClone.setOnClickListener(this)
        classes.setOnClickListener(this)
    }

    override fun onStart() {
        super.onStart()
        super.auth()
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.profilePhoto -> super.profilePhoto.showContextMenu()
            R.id.seeClasses -> startNewActivity(ClassesActivityStudent())
            R.id.seeHomeworks -> startNewActivity(HomeworkListStudent())
            R.id.voiceClone -> startNewActivity(VoiceCloneSample())
        }
    }

}