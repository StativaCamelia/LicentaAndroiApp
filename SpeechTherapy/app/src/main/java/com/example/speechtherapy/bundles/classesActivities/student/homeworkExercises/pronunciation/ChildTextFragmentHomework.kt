package com.example.speechtherapy.bundles.classesActivities.student.homeworkExercises.pronunciation
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.RequiresApi
import com.example.speechtherapy.R
import com.example.speechtherapy.bundles.common.helpers.GlideApp
import com.example.speechtherapy.bundles.interfaces.pronunciation.PronunciationFragment
import com.google.firebase.storage.FirebaseStorage


@Suppress("DEPRECATION")
class ChildTextFragmentHomework(val dataStore: String, val homeworkUID: String, val userUID: String,var position: Int,val collections: ArrayList<String>, val names: ArrayList<String>, val uri: ArrayList<String>, val titles: ArrayList<String>) : PronunciationFragment(), View.OnClickListener {

    var maxim: Int = collections.size
    private lateinit var imagePlace: ImageView


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragment = inflater.inflate(R.layout.child_fragment_speech, container, false)
        initView(fragment)
        initAction()
        initHelpers()
        getData()
        return fragment
    }

    fun initView(fragment: View) {
        imagePlace = fragment.findViewById(R.id.imagePlaceDigit)
        listen = fragment.findViewById(R.id.exercise_listen)
        listenText = fragment.findViewById(R.id.exercise_listen_text)
        exerciseTextContainer = fragment.findViewById(R.id.exercise_text)
        nextButton = fragment.findViewById(R.id.exercise_next)
        listenOwnVoice = fragment.findViewById(R.id.exercise_listen_own_voice)
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
            exerciseTextContainer.text = titles[position]
            val storage = FirebaseStorage.getInstance()
            val gsReference = storage.reference.child(uri[position])
            context?.let {
                GlideApp.with(it)
                    .load(gsReference)
                    .into(imagePlace)
            }
        }
        else{
            context?.let { internetConn.showDialog() }
        }
    }



}