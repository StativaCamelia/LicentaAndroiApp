package com.example.speechtherapy.bundles.common.helpers

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.google.firebase.auth.FirebaseUser
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class RecordingUtils(val context: Context, val activity: Activity) {

    private val PERMISSION_CODE = 16
    private var uniqueID = UUID.randomUUID().toString()
    private var mediaRecorder: MediaRecorder? = null

    fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)
            return true
        else {
            ActivityCompat.requestPermissions(activity, arrayOf(android.Manifest.permission.RECORD_AUDIO), PERMISSION_CODE)
            return false
        }
    }

    @RequiresApi(Build.VERSION_CODES.FROYO)
    fun startMediaRecorder(user: FirebaseUser): Pair<String, MediaRecorder?> {
        val recordPath: String = activity.getExternalFilesDir("/")!!.absolutePath
        val formatter =
            SimpleDateFormat("yyyy_MM_dd_hh_mm_ss", Locale.ENGLISH)
        val now = Date()
        uniqueID = UUID.randomUUID().toString()
        val recordFile = user.displayName + "." + formatter.format(now) + "_" + uniqueID + ".mp3"
        mediaRecorder = MediaRecorder()
        mediaRecorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)
        mediaRecorder!!.setAudioSamplingRate(16000)
        mediaRecorder!!.setAudioEncodingBitRate(16000)
        mediaRecorder!!.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        mediaRecorder!!.setAudioChannels(1)
        mediaRecorder!!.setOutputFile("$recordPath/$recordFile")
        mediaRecorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        try {
            mediaRecorder!!.prepare()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        mediaRecorder!!.start()
        return Pair(recordFile, mediaRecorder)
    }


}