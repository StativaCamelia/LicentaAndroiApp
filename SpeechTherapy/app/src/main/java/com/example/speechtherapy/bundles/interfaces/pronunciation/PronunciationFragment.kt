package com.example.speechtherapy.bundles.interfaces.pronunciation

import android.app.ProgressDialog
import android.media.MediaPlayer
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.speechtherapy.R
import com.example.speechtherapy.bundles.common.helpers.InternetConnection
import com.example.speechtherapy.bundles.common.helpers.ServerError
import com.example.speechtherapy.bundles.common.helpers.TextToSpeechUtils
import com.example.speechtherapy.bundles.model.responses.VoiceCloneResponse
import com.example.speechtherapy.bundles.services.SpeechRecognisionAPI.APIServiceVoiceClone
import com.example.speechtherapy.bundles.services.SpeechRecognisionAPI.ServiceBuilderVoiceClone
import com.example.speechtherapy.bundles.services.UserServices.UserServices
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.storage.FirebaseStorage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

open class PronunciationFragment: Fragment() {

     lateinit var listen: ImageView
     lateinit var listenOwnVoice: ImageView
     var exerciseText: String = ""
     lateinit var exerciseTextContainer: TextView
     lateinit var nextButton: Button
     lateinit var listenText: TextView
     lateinit var listenTextOwnVoice: TextView
     var isTextToSpeech = false
     lateinit var utilsTextToSpeech: TextToSpeechUtils
     lateinit var internetConn: InternetConnection
     val userServices: UserServices = UserServices()
    var user: FirebaseUser = userServices.getCurrentUser()
     lateinit var serverError: ServerError
     val requestVoiceClone = ServiceBuilderVoiceClone.buildService(APIServiceVoiceClone::class.java)

    override fun onStop() {
        super.onStop()
        context?.let { utilsTextToSpeech.stopTextToSpeechGoogle() }
    }


    fun initHelpers(){
        serverError = ServerError(context, activity)
        utilsTextToSpeech = context?.let { TextToSpeechUtils(it, listenText, ::setTextToSpeech) }!!
        context?.let { activity?.let { it1 ->
            utilsTextToSpeech.initTextToSpeech(it,
                it1)
        } }
        internetConn = InternetConnection(requireContext(), requireActivity())
    }

    fun setTextToSpeech(){
        isTextToSpeech = !isTextToSpeech
    }

    fun getInputText() {
        exerciseText = exerciseTextContainer.text.toString()
    }

    fun httpGetVoice(exerciseText: String, collection: String) {
        val call = requestVoiceClone.getRecording(user.uid, collection, exerciseText)
        val progressDialog = ProgressDialog(context)
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
                        val storage = FirebaseStorage.getInstance()
                        if(response.body()?.status == true) {
                            response.body()?.result?.let { it ->
                                storage.reference.child(it).downloadUrl.addOnSuccessListener {
                                    val mediaPlayer = MediaPlayer()
                                    mediaPlayer.setDataSource(it.toString())
                                    mediaPlayer.setOnPreparedListener { player ->
                                        player.start()
                                    }
                                    mediaPlayer.prepareAsync()
                                }
                            }
                        }
                        else{
                            Toast.makeText(requireContext(), response.body()?.result, Toast.LENGTH_SHORT).show()
                        }
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