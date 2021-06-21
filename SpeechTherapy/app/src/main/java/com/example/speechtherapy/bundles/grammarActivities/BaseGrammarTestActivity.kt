package com.example.speechtherapy.bundles.grammarActivities

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.speechtherapy.R
import com.example.speechtherapy.bundles.common.BaseActivity
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

open class BaseGrammarTestActivity : BaseActivity() {

    private lateinit var storage: FirebaseStorage

    override fun onStart() {
        super.onStart()
        storage = Firebase.storage
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.base_activity_grammar)
        user = authService.getUser()!!
        initlizeViews()
    }

    private fun initlizeViews() {
        setScreenTitle("Test you Grammar")
    }

    fun onCreateParams(fragment: Fragment, tag: String){
        supportFragmentManager.beginTransaction().add(R.id.frameLayout, fragment, tag).commit()
    }

}