package com.example.speechtherapy.bundles.interfaces.grammar

import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.speechtherapy.bundles.common.helpers.InternetConnection
import com.example.speechtherapy.bundles.common.helpers.TextToSpeechUtils
import com.example.speechtherapy.bundles.grammarActivities.Answers
import com.example.speechtherapy.bundles.services.ExerciseService.ExerciseService
import com.example.speechtherapy.bundles.services.ResponseService.ResponseService
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

open class GrammarFragment: Fragment() {

     var isTextToSpeech = false
     lateinit var grammarListen: ImageButton
     lateinit var word: TextView
     lateinit var submit: MaterialButton
     lateinit var answerInput: TextInputEditText
     lateinit var correctAnswer: TextView
     lateinit var nextButton: MaterialButton
     lateinit var utilsTextToSpeech: TextToSpeechUtils
     lateinit var listenText: TextView
     lateinit var internetConn: InternetConnection
     private lateinit var answers: Answers
     lateinit var exercisesService: ExerciseService
     private val responseService = ResponseService()
     var collectionPath = ""

    fun initHelpers(){
        utilsTextToSpeech = context?.let { TextToSpeechUtils(it, listenText, ::setTextToSpeech) }!!
        context?.let { activity?.let { it1 ->
            utilsTextToSpeech.initTextToSpeech(it,
                it1)
        } }
        internetConn = InternetConnection(requireContext(), requireActivity())
        answers = Answers(requireContext())
        exercisesService = ExerciseService()
    }


    private fun setTextToSpeech(){
        isTextToSpeech = !isTextToSpeech
    }


    override fun onStop() {
        super.onStop()
        utilsTextToSpeech.stopTextToSpeechGoogle()
    }

    override fun onDestroy() {
        super.onDestroy()
        utilsTextToSpeech.stopTextToSpeechGoogle()
    }

    fun checkAnswer(){
        val answer = answerInput.text.toString()
        val correct = word.text.toString()
        answers.checkAnswer(answer, correct, correctAnswer, null, submit, nextButton)
    }

    fun updateAnswer(answer : String, title: String, uri: String, collection: String, userUID: String, homeworkUID: String){
        val exercise = mutableMapOf<String, Any>()
        exercise["name"] = title
        exercise["uri"] = uri
        exercise["collectionPath"] = collection
        exercise["answer"] = answer
        responseService.updateResponse("grammar", userUID, homeworkUID, exercise)
    }
}