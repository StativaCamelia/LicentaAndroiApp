package com.example.speechtherapy.bundles.registerActivities

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import com.example.speechtherapy.R
import com.example.speechtherapy.bundles.common.BaseActivity
import com.example.speechtherapy.bundles.common.helpers.RecordingUtils
import com.example.speechtherapy.bundles.common.helpers.ServerError
import com.example.speechtherapy.bundles.model.responses.VoiceCloneResponse
import com.example.speechtherapy.bundles.services.SpeechRecognisionAPI.APIServiceVoiceClone
import com.example.speechtherapy.bundles.services.SpeechRecognisionAPI.ServiceBuilderVoiceClone
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_base_speech_sample.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.util.*


class VoiceCloneSample: BaseActivity(), View.OnClickListener {

    private lateinit var recordText: TextView
    private lateinit var skipVoiceCloning: Button
    private lateinit var recordTime: Chronometer
    private lateinit var recordButton: ImageView
    private var mediaRecorder: MediaRecorder? = null
    private var recordFile: String? = null
    private var isRecoding = false
    private lateinit var storage: FirebaseStorage
    private var think = false
    private val request = ServiceBuilderVoiceClone.buildService(APIServiceVoiceClone::class.java)
    private lateinit var serverError: ServerError
    private lateinit var recordingUtils: RecordingUtils

    override fun onStart() {
        super.onStart()
        storage = Firebase.storage
        super.auth()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base_speech_sample)
        user = super.authService.getUser()!!
        recordingUtils = RecordingUtils(this, this)
        initlizeViews()
        initView()
        initAction()
    }

    private fun initlizeViews() {
        setScreenTitle("Let's get to know each other")
    }

    private fun initView() {
        serverError = ServerError(this, this)
        recordText = findViewById(R.id.record_text)
        recordTime = findViewById(R.id.record_time)
        recordButton = findViewById(R.id.record_button)
        skipVoiceCloning = findViewById(R.id.skipVoiceCloning)
    }

    private fun initAction() {
        recordButton.setOnClickListener(this)
        skipVoiceCloning.setOnClickListener(this)
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
            R.id.skipVoiceCloning -> {
                startNewActivity(SelectCategory())
            }
        }
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
    fun startRecording() {
        think = true
        isRecoding = true
        startTimer()
        val (recordFileNew, mediaRecorderNew) = recordingUtils.startMediaRecorder(user)
        recordFile = recordFileNew
        mediaRecorder = mediaRecorderNew
        recordingDisplay(R.drawable.recording_stop_adult, "Press the button when you are done!")
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun recordingDisplay(imageResource: Int, text: String) {
        recordButton.setImageDrawable(resources.getDrawable(imageResource, null))
        recordText.text = text
    }

    private fun startTimer() {
        recordTime.base = SystemClock.elapsedRealtime()
        recordTime.start()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun stopRecording() {
        mediaRecorder!!.stop()
        mediaRecorder!!.release()
        mediaRecorder = null
        updateDataOnStop()
    }

    private fun updateDataOnStop(){
        think = false
        isRecoding = false
        recordTime.stop()
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
        val filePath: StorageReference? = recordFile?.let { fStorage.child("Audio").child("Sample").child(user.uid).child(it) }
        val uri = Uri.fromFile(File("$recordPath/$recordFile"))
        filePath?.putFile(uri, metadata)?.addOnSuccessListener {
            recordingDisplay(
                R.drawable.recording_start_adult,
                "Press the button to start recording"
            )
            httpPostVoice("$recordFile")
            recordTime.base = SystemClock.elapsedRealtime()
        }
    }

    private fun httpPostVoice(filename: String) {
        val call = request.postRecording(mapOf<String, String>("filename" to filename, "user" to user.uid))
        val progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Loading...")
        progressDialog.setMessage("Application is loading, please wait")
        progressDialog.setIcon(R.drawable.exercise_thinking)
        progressDialog.show()
        try {
            call.enqueue(object : Callback<VoiceCloneResponse> {
                override fun onResponse(
                    call: Call<VoiceCloneResponse>,
                    response: Response<VoiceCloneResponse>
                ) {
                    progressDialog.dismiss()
                    if(response.isSuccessful) {
                        startNewActivity(SelectCategory())
                        Toast.makeText(this@VoiceCloneSample, "Sample successfully submited", Toast.LENGTH_SHORT).show()
                    }
                    else{
                        serverError.showDialog()
                    }
                }

                override fun onFailure(call: Call<VoiceCloneResponse>, t: Throwable) {
                    progressDialog.dismiss()
                }
            })
        }
        catch(ex: Exception){
            serverError.showDialog()
        }
    }


}