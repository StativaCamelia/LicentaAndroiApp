package com.example.speechtherapy.bundles.interfaces.teacher

import android.widget.ListView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.speechtherapy.R
import com.example.speechtherapy.bundles.common.BaseActivity
import com.example.speechtherapy.bundles.services.ResponseService.ResponseService

open class HomeworkListTeacher: BaseActivity() {

     lateinit var homework : ListView
     lateinit var studentUID: String
     var correctAnswers: MutableList<String> = mutableListOf()
     var answers: ArrayList<String> = arrayListOf()
     lateinit var homeworkUID: String
     lateinit var userUID: String
      val responseService: ResponseService = ResponseService()
      lateinit var swipeRefresh: SwipeRefreshLayout
      lateinit var exerciseType: String


     fun setRefresh(){
          swipeRefresh = findViewById(R.id.swiperefresh)
          swipeRefresh.setOnRefreshListener {
               swipeRefresh.isRefreshing = false
          }
     }

}