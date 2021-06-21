package com.example.speechtherapy.bundles.classesActivities.teacher

import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Point
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.speechtherapy.R
import com.example.speechtherapy.bundles.common.BaseActivity
import com.example.speechtherapy.bundles.common.helpers.DataListener.OnCreateData
import com.example.speechtherapy.bundles.common.helpers.DataListener.OnGetDataListener
import com.example.speechtherapy.bundles.common.helpers.DataListener.OnGetDataListenerQuery
import com.example.speechtherapy.bundles.services.ClassService.ClassService
import com.example.speechtherapy.bundles.services.ClassService.ConflictException
import com.example.speechtherapy.bundles.services.UserServices.UserServices
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.zxing.WriterException


class StudentsActivityTeacher: BaseActivity(), View.OnClickListener {

    private val classService: ClassService = ClassService()
    private val userService: UserServices = UserServices()
    private lateinit var addStudent: Button
    private lateinit var studentsList: ListView
    private lateinit var noStudent: TextView
    private lateinit var classUID: String
    private var qrCode: ImageView? = null
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private var uids = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.teacher_activity_students)
        classUID = this.intent.getStringExtra("classId").toString()
        initViews()
        getStudents()
        setRefresh()
    }

    private fun setRefresh(){
        swipeRefresh = findViewById(R.id.swiperefresh)
        swipeRefresh.setOnRefreshListener {
            swipeRefresh.isRefreshing = false
            refresh()
        }
    }

    fun refresh(){
        finish()
        val b = Bundle()
        b.putString("classId", classUID)
        startActivityWithExtras(this@StudentsActivityTeacher, b)
    }

    private fun setLongClickItemListener() {
        studentsList.setOnItemLongClickListener { _, _, position, _ ->
            val studentUID: String = studentsList.getItemAtPosition(position).toString()
            val alertD = AlertDialog.Builder(this)
            alertD.setTitle("Remove student")
            alertD.setMessage("Do you want to remove this student from the class?")
            alertD.setNegativeButton("No") { dialogInterface: DialogInterface, _: Int ->
                dialogInterface.cancel()
            }
            alertD.setPositiveButton("Yes") { _: DialogInterface, _: Int ->
                classService.deleteStudentFromClass(classUID, studentUID)
                Toast.makeText(this, "Student removed", Toast.LENGTH_LONG).show()
            }
            alertD.create()
            alertD.show()
            return@setOnItemLongClickListener true
        }
    }


    private fun initViews(){
        qrCode = findViewById(R.id.qrCode)
        noStudent = findViewById(R.id.noStudent)
        noStudent.visibility = View.INVISIBLE
        addStudent = findViewById(R.id.addStudent)
        addStudent.setOnClickListener(this)
        studentsList = findViewById(R.id.studentsList)
        studentsList.setOnItemClickListener { adapterView: AdapterView<*>, view1: View, i: Int, l: Long ->
            val bundle = Bundle()
            println(uids[i])
            bundle.putString("studentUID", uids[i])
            startActivityWithExtras(HomeworkActivityPerStudent(), bundle)}
        setLongClickItemListener()
    }

    private fun getStudents(){
        classService.getStudentsByClass(classUID, object : OnGetDataListener {
            override fun onSuccess(documentSnapshot: DocumentSnapshot?) {
                if (documentSnapshot?.data?.get("students") != null) {
                    val students = documentSnapshot.data?.get("students") as List<*>
                    if (students.isNotEmpty()) {
                        uids = mutableListOf<String>()
                        var index = 0
                        for (student in students) {
                            uids.add(index, student.toString())
                            index += 1
                        }
                        studentsList.adapter = StudentAdapter(this@StudentsActivityTeacher, uids)
                    } else {
                        noStudent.visibility = View.VISIBLE
                    }
                }
            }
        })
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.addStudent -> addStudent()
        }
    }

    fun onClick(dialog: DialogInterface) {
        dialog.dismiss()
    }

    private fun addStudent() {
        val dialogAlert = AlertDialog.Builder(this)
        val viewLayout = layoutInflater.inflate(R.layout.dialog_add_student, null)
        dialogAlert.setView(viewLayout)
        dialogAlert.setCancelable(false)
        dialogAlert.setNegativeButton("Cancel") { dialogInterface: DialogInterface, _: Int ->
            dialogInterface.dismiss()
        }
        val addButton = viewLayout.findViewById<Button>(R.id.studentAdd)
        addButton.setOnClickListener {
            val emailView = viewLayout.findViewById<EditText>(R.id.studentEmail)
            val email = emailView.text.toString().trim()
            val errorText = viewLayout.findViewById<TextView>(R.id.errorText)
            addStudentToClass(email, errorText)
        }
        val qrCodeContainer = viewLayout.findViewById<ImageView>(R.id.qrCode)
        generateQr(qrCodeContainer)
        dialogAlert.create()
        dialogAlert.show()
    }


    private fun addStudentToClass(email: String, error: TextView){
        userService.getUserByEmail(email, object : OnGetDataListenerQuery {
            override fun onSuccess(querySnapshot: QuerySnapshot?) {
                if (querySnapshot?.documents?.size == 0) {
                    error.text = resources.getString(R.string.InvalidStudentEmail)
                    error.visibility = View.VISIBLE
                } else {
                    error.visibility = View.INVISIBLE
                    val id = querySnapshot?.documents?.get(0)?.id
                    if (id != null) {
                        classService.addStudentToClass(classUID, id, object : OnCreateData {
                            override fun onSuccess() {
                                Toast.makeText(this@StudentsActivityTeacher, "Student successfully added", Toast.LENGTH_LONG).show()
                                refresh()
                            }

                            override fun onFailure(e: ConflictException) {
                                error.text = resources.getString(R.string.StudentAlreadyInClass)
                                error.visibility = View.VISIBLE
                            }
                        })
                    }
                }
            }
        })
    }



    private fun generateQr(qrCode: ImageView){
        val manager = getSystemService(WINDOW_SERVICE) as WindowManager
        val display = manager.defaultDisplay
        val point = Point()
        display.getRealSize(point)
        val width: Int = point.x
        val height: Int = point.y
        var dimen = if (width < height) width else height
        dimen = dimen * 3 / 4
        val qrgEncoder = QRGEncoder(classUID, null, QRGContents.Type.TEXT, dimen)
        try {
            val bitmap = qrgEncoder.encodeAsBitmap()
            qrCode.setImageBitmap(bitmap)
        } catch (e: WriterException) {
            println(e.toString())
        }
    }


}