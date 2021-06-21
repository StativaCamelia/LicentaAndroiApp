package com.example.speechtherapy.bundles.classesActivities.teacher.homeworkExercises.pronunciation

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.speechtherapy.R
import com.example.speechtherapy.bundles.common.BaseActivity
import com.example.speechtherapy.bundles.speechRecognisionActivity.feedback.Feedback

class PronunciationResponse: BaseActivity(), AdapterView.OnItemClickListener {

    private lateinit var responses: MutableList<MutableMap<String, Any>>
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var responsesList: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.adult_activity_informations)
        setRefresh()
        responses = this.intent.getSerializableExtra("responses") as MutableList<MutableMap<String, Any>>
        responsesList = findViewById(R.id.listView)
        val names = responses.map {it["name"]} as List<String>
        responsesList.adapter = PronunciationResponseAdapter(this, names, responses)
        responsesList.onItemClickListener = this
    }

    private fun setRefresh(){
        swipeRefresh = findViewById(R.id.swiperefresh)
        swipeRefresh.setOnRefreshListener {
            swipeRefresh.isRefreshing = false
        }
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        var evaluation = responses[position]["feedback_files"]
        var fileData = responses[position]["audio_file"] as MutableMap<String, String>
        if (evaluation != null) {
            evaluation = evaluation as MutableMap<String, String>
            this.finish()
            val intent = Intent(this, Feedback::class.java)
            val extras = Bundle()
            extras.putString("student", fileData["student"])
            extras.putString("pitch", evaluation["pitch"])
            extras.putString("spectogram", evaluation["spectogram"])
            extras.putString("native_spectogram", evaluation["spectogram_native"])
            intent.putExtras(extras)
            this.startActivity(intent)
        }
    }

}