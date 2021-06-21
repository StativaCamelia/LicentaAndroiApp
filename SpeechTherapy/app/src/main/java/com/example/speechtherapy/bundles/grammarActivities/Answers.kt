package com.example.speechtherapy.bundles.grammarActivities

import android.content.Context
import android.media.MediaPlayer
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.example.speechtherapy.R

open class Answers(val context: Context) {

    fun checkAnswer(answer: String, correctAnswer: String, correctContainer: TextView, wordDefinition: TextView?, submit: Button, next: Button){
        correctContainer.text = correctAnswer
        correctContainer.visibility = View.VISIBLE
        wordDefinition?.visibility = View.VISIBLE
        submit.visibility = View.GONE
        if(correctAnswer== answer) {
            correctContainer.setTextColor(context.getColor(R.color.green))
            startMusic(R.raw.win)
        }
        else{
            correctContainer.setTextColor(context.getColor(R.color.red))
            startMusic(R.raw.error)
        }
        next.visibility = View.VISIBLE
    }

    private fun startMusic(file: Int) {
        val mp: MediaPlayer = MediaPlayer.create(context, file)
        mp.start()
    }
}