package com.example.speechtherapy.bundles.services.SpeechRecognisionAPI

import com.example.speechtherapy.bundles.model.dataModel.Recording
import com.example.speechtherapy.bundles.model.responses.SpeechCNNResponse
import com.example.speechtherapy.bundles.model.responses.SpeechRecognizerResponse
import retrofit2.Call
import retrofit2.http.*

interface APIServicePronunciation {
        @POST("/recordCMU")
        fun postRecordingCMU(@Body recording: Recording): Call<SpeechRecognizerResponse>
        @POST("/recordCNN")
        fun postRecordingCNN(@Body recording: Recording): Call<SpeechCNNResponse>

}