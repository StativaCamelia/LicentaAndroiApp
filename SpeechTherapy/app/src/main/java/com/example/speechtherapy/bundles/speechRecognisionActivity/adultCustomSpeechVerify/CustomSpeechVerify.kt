package com.example.speechtherapy.bundles.speechRecognisionActivity.adultCustomSpeechVerify

import android.os.Bundle
import com.example.speechtherapy.R
import com.example.speechtherapy.bundles.speechRecognisionActivity.ExerciseRecordingActivity

class CustomSpeechVerify : ExerciseRecordingActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            val fragment = CustomTextFragment()
            supportFragmentManager.beginTransaction().replace(R.id.frameLayout, fragment).commit()
        }
    }

}