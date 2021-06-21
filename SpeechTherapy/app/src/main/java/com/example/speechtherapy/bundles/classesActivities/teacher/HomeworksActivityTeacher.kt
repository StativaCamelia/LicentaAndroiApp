package com.example.speechtherapy.bundles.classesActivities.teacher

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.*
import android.widget.AdapterView.AdapterContextMenuInfo
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.speechtherapy.R
import com.example.speechtherapy.bundles.adapters.HomeworkObjectAdapter
import com.example.speechtherapy.bundles.common.BaseActivity
import com.example.speechtherapy.bundles.common.helpers.DataListener.OnGetDataListenerQuery
import com.example.speechtherapy.bundles.common.helpers.DateUtils
import com.example.speechtherapy.bundles.model.dataModel.Homework
import com.example.speechtherapy.bundles.services.HomeworkService.HomeworkService
import com.google.firebase.firestore.QuerySnapshot
import java.util.*


class HomeworksActivityTeacher: BaseActivity(), View.OnClickListener,
    AdapterView.OnItemSelectedListener {

    private val homeworkService: HomeworkService = HomeworkService()
    lateinit var adapter: HomeworkAdapter
    private lateinit var homeworkList: ListView
    private var homeworkUid = ""
    var homeworks = mutableListOf<Homework>()
    var uids = mutableListOf<String>()
    private lateinit var errorTime: TextView
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var search: Button
    private lateinit var searchText: EditText
    private var displayType = ""
    private val dateUtils =  DateUtils()

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

    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menuInflater.inflate(R.menu.homework_menu, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val info = item.menuInfo as AdapterContextMenuInfo
        val uid = uids[info.position]
        when(item.itemId){
            R.id.homeworkRemove -> removeHomework(uid)
            R.id.homeworkEdit -> editHomework(uid)
        }
        return super.onContextItemSelected(item)
    }

    private fun editHomework(uid: String) {
        val dialog = AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.teacher_homework_edit, null)
        dialog.setView(view)
        errorTime = view.findViewById(R.id.errorTime)
        dateUtils.getDate(this, view)
        dateUtils.getTime(this, errorTime, view)
        dialog.setPositiveButton("Update") { _: DialogInterface, _: Int ->
            updateHomework(uid)
        }

        dialog.setNegativeButton("Cancel") { dialogInterface: DialogInterface, _: Int -> dialogInterface.cancel() }
        dialog.create()
        dialog.show()
    }

    private fun updateHomework(uid: String) {
        val postValues = mapOf("deadline" to dateUtils.getDeadline())
        homeworkService.updateHomework(uid, postValues)
        adapter.notifyDataSetChanged()
    }


    private fun removeHomework(uid: String) {
        homeworkService.deleteHomework(uid)
        adapter.notifyDataSetChanged()
    }

    fun initView(){
        search = findViewById(R.id.search)
        search.setOnClickListener(this)
        searchText = findViewById(R.id.searchText)
        setSpinner()
        homeworkList = findViewById(R.id.homeworkList)
        showHomeworksByClasses()
        registerForContextMenu(homeworkList)
        homeworkList.setOnItemClickListener { _: AdapterView<*>, _: View, i: Int, _: Long ->
                homeworkList.showContextMenu()
                homeworkUid = homeworks[i].className
        }
    }

    private fun setSpinner(){
        val spinner: Spinner = findViewById(R.id.typeSpinner)
        ArrayAdapter.createFromResource(
            this,
            R.array.view_homeworks_array,
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
        val query = searchText.text.toString()
        when(displayType){
            "Clase" -> showHomeworksByClass(query)
            "Studenti" -> showHomeworksByStudent(query)
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        displayType = parent?.getItemAtPosition(position) as String
        when(displayType){
            "Clase" -> showHomeworksByClasses()
            "Studenti" -> showHomeworksByStudents()
        }
    }

    private fun setHomework(querySnapshot: QuerySnapshot?){
        if (querySnapshot!!.size() > 0) {
            homeworks = HomeworkObjectAdapter().getHomeworkList(querySnapshot) as MutableList<Homework>
            uids = homeworks.map { it.uid } as MutableList<String>
            adapter = HomeworkAdapter(this@HomeworksActivityTeacher, uids, homeworks)
            homeworkList.adapter = HomeworkAdapter(
                this@HomeworksActivityTeacher,
                uids,
                homeworks
            )
            adapter.notifyDataSetChanged()
        }
        else{
            Toast.makeText(this@HomeworksActivityTeacher, "No result", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showHomeworksByStudent(query: String){
        homeworkService.getHomeworkByStudent(query, user.uid, object : OnGetDataListenerQuery {
            override fun onSuccess(querySnapshot: QuerySnapshot?) {
                    setHomework(querySnapshot)
                }
        })
    }

    private fun showHomeworksByStudents() {
        homeworkService.getHomeworksByStudents(user.uid, object : OnGetDataListenerQuery {
            override fun onSuccess(querySnapshot: QuerySnapshot?) {
                    setHomework(querySnapshot)
                }
        })
    }

    private fun showHomeworksByClasses() {
        homeworkService.getHomeworksByClasses(user.uid, object : OnGetDataListenerQuery {
            override fun onSuccess(querySnapshot: QuerySnapshot?) {
                    setHomework(querySnapshot)
            }

        })
    }

    private fun showHomeworksByClass(query: String) {
        homeworkService.getHomeworksByClass(query, user.uid, object : OnGetDataListenerQuery {
            override fun onSuccess(querySnapshot: QuerySnapshot?) {
                    setHomework(querySnapshot)
            }

        })
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }
}