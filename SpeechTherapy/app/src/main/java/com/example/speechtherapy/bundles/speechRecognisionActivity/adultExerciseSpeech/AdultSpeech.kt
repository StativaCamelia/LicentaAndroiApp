package com.example.speechtherapy.bundles.speechRecognisionActivity.adultExerciseSpeech

import android.os.Bundle
import com.example.speechtherapy.R
import com.example.speechtherapy.bundles.speechRecognisionActivity.ExerciseRecordingActivity

class AdultSpeech : ExerciseRecordingActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val b = intent.extras
        val dataStore = b?.getString("dataStore").toString()
        val item = b?.getInt("item")
        if (savedInstanceState == null) {
            val fragment = AdultTextFragment(dataStore, item!!)
            supportFragmentManager.beginTransaction().replace(R.id.frameLayout, fragment).commit()
        }
    }

}