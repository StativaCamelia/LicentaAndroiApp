package com.example.speechtherapy.bundles.classesActivities.student.homeworkExercises.grammar
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.RequiresApi
import com.example.speechtherapy.R
import com.example.speechtherapy.bundles.interfaces.grammar.GrammarFragment


class ChildGrammarHomeworkFragment(val homeworkUID: String, val userUID: String, var position: Int, val collections: ArrayList<String>, val names: ArrayList<String>, val uri: ArrayList<String>, private val titles: ArrayList<String>): GrammarFragment(), View.OnClickListener {

    private var maxElements = names.size
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
        initHelpers()
        getExercise()
        return fragment
    }

    private fun getExercise(){
        if (internetConn.checkConnection()) {
            exercisesService.getExerciseWithImageByName(
                requireContext(),
                imagePlaceGrammar,
                word,
                titles[position],
                uri[position]
                )
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
                    updateAnswer(answerInput.text.toString(), titles[position], uri[position], collections[position], userUID, homeworkUID)
                }
                R.id.nextButton ->{
                    nextImage()
                }
            }
        }
    }

    private fun nextImage(){
        if(position == maxElements - 1)
            position = 0
        else{
            position +=1
        }
        getExercise()
        submit.visibility = View.VISIBLE
        answerInput.setText("")
        correctAnswer.text = ""
    }



}