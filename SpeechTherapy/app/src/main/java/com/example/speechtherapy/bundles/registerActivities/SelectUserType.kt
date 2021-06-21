package com.example.speechtherapy.bundles.registerActivities

import android.os.Bundle
import android.view.View
import android.widget.Button
import com.example.speechtherapy.R
import com.example.speechtherapy.bundles.common.BaseActivity
import com.example.speechtherapy.bundles.services.UserServices.UserServices

class SelectUserType: BaseActivity(), View.OnClickListener {

    private lateinit var startStudent: Button
    private lateinit var startTeacher: Button
    private val userService: UserServices = UserServices()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_type)
        initViews()
    }

    private fun initViews(){
        startStudent = findViewById(R.id.startStudent)
        startStudent.setOnClickListener(this)
        startTeacher = findViewById(R.id.startTeacher)
        startTeacher.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.startTeacher -> userService.updateUser(authService.getUser()?.uid, mapOf("type" to "teacher"))
            R.id.startStudent -> userService.updateUser(authService.getUser()?.uid, mapOf("type" to "student"))
        }
        super.startNewActivity(VoiceCloneSample())
    }


}