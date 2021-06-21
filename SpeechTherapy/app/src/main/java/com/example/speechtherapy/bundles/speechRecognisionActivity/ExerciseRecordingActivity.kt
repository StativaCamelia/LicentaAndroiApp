@file:Suppress("LiftReturnOrAssignment")

package com.example.speechtherapy.bundles.speechRecognisionActivity

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.speechtherapy.R
import com.example.speechtherapy.bundles.classesActivities.student.homeworkExercises.pronunciation.AdultTextFragmentHomework
import com.example.speechtherapy.bundles.classesActivities.student.homeworkExercises.pronunciation.ChildTextFragmentHomework
import com.example.speechtherapy.bundles.common.helpers.RecordingUtils
import com.example.speechtherapy.bundles.interfaces.recording.RecordingActivity
import com.example.speechtherapy.bundles.model.dataModel.Recording
import com.example.speechtherapy.bundles.model.responses.SpeechCNNResponse
import com.example.speechtherapy.bundles.model.responses.SpeechRecognizerResponse
import com.example.speechtherapy.bundles.speechRecognisionActivity.adultExerciseSpeech.AdultTextFragment
import com.example.speechtherapy.bundles.speechRecognisionActivity.childSpeech.ChildTextFragment
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.adult_fragment_speech.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


open class ExerciseRecordingActivity : RecordingActivity(), View.OnClickListener {


    private var isRecording = false

    override fun onStart() {
        super.onStart()
        storage = Firebase.storage
        super.auth()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.base_activity_recording)
        user = super.authService.getUser()!!
        recordingUtils = RecordingUtils(this, this)
        getMaxim()
        initlizeViews()
        initView()
        initAction()
    }

    fun getMaxim(){
        val fragments = supportFragmentManager.fragments
        for (fragment in fragments) {
            if (fragment is ChildTextFragmentHomework) {
                maxim = fragment.maxim
            }
            else if(fragment is AdultTextFragmentHomework){
                maxim = fragment.maxim
            }
        }
    }

    private fun initAction() {
        recordButton.setOnClickListener(this)
        CMU.setOnClickListener(this)
        CNN.setOnClickListener(this)
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onClick(v: View) {
        when (v.id) {
            R.id.image_back_button -> {
                if (isRecording) {
                    Toast.makeText(applicationContext, "Please stop the recording before leaving", Toast.LENGTH_LONG).show()
                    return
                }
                onBackPressed()
            }
            R.id.record_button -> {
                recordingLogic()
            }
            R.id.CMU ->{
                httpPostCMU("$recordFile")
            }
            R.id.CNN ->{
                httpPostCNN("$recordFile")
            }
        }
    }

    private fun getExerciseWord(): String {
        val fragments = supportFragmentManager.fragments
        for (fragment in fragments) {
            if (fragment is ChildTextFragment || fragment is AdultTextFragment) {
                exerciseText = fragment.exercise_text.text.toString()
            }
        }
        return exerciseText
    }

    private fun getDataType(): String {
        val fragments = supportFragmentManager.fragments
        for (fragment in fragments) {
            when(fragment){
                is ChildTextFragment -> return fragment.dataStore
                is AdultTextFragment -> return fragment.dataStore
            }
        }
        return ""
    }

    private fun httpPostCMU(filename: String) {
        val recording = Recording(filename, getExerciseWord(), getDataType())
        val call = request.postRecordingCMU(recording)
        val progressDialog = progressDialog()
        try {
            call.enqueue(object : Callback<SpeechRecognizerResponse> {
                override fun onResponse(
                    call: Call<SpeechRecognizerResponse>,
                    response: Response<SpeechRecognizerResponse>
                ) {
                    progressDialog.dismiss()
                    if (response.isSuccessful) {
                        if (response.body()!!.recording_status == "Good") {
                            val correctAnswer = response.body()!!.correct_phones_sequence
                            val phoneSequence = response.body()!!.phones_sequence
                            val pitch = response.body()!!.pitch
                            val nativeSpectogram = response.body()!!.native_spectogram
                            val spectogram = response.body()!!.spectogram
                            speechFeedback.showDialog(correctAnswer, phoneSequence, pitch, nativeSpectogram, spectogram)
                        } else if (response.body()!!.recording_status == "Bad") {
                            checkRecording.showDialog()
                        }
                    } else {
                        serverError.showDialog()
                    }
                }

                override fun onFailure(call: Call<SpeechRecognizerResponse>, t: Throwable) {
                    progressDialog.dismiss()
                }
            })
        } catch (ex: Exception) {
            serverError.showDialog()
        }

}

    private fun httpPostCNN(filename: String) {
        val recording = Recording(filename, getExerciseWord(), getDataType())
        val call = request.postRecordingCNN(recording)
        val progressDialog = progressDialog()
        try {
            call.enqueue(object : Callback<SpeechCNNResponse> {
                override fun onResponse(
                    call: Call<SpeechCNNResponse>,
                    response: Response<SpeechCNNResponse>
                ) {
                    progressDialog.dismiss()
                    if (response.isSuccessful) {
                        if (response.body()!!.recording_status == "Good") {
                            val answer = response.body()!!.predict
                            val pitch = response.body()!!.pitch
                            val nativeSpectogram = response.body()!!.native_spectogram
                            val spectogram = response.body()!!.spectogram
                            speechFeedback.showDialogCNN(answer, pitch, nativeSpectogram, spectogram)

                        } else if (response.body()!!.recording_status == "Bad") {
                            checkRecording.showDialog()
                        }
                    } else {
                        serverError.showDialog()
                    }
                }

                override fun onFailure(call: Call<SpeechCNNResponse>, t: Throwable) {
                    TODO("Not yet implemented")
                }
            })
        } catch (ex: Exception) {
            serverError.showDialog()
        }

    }

}