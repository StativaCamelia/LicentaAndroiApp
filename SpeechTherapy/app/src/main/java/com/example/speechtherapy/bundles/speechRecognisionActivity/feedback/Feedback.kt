package com.example.speechtherapy.bundles.speechRecognisionActivity.feedback

import android.os.Bundle
import android.widget.ImageView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.speechtherapy.R
import com.example.speechtherapy.bundles.common.BaseActivity
import com.example.speechtherapy.bundles.common.helpers.GlideApp

class Feedback: BaseActivity() {

    private lateinit var pitchContainer: ImageView
    private lateinit var spectogramContainer: ImageView
    private lateinit var nativeSpectogramContainer: ImageView
    private lateinit var pitch: String
    private lateinit var nativeSpectogram: String
    private lateinit var spectogram: String
    private lateinit var student: String
    private var uri = "https://03ce3db4bff3.ngrok.io/feedback"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.feedback)
        getData()
        initViews()
    }

    private fun getData(){
        if(this.intent.getStringExtra("student").toString() != "null"){
            student = this.intent.getStringExtra("student").toString()
        }
        else{
            student = user.displayName.toString()
        }
        pitch = this.intent.getStringExtra("pitch").toString()
        nativeSpectogram = this.intent.getStringExtra("native_spectogram").toString()
        spectogram = this.intent.getStringExtra("spectogram").toString()
    }

    private fun initViews(){
        pitchContainer = findViewById(R.id.pitch)
        val pitchReference = "$uri/${student}/$pitch"
        loadImage(pitchContainer, pitchReference)
        spectogramContainer = findViewById(R.id.spectogram)
        val spectogramReference = "$uri/${student}/$spectogram"
        loadImage(spectogramContainer, spectogramReference)
        nativeSpectogramContainer = findViewById(R.id.native_spectogram)
        val nativeReference = "$uri/$nativeSpectogram"
        loadImage(nativeSpectogramContainer, nativeReference)
    }


    private fun loadImage(container: ImageView, reference: String){
        applicationContext.let {
            GlideApp.with(it)
                .load(reference)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(container)
        }
    }


}