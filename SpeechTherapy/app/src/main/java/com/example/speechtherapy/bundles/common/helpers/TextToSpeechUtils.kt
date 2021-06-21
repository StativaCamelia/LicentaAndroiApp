package com.example.speechtherapy.bundles.common.helpers

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Build
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.speechtherapy.R
import java.util.*

class TextToSpeechUtils(context: Context, private var listenText: TextView, private var setTextToSpeech: () -> Unit) {

    private var activityContext = context

    private var textToSpeech: TextToSpeech? = null

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun initTextToSpeech(context : Context, activity: Activity) {
        textToSpeech = TextToSpeech(context) { status ->
            if (status != TextToSpeech.ERROR) {
                textToSpeech!!.language = Locale.UK
                textToSpeech!!.setOnUtteranceProgressListener(object :
                    UtteranceProgressListener() {
                    override fun onStart(utteranceId: String) {
                        Log.i("TextToSpeechUtils", "On Start")
                    }
                    @SuppressLint("ResourceType")
                    override fun onDone(utteranceId: String) {
                        activity.runOnUiThread {
                            modifyViewOnListen(context.getString(R.string.custom_listen_button))
                        }
                    }
                    override fun onError(utteranceId: String) {
                        Log.i("TextToSpeechUtils", "On Error")
                    }
                })
            } else {
                Log.i("TextToSpeechUtils", "Initialization Failed")
            }
        }
    }

    private fun startTextToSpeechGoogle(exerciseText: String) {
        val myHashAlarm: HashMap<String, String> = HashMap()
        if (exerciseText.isNotEmpty()) {
            modifyViewOnListen(activityContext.getString(R.string.custom_stop_listen))
            myHashAlarm[TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID] = exerciseText
            textToSpeech?.speak(exerciseText, TextToSpeech.QUEUE_FLUSH, myHashAlarm)
        } else {
            Toast.makeText(activityContext, "Nothing to listen", Toast.LENGTH_SHORT).show()
        }
    }

    fun stopTextToSpeechGoogle() {
        modifyViewOnListen(activityContext.getString(R.string.custom_listen_button))
        if (textToSpeech != null) {
            textToSpeech!!.stop()
        }
    }

    private fun modifyViewOnListen(text: String) {
        listenText.text = text
        setTextToSpeech()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun listenWithGoogleLogic(isTextToSpeech: Boolean, inputText: String) {
        when {
            !isTextToSpeech -> {
                startTextToSpeechGoogle(inputText) }
            else -> {
                stopTextToSpeechGoogle()
            }
            }
        }
    }
