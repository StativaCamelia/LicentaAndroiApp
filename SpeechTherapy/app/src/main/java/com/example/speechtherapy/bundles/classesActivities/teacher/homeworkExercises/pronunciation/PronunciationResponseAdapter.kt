package com.example.speechtherapy.bundles.classesActivities.teacher.homeworkExercises.pronunciation

import android.annotation.SuppressLint
import android.app.Activity
import android.media.AudioManager
import android.media.MediaPlayer
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.speechtherapy.R


class PronunciationResponseAdapter(
    private val contextMain: Activity,
    val names: List<String>,
    val exercises: MutableList<MutableMap<String, Any>>
) :
    ArrayAdapter<String>(contextMain, R.layout.teacher_pronunciation_response, names){

    private var uri = "https://03ce3db4bff3.ngrok.io/audio"

    @SuppressLint("ViewHolder", "InflateParams")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val viewRow = contextMain.layoutInflater.inflate(
            R.layout.teacher_pronunciation_response,
            null,
            true
        )
        val listenButton = viewRow.findViewById<ImageView>(R.id.listenFile)
        listenButton.setOnClickListener { listenRecording(position) }
        setEvaluation(viewRow, position)
        return viewRow
    }


    private fun listenRecording(position: Int){
        val fileData = exercises[position]["audio_file"] as MutableMap<String, String>
        val filename = fileData["filename"]
        val student = fileData["student"]
        try {
            val player = MediaPlayer()
            player.setAudioStreamType(AudioManager.STREAM_MUSIC)
            player.setDataSource(
                "$uri/${student}/$filename"
            )
            player.prepare()
            player.start()
        } catch (e: Exception) {
            println(e.message)
        }
    }

    private fun setEvaluation(view: View, position: Int){
        val evaluationType = view.findViewById<TextView>(R.id.evaluationType)
        val prediction = view.findViewById<TextView>(R.id.prediction)
        val correctAnswer = view.findViewById<TextView>(R.id.correctAnswer)
        val labelCorrect = view.findViewById<TextView>(R.id.labelCorrect)
        evaluationType.text = exercises[position]["typeEvaluation"].toString()
        if(exercises[position]["typeEvaluation"].toString() == "CNN"){
            labelCorrect.visibility = View.INVISIBLE
            prediction.text = exercises[position]["answer"].toString()
        }
        else{
            if(exercises[position]["answer"] is String) {
                labelCorrect.visibility = View.INVISIBLE
                prediction.text = exercises[position]["answer"].toString()
            }
            else{
                val answer = exercises[position]["answer"] as MutableMap<String, String>
                prediction.text = answer["actual_phonems"].toString()
                correctAnswer.text = answer["correct_phonems"].toString()
            }
        }
    }
}