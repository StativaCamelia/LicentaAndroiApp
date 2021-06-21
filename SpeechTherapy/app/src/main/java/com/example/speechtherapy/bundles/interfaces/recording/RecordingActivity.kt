package com.example.speechtherapy.bundles.interfaces.recording

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.os.SystemClock
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import com.example.speechtherapy.R
import com.example.speechtherapy.bundles.common.BaseActivity
import com.example.speechtherapy.bundles.common.helpers.RecordingUtils
import com.example.speechtherapy.bundles.common.helpers.ServerError
import com.example.speechtherapy.bundles.services.SpeechRecognisionAPI.APIServicePronunciation
import com.example.speechtherapy.bundles.services.SpeechRecognisionAPI.ServiceBuilderPronunciation
import com.example.speechtherapy.bundles.speechRecognisionActivity.alertDialogs.BadRecording
import com.example.speechtherapy.bundles.speechRecognisionActivity.alertDialogs.SpeechFeedback
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.StorageReference
import java.io.File

open class RecordingActivity: BaseActivity() {

     var maxim: Int =0
     lateinit var recordText: TextView
     lateinit var recordTime: Chronometer
     lateinit var recordButton: ImageView
     var mediaRecorder: MediaRecorder? = null
     var recordFile: String? = null
     var isRecoding = false
     lateinit var storage: FirebaseStorage
     var think = false
     lateinit var exerciseText: String
     val request = ServiceBuilderPronunciation.buildService(APIServicePronunciation::class.java)
     lateinit var checkRecording: BadRecording
     lateinit var serverError: ServerError
     lateinit var speechFeedback: SpeechFeedback
     lateinit var CMU: Button
     lateinit var CNN: Button
     lateinit var recordingUtils: RecordingUtils

     fun initView() {
        checkRecording = BadRecording(this, this)
        speechFeedback = SpeechFeedback(maxim, this, this)
        serverError = ServerError(this, this)
        recordText = findViewById(R.id.record_text)
        recordTime = findViewById(R.id.record_time)
        recordButton = findViewById(R.id.record_button)
        CMU = findViewById(R.id.CMU)
        CNN = findViewById(R.id.CNN)
    }

    fun progressDialog(): ProgressDialog {
        val progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Loading...")
        progressDialog.setMessage("Application is loading, please wait")
        progressDialog.setIcon(R.drawable.exercise_thinking)
        progressDialog.show()
        return progressDialog
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun recordingLogic() {
        if (isRecoding) {
            stopRecording()
        } else {
            if (recordingUtils.checkPermissions()) {
                startRecording()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun stopRecording() {
        mediaRecorder!!.stop()
        mediaRecorder!!.release()
        mediaRecorder = null
        updateDataOnStop()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun startRecording() {
        think = true
        isRecoding = true
        startTimer()
        val (recordFileNew, mediaRecorderNew) = recordingUtils.startMediaRecorder(user)
        recordFile = recordFileNew
        mediaRecorder = mediaRecorderNew
        recordingDisplay(R.drawable.recording_stop_adult, "Press the button when you are done!")
    }

    private fun startTimer() {
        recordTime.base = SystemClock.elapsedRealtime()
        recordTime.start()
    }

    fun initlizeViews() {
        setScreenTitle("Test you Speech")
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun recordingDisplay(imageResource: Int, text: String) {
        recordButton.setImageDrawable(resources.getDrawable(imageResource, null))
        recordText.text = text
    }

    private fun updateDataOnStop() {
        think = false
        isRecoding = false
        recordTime.stop()
        Toast.makeText(this, "Successfully recorded", Toast.LENGTH_SHORT).show()
        uploadFile()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun uploadFile() {
        val fStorage = storage.reference
        val recordPath: String? = getExternalFilesDir("/")?.absolutePath
        val metadata = StorageMetadata.Builder()
            .setContentType("audio/mp3")
            .setCustomMetadata("user", user.displayName)
            .build()
        val filePath: StorageReference? = recordFile?.let { fStorage.child("Audio").child(it) }
        val uri = Uri.fromFile(File("$recordPath/$recordFile"))
        filePath?.putFile(uri, metadata)?.addOnSuccessListener {
            recordingDisplay(
                R.drawable.recording_start_adult,
                "Press the button to start recording"
            )
            recordTime.base = SystemClock.elapsedRealtime()
        }
    }
}