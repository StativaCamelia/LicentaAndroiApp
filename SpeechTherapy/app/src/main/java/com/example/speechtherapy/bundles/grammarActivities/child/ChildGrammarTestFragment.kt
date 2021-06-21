package com.example.speechtherapy.bundles.grammarActivities.child
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.RequiresApi
import com.example.speechtherapy.R
import com.example.speechtherapy.bundles.interfaces.grammar.GrammarFragment


class ChildGrammarTestFragment: GrammarFragment(), View.OnClickListener {

    private lateinit var imagePlaceGrammar: ImageView

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragment = inflater.inflate(R.layout.child_fragment_grammar_check, container, false)
        initView(fragment)
        initAction()
        getExtras()
        initHelpers()
        getExercise()
        return fragment
    }

    private fun getExtras(){
        val b = requireActivity().intent.extras
        collectionPath = b?.getString("collectionPath").toString()
    }

    private fun getExercise(){
        if (internetConn.checkConnection()) {
            exercisesService.getExerciseWithImage(requireContext(), utilsTextToSpeech, collectionPath, imagePlaceGrammar, word, isTextToSpeech)
        }
        else{
            context?.let { internetConn.showDialog() }
        }
    }

    private fun initAction(){
        grammarListen.setOnClickListener(this)
        submit.setOnClickListener(this)
        nextButton.setOnClickListener(this)
    }

    private fun initView(fragment: View){
        imagePlaceGrammar = fragment.findViewById(R.id.imagePlaceGrammar)
        grammarListen = fragment.findViewById(R.id.childGrammarListen)
        word = fragment.findViewById(R.id.childWord)
        submit = requireActivity().findViewById(R.id.submitButton)
        answerInput = requireActivity().findViewById(R.id.answerGrammar)
        correctAnswer = requireActivity().findViewById(R.id.correctAnswer)
        nextButton = requireActivity().findViewById(R.id.nextButton)
        listenText = fragment.findViewById(R.id.grammarListenText)
        nextButton.visibility = View.GONE
    }

    override fun onClick(view: View?) {
        if (view != null) {
            when (view.id) {
                R.id.childGrammarListen -> {
                    utilsTextToSpeech.listenWithGoogleLogic(isTextToSpeech, word.text as String)
                }
                R.id.submitButton -> {
                    checkAnswer()
                }
                R.id.nextButton ->{
                    nextImage()
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        utilsTextToSpeech.stopTextToSpeechGoogle()
    }

    override fun onDestroy() {
        super.onDestroy()
        utilsTextToSpeech.stopTextToSpeechGoogle()
    }

    private fun nextImage(){
        getExercise()
        submit.visibility = View.VISIBLE
        answerInput.setText("")
        correctAnswer.text = ""
    }

}