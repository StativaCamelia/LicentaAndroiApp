package com.example.speechtherapy.bundles.registerActivities.profile

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.example.speechtherapy.R
import com.example.speechtherapy.bundles.classesActivities.teacher.ClassesActivityTeacher
import com.example.speechtherapy.bundles.classesActivities.teacher.HomeworksActivityTeacher
import com.example.speechtherapy.bundles.registerActivities.VoiceCloneSample

class ProfileActivityTeacher: BaseProfileActivity(), View.OnClickListener {

    private lateinit var language: TextView
    private lateinit var email: TextView
    private lateinit var userTypeContainer: TextView
    private lateinit var allClasses: TextView
    private lateinit var allHomeworks: Button
    private lateinit var voiceClone: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.teacher_profile_activity)
        initViews()
        super.setUserData(email, userTypeContainer, profilePhoto)
    }

    private fun initViews(){
        voiceClone = findViewById(R.id.voiceClone)
        language = findViewById(R.id.language)
        val pref = this.getSharedPreferences("prefs", 0)
        language.text = pref.getString("language", "en").toString()
        userTypeContainer = findViewById(R.id.userType)
        profilePhoto = findViewById(R.id.profilePhoto)
        email = findViewById(R.id.userEmail)
        allClasses = findViewById(R.id.seeClasses)
        allHomeworks = findViewById(R.id.seeHomeworks)
        initActions()
    }

    fun initActions(){
        language.setOnClickListener(this)
        registerForContextMenu(language)
        profilePhoto.setOnClickListener(this)
        registerForContextMenu(profilePhoto)
        allClasses.setOnClickListener(this)
        allHomeworks.setOnClickListener(this)
    }

    override fun onStart() {
        super.onStart()
        super.auth()
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.profilePhoto -> super.profilePhoto.showContextMenu()
            R.id.seeClasses -> startNewActivity(ClassesActivityTeacher())
            R.id.seeHomeworks -> startNewActivity(HomeworksActivityTeacher())
            R.id.voiceClone -> startNewActivity(VoiceCloneSample())
        }
    }

}