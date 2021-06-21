package com.example.speechtherapy.bundles.classesActivities.student.homeworkExercises.pronunciation

import android.content.ContentValues
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.RequiresApi
import com.example.speechtherapy.R
import com.example.speechtherapy.bundles.interfaces.pronunciation.PronunciationFragment
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


@Suppress("DEPRECATION")
class AdultTextFragmentHomework(val dataStore: String, val homeworkUID: String, val userUID: String, var position: Int,val collections: ArrayList<String>, val names: ArrayList<String>, val descriptions: ArrayList<String>, val titles: ArrayList<String>) : PronunciationFragment(), View.OnClickListener {

    var maxim: Int = collections.size
    private lateinit var answerImage: ImageView

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragment = inflater.inflate(R.layout.adult_fragment_speech, container, false)
        initView(fragment)
        initHelpers()
        initAction()
        getData()
        return fragment
    }

    override fun onStop() {
        super.onStop()
        context?.let { utilsTextToSpeech.stopTextToSpeechGoogle() }
    }


    fun initView(fragment: View) {
        listen = fragment.findViewById(R.id.exercise_listen)
        listenText = fragment.findViewById(R.id.exercise_listen_text)
        listenOwnVoice = fragment.findViewById(R.id.exercise_listen_own_voice)
        exerciseTextContainer = fragment.findViewById(R.id.exercise_text)
        nextButton = fragment.findViewById(R.id.exercise_next)
        answerImage = fragment.findViewById(R.id.exercise_image)
    }

    fun initAction() {
        nextButton.setOnClickListener(this)
        listen.setOnClickListener(this)
        listenOwnVoice.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        if (view != null) {
            when (view.id) {
                R.id.exercise_listen -> {
                    getInputText()
                    utilsTextToSpeech.listenWithGoogleLogic(isTextToSpeech, exerciseText)
                }
                R.id.exercise_listen_own_voice -> {
                    getInputText()
                    httpGetVoice(exerciseText, collections[position])
                }
                R.id.exercise_next -> {
                }

            }
        }
    }


    private fun getData() {
        if (internetConn.checkConnection()) {
            nextButton.visibility = View.GONE
            Firebase.firestore.collection(collections[position]).get().addOnSuccessListener { result ->
                val result = result.toList()
                exerciseTextContainer.text = result[position].data["name"] as CharSequence?
            }.addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting documents.", exception)
            }
        }
        else{
            context?.let { internetConn.showDialog() }
        }
    }


}