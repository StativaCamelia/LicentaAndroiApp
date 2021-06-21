package com.example.speechtherapy.bundles.services.SpeechRecognisionAPI

import com.example.speechtherapy.bundles.model.responses.VoiceCloneResponse
import retrofit2.Call
import retrofit2.http.*

interface APIServiceVoiceClone {
    @POST("/voiceClone")
    fun postRecording(@Body postBody: Map<String, String>): Call<VoiceCloneResponse>
    @GET("/voiceClone")
    fun getRecording(@Query("user") user: String?, @Query("datastore") datastore: String?, @Query("word") word: String?): Call<VoiceCloneResponse>
}