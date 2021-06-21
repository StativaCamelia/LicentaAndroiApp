package com.example.speechtherapy.bundles.registerActivities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.example.speechtherapy.R
import com.example.speechtherapy.bundles.common.BaseActivity
import com.example.speechtherapy.bundles.menuActivities.AdultMenu
import com.example.speechtherapy.bundles.menuActivities.ChildMenu


class SelectCategory: BaseActivity(), View.OnClickListener{

    private lateinit var beginAdult: Button
    private lateinit var beginChildren:Button


    override fun onStart() {
        super.onStart()
        super.auth()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.base_activity_select_category)
        initlizeViews()
        initView()
        initAction()
    }


    fun initView(){
        beginAdult = findViewById(R.id.adultButton)
        beginChildren = findViewById(R.id.childrenButton)
    }

    fun initAction(){
        beginAdult.setOnClickListener(this)
        beginChildren.setOnClickListener(this)
    }

    private fun initlizeViews() {
        setScreenTitle("Select Category")
    }

    override fun onClick(v: View) {
        when(v.id) {
            R.id.adultButton -> {
                val intentMain = Intent(applicationContext, AdultMenu::class.java)
                startActivity(intentMain)
            }
            R.id.childrenButton -> {
                val intentMain = Intent(applicationContext, ChildMenu::class.java)
                startActivity(intentMain)
            }
        }
    }


}