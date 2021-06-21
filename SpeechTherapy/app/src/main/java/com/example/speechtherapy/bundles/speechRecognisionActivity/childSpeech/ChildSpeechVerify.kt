package com.example.speechtherapy.bundles.speechRecognisionActivity.childSpeech

import android.os.Bundle
import com.example.speechtherapy.R
import com.example.speechtherapy.bundles.speechRecognisionActivity.ExerciseRecordingActivity

class ChildSpeechVerify : ExerciseRecordingActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val b = intent.extras
        val dataStore = b?.getString("dataStore").toString()
        val item = b?.getInt("item")
        if (savedInstanceState == null) {
            val fragment = ChildTextFragment(dataStore, item!!)
            supportFragmentManager.beginTransaction().replace(R.id.frameLayout, fragment).commit()
        }
    }

}