package com.example.speechtherapy.bundles.classesActivities.student

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.speechtherapy.R
import com.example.speechtherapy.bundles.adapters.HomeworkObjectAdapter
import com.example.speechtherapy.bundles.classesActivities.homework.ExerciseType
import com.example.speechtherapy.bundles.classesActivities.teacher.HomeworkAdapter
import com.example.speechtherapy.bundles.common.BaseActivity
import com.example.speechtherapy.bundles.common.helpers.DataListener.OnGetDataListenerQuery
import com.example.speechtherapy.bundles.services.HomeworkService.HomeworkService
import com.example.speechtherapy.bundles.model.dataModel.Homework
import com.google.firebase.firestore.QuerySnapshot
import java.util.*


class HomeworkListStudent: BaseActivity(), View.OnClickListener,
    AdapterView.OnItemSelectedListener {

    private val homeworkService: HomeworkService = HomeworkService()
    lateinit var adapter: HomeworkAdapter
    private lateinit var homeworkList: ListView
    var homeworks = mutableListOf<Homework>()
    var uids = mutableListOf<String>()
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var search: Button
    private lateinit var searchText: EditText
    private var displayType = ""
    private var userUID = user.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.teacher_homework_activity)
        initView()
        setRefresh()
    }

    private fun setRefresh(){
        swipeRefresh = findViewById(R.id.swiperefresh)
        swipeRefresh.setOnRefreshListener {
            swipeRefresh.isRefreshing = false
        }
    }


    fun initView(){
        search = findViewById(R.id.search)
        search.visibility = View.GONE
        searchText = findViewById(R.id.searchText)
        searchText.visibility = View.GONE
        setSpinner()
        homeworkList = findViewById(R.id.homeworkList)
        showHomeworkByStudent(userUID, "")
        homeworkList.setOnItemClickListener { _: AdapterView<*>, view1: View, position: Int, _: Long ->
            if(homeworks[position].status == "on_going") {
                val b = Bundle()
                b.putString("homeworkUID", uids[position])
                startActivityWithExtras(ExerciseType(), b)
            }
        }
    }

    private fun setSpinner(){
        val spinner: Spinner = findViewById(R.id.typeSpinner)
        ArrayAdapter.createFromResource(
            this,
            R.array.view_homeworks_types_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }
        spinner.onItemSelectedListener = this
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.search -> filterHomeworks()
        }
    }

    private fun filterHomeworks() {

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        displayType = parent?.getItemAtPosition(position) as String
        when(displayType){
            "On Going" -> showHomeworkByStudent(userUID, "on_going")
            "Delivered" -> showHomeworkByStudent(userUID, "delivered")
            "Delivered Late" -> showHomeworkByStudent(userUID, "delivered_late")
        }
    }

    private fun showHomeworkByStudent(query: String, status: String) {
        homeworkService.getHomeworkByStudentAndStatus(query, status, object : OnGetDataListenerQuery {
            override fun onSuccess(querySnapshot: QuerySnapshot?) {
                if (querySnapshot != null) {
                    if (querySnapshot.size() > 0) {
                        homeworks = HomeworkObjectAdapter().getHomeworkList(querySnapshot) as MutableList<Homework>
                        uids = homeworks.map { it.uid } as MutableList<String>
                        adapter = HomeworkAdapter(this@HomeworkListStudent, uids, homeworks)
                        homeworkList.adapter = HomeworkAdapter(
                            this@HomeworkListStudent,
                            uids,
                            homeworks
                        )
                        adapter.notifyDataSetChanged()
                    }
                    else{
                        Toast.makeText(this@HomeworkListStudent, "No result", Toast.LENGTH_SHORT).show()
                    }
                }
            }

        })
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }
}