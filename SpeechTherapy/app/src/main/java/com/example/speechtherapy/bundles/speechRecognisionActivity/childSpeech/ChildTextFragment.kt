package com.example.speechtherapy.bundles.speechRecognisionActivity.childSpeech
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
import com.example.speechtherapy.bundles.common.helpers.GlideApp
import com.example.speechtherapy.bundles.interfaces.pronunciation.PronunciationFragment
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage


@Suppress("DEPRECATION")
class ChildTextFragment(var dataStore: String, var item: Int) : PronunciationFragment(), View.OnClickListener {

    private lateinit var imagePlace: ImageView
    var maxim: Int = 0

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
                    httpGetVoice(exerciseText, dataStore)
                }
                R.id.exercise_next -> {
                }

            }
        }
    }

    private fun getData() {
        if (internetConn.checkConnection()) {
            nextButton.visibility = View.GONE
            Firebase.firestore.collection(dataStore).get().addOnSuccessListener { result ->
                val result = result.toList()
                maxim = result.size
                exerciseTextContainer.text = result[item].data["name"] as CharSequence?
                val storage = FirebaseStorage.getInstance()
                val gsReference = storage.reference.child(result[item].data["uri"] as String)
                context?.let {
                    GlideApp.with(it)
                        .load(gsReference)
                        .into(imagePlace)
                }
            }.addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting documents.", exception)
            }
        }
        else{
            context?.let { internetConn.showDialog() }
        }
    }



}