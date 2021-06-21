package com.example.speechtherapy.bundles.speechRecognisionActivity.alertDialogs

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.TextView
import com.example.speechtherapy.R
import com.example.speechtherapy.bundles.speechRecognisionActivity.feedback.Feedback

class SpeechFeedback(private val maxim: Int, val context: Context, val activity: Activity) {

    private lateinit var pitch: String
    private lateinit var nativeSpectogram: String
    private lateinit var spectogram: String

    private val nextButton = { _: DialogInterface, _: Int ->
        val position = activity.intent.extras?.getInt("item")!!.toInt()
        activity.finish()
        val intent = activity.intent
        val b = Bundle()
        if(position < maxim) {
            b.putInt("item", position + 1)
        }
        else{
            b.putInt("item", 0)
        }
        intent.putExtras(b)
        activity.startActivity(activity.intent)
    }

    fun showDialog(correctAnswer: List<String>, actualAnswer: List<String>, pitch: String, nativeSpectogram: String, spectogram: String) {
        this.pitch= pitch
        this.nativeSpectogram = nativeSpectogram
        this.spectogram = spectogram

        val dialogBuilder = AlertDialog.Builder(context).create()
        val view = activity.layoutInflater.inflate(R.layout.base_pronunciation_response, null)
        val correctAnswerView = view.findViewById<TextView>(R.id.correctAnswer)
        correctAnswerView.text = correctAnswer.toString()
        val actualAnswerView = view.findViewById<TextView>(R.id.actualAnswer)
        actualAnswerView.text = actualAnswer.toString()
        dialogBuilder.setView(view)
        if(correctAnswer == actualAnswer) {
            dialogBuilder.setTitle("Congratulations!")
            dialogBuilder.setIcon(R.drawable.exercise_succes)
            dialogBuilder.setButton(
                AlertDialog.BUTTON_POSITIVE, "See more info!"
            ) { dialog, _ -> dialog.dismiss() }
        }
        else{
            dialogBuilder.setTitle("You can do better!")
            dialogBuilder.setIcon(R.drawable.exercise_thinking)
            dialogBuilder.setButton(
                AlertDialog.BUTTON_POSITIVE, "See more info!", DialogInterface.OnClickListener(function = feedback))
        }
        dialogBuilder.setButton(
            AlertDialog.BUTTON_NEUTRAL,
            "Next",
            DialogInterface.OnClickListener(function = nextButton)
        )
        dialogBuilder.show()
        updateSucces()
    }

    fun showDialogCNN(answer: String, pitch: String, nativeSpectogram: String, spectogram: String) {
        this.pitch= pitch
        this.nativeSpectogram = nativeSpectogram
        this.spectogram = spectogram
        val dialogBuilder = AlertDialog.Builder(context).create()
        dialogBuilder.setMessage("Your pronunciation was $answer")
        dialogBuilder.setTitle("Answer!")
        dialogBuilder.setIcon(R.drawable.exercise_thinking)
        dialogBuilder.setButton(
            AlertDialog.BUTTON_POSITIVE, "See more info!", DialogInterface.OnClickListener(function = feedback))

        dialogBuilder.setButton(
            AlertDialog.BUTTON_NEUTRAL,
            "Next",
            DialogInterface.OnClickListener(function = nextButton)
        )
        dialogBuilder.show()
        updateSucces()
    }

    private val feedback = { _: DialogInterface, _: Int ->
        activity.finish()
        val intent = Intent(context, Feedback::class.java)
        val extras = Bundle()
        extras.putString("pitch", pitch)
        extras.putString("spectogram", spectogram)
        extras.putString("native_spectogram", nativeSpectogram)
        intent.putExtras(extras)
        activity.startActivity(intent)
    }


    private fun startMusic(file: Int) {
        val mp: MediaPlayer = MediaPlayer.create(context, file)
        mp.start()
    }

    private fun updateSucces() {
        startMusic(R.raw.win)
     }

    fun updateError() {
        startMusic(R.raw.error)
    }

}