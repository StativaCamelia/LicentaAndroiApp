@file:Suppress("LiftReturnOrAssignment")

package com.example.speechtherapy.bundles.classesActivities.student.homeworkExercises.pronunciation

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.speechtherapy.R
import com.example.speechtherapy.bundles.common.helpers.RecordingUtils
import com.example.speechtherapy.bundles.interfaces.recording.RecordingActivity
import com.example.speechtherapy.bundles.model.dataModel.Recording
import com.example.speechtherapy.bundles.model.responses.SpeechCNNResponse
import com.example.speechtherapy.bundles.model.responses.SpeechRecognizerResponse
import com.example.speechtherapy.bundles.services.ResponseService.ResponseService
import com.example.speechtherapy.bundles.speechRecognisionActivity.adultExerciseSpeech.AdultTextFragment
import com.example.speechtherapy.bundles.speechRecognisionActivity.childSpeech.ChildTextFragment
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.adult_fragment_speech.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


open class HomeworkRecordingActivity : RecordingActivity(), View.OnClickListener {

    private val responseService = ResponseService()
    private lateinit var homeworkUID : String
    private lateinit var userUID: String
    private lateinit var nameExercise: String
    private lateinit var collectionPathExercise: String
    private lateinit var uriExercise: String

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

    private fun getMaxim(){
        val fragments = supportFragmentManager.fragments
        for (fragment in fragments) {
            if (fragment is ChildTextFragment) {
                maxim = fragment.maxim
            }
            else if(fragment is AdultTextFragment){
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
                if (isRecoding) {
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
            if (fragment is AdultTextFragmentHomework || fragment is ChildTextFragmentHomework) {
                exerciseText = fragment.exercise_text.text.toString()
            }
        }
        return exerciseText
    }

    private fun getDataType(): String {
        val fragments = supportFragmentManager.fragments
        for (fragment in fragments) {
            when(fragment){
                is AdultTextFragmentHomework -> return fragment.dataStore
                is ChildTextFragmentHomework ->return fragment.dataStore
            }
        }
        return ""
    }

    private fun setExerciseInfo(){
        val fragments = supportFragmentManager.fragments
        for (fragment in fragments) {
            when(fragment){
                is AdultTextFragmentHomework -> {
                    nameExercise = fragment.titles[fragment.position]
                    uriExercise = fragment.descriptions[fragment.position]
                    collectionPathExercise = fragment.collections[fragment.position]
                    userUID = fragment.userUID
                    homeworkUID = fragment.homeworkUID
                }
                is ChildTextFragmentHomework ->{
                    nameExercise = fragment.titles[fragment.position]
                    uriExercise = fragment.uri[fragment.position]
                    collectionPathExercise = fragment.collections[fragment.position]
                    userUID = fragment.userUID
                    homeworkUID = fragment.homeworkUID
                }
            }
        }
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
                        createResponseCMU(filename, response)
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
                        createResponseCNN(filename, response)
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

    fun createResponseCNN(filename: String, response: Response<SpeechCNNResponse>){
        val body = response.body()
        val exercise: MutableMap<String, Any> = createResponseBaseInfo(response)
        if(body!!.recording_status == "Good"){
            val (audioFile, feedbackInfo) = getAudioInfo(filename, response.body()!!.pitch, response.body()!!.spectogram, response.body()!!.native_spectogram)
            exercise["audio_file"] = audioFile
            exercise["feedback_files"] = feedbackInfo
            exercise["answer"] = response.body()!!.predict
        }
        else{
            val audioFile = getAudio(filename)
            exercise["audio_file"] = audioFile
            exercise["answer"] = "Not recognized"
        }
        responseService.updateResponse("pronunciation", userUID, homeworkUID, exercise)
    }

    fun createResponseBaseInfo(response: Response<*>): MutableMap<String, Any>{
        val exercise = mutableMapOf<String, Any>()
        setExerciseInfo()
        exercise["typeEvaluation"] = "CNN"
        exercise["name"] = nameExercise
        exercise["uri"] = uriExercise
        exercise["collectionPath"] = collectionPathExercise
        return exercise
    }

    fun createResponseCMU(filename: String, response: Response<SpeechRecognizerResponse>){
        val body = response.body()
        val exercise: MutableMap<String, Any> = createResponseBaseInfo(response)
        if(body!!.recording_status == "Good"){
            val (audioFile, feedbackInfo) = getAudioInfo(filename, response.body()!!.pitch, response.body()!!.spectogram, response.body()!!.native_spectogram)
            exercise["audio_file"] = audioFile
            exercise["feedback_files"] = feedbackInfo
            exercise["answer"] = mutableMapOf("correct_phonems" to body.correct_phones_sequence.toString(), "actual_phonems" to body.phones_sequence.toString())
        }
        else{
            val audioFile = getAudio(filename)
            exercise["audio_file"] = audioFile
            exercise["answer"] = "Not recognized"
        }
        responseService.updateResponse("pronunciation", userUID, homeworkUID, exercise)
    }
    fun getAudio(filename: String): MutableMap<String, String?>{
        return mutableMapOf("filename" to filename, "student" to user.displayName)
    }

    private fun getAudioInfo(filename: String, pitch: String, spectogram: String, spectogram_native: String): Pair<MutableMap<String, String?>, MutableMap<String, String>>{
        val audioFile = getAudio(filename)
        val feedback = mutableMapOf( "pitch" to pitch,  "spectogram" to spectogram, "spectogram_native" to spectogram_native)
        return Pair(audioFile, feedback)
    }


}