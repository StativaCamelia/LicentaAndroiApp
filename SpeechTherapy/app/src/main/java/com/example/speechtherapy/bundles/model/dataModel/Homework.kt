package com.example.speechtherapy.bundles.model.dataModel

import java.text.SimpleDateFormat
import java.util.*

class Homework {

    var uid = ""
    var isClass = true
    var homeworkName = ""
    var className = ""
    var exercises  = mutableListOf<Exercise>()
    var teacher = ""
    var students = mutableListOf<String>()
    var createdAt =  SimpleDateFormat("dd/M/yyyy hh:mm:ss").format(Date())
    var deadline = ""
    var status = "on_going"
}