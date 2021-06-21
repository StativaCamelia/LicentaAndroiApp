package com.example.speechtherapy.bundles.classesActivities.teacher

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.speechtherapy.R
import com.example.speechtherapy.bundles.adapters.HomeworkObjectAdapter
import com.example.speechtherapy.bundles.classesActivities.homework.ExerciseType
import com.example.speechtherapy.bundles.common.BaseActivity
import com.example.speechtherapy.bundles.common.helpers.DataListener.OnGetDataListenerQuery
import com.example.speechtherapy.bundles.model.dataModel.Homework
import com.example.speechtherapy.bundles.services.HomeworkService.HomeworkService
import com.google.firebase.firestore.QuerySnapshot

class HomeworkActivityPerStudent : BaseActivity(), AdapterView.OnItemSelectedListener {

    private val homeworkService: HomeworkService = HomeworkService()
    lateinit var adapter: HomeworkAdapter
    private lateinit var homeworkList: ListView
    private lateinit var swipeRefresh: SwipeRefreshLayout
    var homeworks = mutableListOf<Homework>()
    var uids = mutableListOf<String>()
    private var studentUID = ""
    private var displayType = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.teacher_homework_per_student)
        setRefresh()
        studentUID = this.intent.getStringExtra("studentUID").toString()
        initView()
    }


    private fun setRefresh(){
        swipeRefresh = findViewById(R.id.swiperefresh)
        swipeRefresh.setOnRefreshListener {
            swipeRefresh.isRefreshing = false
        }
    }

    fun initView(){
        setSpinner()
        homeworkList = findViewById(R.id.homeworkList)
        showHomeworkByStudent(studentUID, "")
        registerForContextMenu(homeworkList)
        homeworkList.setOnItemClickListener { _: AdapterView<*>, view1: View, position: Int, _: Long ->
            if(homeworks[position].status == "delivered" || homeworks[position].status == "delivered_late"){
                val b = Bundle()
                b.putString("homeworkUID", homeworks[position].uid)
                startActivityWithExtras(ExerciseType(), b)
            }
        }
    }

    private fun showHomeworkByStudent(query: String, status: String) {
        homeworkService.getHomeworkByStudentAndStatus(query, status, object : OnGetDataListenerQuery {
            override fun onSuccess(querySnapshot: QuerySnapshot?) {
                if (querySnapshot != null) {
                    if (querySnapshot.size() > 0) {
                        homeworks = HomeworkObjectAdapter().getHomeworkList(querySnapshot) as MutableList<Homework>
                        uids = homeworks.map { it.uid } as MutableList<String>
                        adapter = HomeworkAdapter(this@HomeworkActivityPerStudent, uids, homeworks)
                        homeworkList.adapter = HomeworkAdapter(
                            this@HomeworkActivityPerStudent,
                            uids,
                            homeworks
                        )
                        adapter.notifyDataSetChanged()
                    }
                    else{
                        Toast.makeText(this@HomeworkActivityPerStudent, "No result", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
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

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        displayType = parent?.getItemAtPosition(position) as String
        when(displayType){
            "On Going" -> showHomeworkByStudent(studentUID, "on_going")
            "Delivered" -> showHomeworkByStudent(studentUID, "delivered")
            "Delivered Late" -> showHomeworkByStudent(studentUID, "delivered_late")
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }
}
