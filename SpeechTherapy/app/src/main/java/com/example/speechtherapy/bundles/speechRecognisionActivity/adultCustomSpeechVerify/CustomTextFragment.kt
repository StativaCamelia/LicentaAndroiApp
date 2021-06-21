package com.example.speechtherapy.bundles.speechRecognisionActivity.adultCustomSpeechVerify

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.speechtherapy.R
import com.example.speechtherapy.bundles.common.helpers.TextToSpeechUtils
import com.google.android.material.textfield.TextInputEditText


open class CustomTextFragment : Fragment(), View.OnClickListener {

    private lateinit var listen: ImageView
    private lateinit var listenOwnVoice: ImageView
    private var inputText: String = ""
    private lateinit var inputTextContainer: TextInputEditText
    private lateinit var clearButton: Button
    private lateinit var listenText: TextView
    private lateinit var listenTextOwnVoice: TextView
    private var isTextToSpeech = false
    private lateinit var utilsTextToSpeech: TextToSpeechUtils


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragment = inflater.inflate(R.layout.adult_fragment_speech_custom, container, false)
        initView(fragment)
        initAction()
        utilsTextToSpeech = context?.let { TextToSpeechUtils(it, listenText, ::setTextToSpeech) }!!
        context?.let { activity?.let { it1 ->
            utilsTextToSpeech.initTextToSpeech(it,
                it1)
        } }
        return fragment
    }

    private fun setTextToSpeech(){
        isTextToSpeech = !isTextToSpeech
    }


    fun initView(fragment: View) {
        listen = fragment.findViewById(R.id.custom_listen)
        listenText = fragment.findViewById(R.id.custom_listen_text)
        listenOwnVoice = fragment.findViewById(R.id.custom_listen_own_voice)
        listenTextOwnVoice = fragment.findViewById(R.id.custom_listen_own_voice_text)
        inputTextContainer = fragment.findViewById(R.id.custom_input_text)
        clearButton = fragment.findViewById(R.id.custom_clear)
    }

    private fun initAction() {
        clearButton.setOnClickListener(this)
        listen.setOnClickListener(this)
        listenOwnVoice.setOnClickListener(this)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onClick(view: View?) {
        if (view != null) {
            when (view.id) {
                R.id.custom_listen -> {
                    getInputText()
                    utilsTextToSpeech.listenWithGoogleLogic(isTextToSpeech, inputText)
                }
                R.id.custom_listen_own_voice -> {
                    listenToYouOwnVoice()
                }
                R.id.custom_clear -> {
                    inputTextContainer.text?.clear()
                }

            }
        }
    }

    private fun listenToYouOwnVoice() {
        Toast.makeText(context, "Function available later", Toast.LENGTH_SHORT).show()
    }

    private fun getInputText() {
        inputText = inputTextContainer.text.toString()
    }

    override fun onStop() {
        super.onStop()
        utilsTextToSpeech.stopTextToSpeechGoogle()
    }


}
