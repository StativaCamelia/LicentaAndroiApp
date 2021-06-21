package com.example.speechtherapy.bundles.common.helpers

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.view.ContextThemeWrapper
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.example.speechtherapy.R
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.*

class DateUtils {

    private var year: Int = 0
    private var day: Int = 0
    private var month: Int = 0
    private var hour: Int = 0
    private var minute: Int = 0
    private var date: String = ""
    private var hourAndMinute: String = ""
    private var time: String = "$date $hourAndMinute"

    fun getDeadline(): String{
        return time
    }

    @SuppressLint("SetTextI18n")
    fun getDate(context: Context, view: View){
        val buttonDate = view.findViewById<Button>(R.id.selectDate)
        val inputDate = view.findViewById<EditText>(R.id.inputDate)
        buttonDate.setOnClickListener {
            val c: Calendar = Calendar.getInstance()
            year = c.get(Calendar.YEAR)
            month = c.get(Calendar.MONTH)
            day = c.get(Calendar.DAY_OF_MONTH)
            val datePickerDialog = DatePickerDialog(
                context,
                { _, year, monthOfYear, dayOfMonth ->
                    inputDate.setText(dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year.toString() + " ")
                    date = dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year.toString()
                    time = "$date $hourAndMinute"
                }, year, month, day
            )
            datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
            datePickerDialog.show()
        }
    }

    @SuppressLint("SetTextI18n")
    fun getTime(context: Context, errorView: TextView, view: View){
        val buttonTime = view.findViewById<Button>(R.id.selectTime)
        val inputTime = view.findViewById<EditText>(R.id.inputTime)
        val c = Calendar.getInstance()
        hour = c[Calendar.HOUR_OF_DAY]
        minute = c[Calendar.MINUTE]
        buttonTime.setOnClickListener {
            val timePickerDialog = TimePickerDialog(
                context,
                { _, hourOfDay, minute ->
                    val datetime = Calendar.getInstance()
                    datetime.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    datetime.set(Calendar.MINUTE, minute)
                    hourAndMinute = "$hourOfDay:$minute"
                    if (DateUtils().verifyDate("$date $hourAndMinute")) {
                        inputTime.setText("$hourOfDay:$minute")
                        errorView.visibility = View.INVISIBLE
                        time = "$date $hourAndMinute"
                    } else {
                        inputTime.setText("")
                        errorView.visibility = View.VISIBLE
                    }
                },
                hour,
                minute,
                false
            )
            timePickerDialog.show()
        }
    }

    fun verifyDate(date: String): Boolean{
        val knownPatterns: MutableList<DateTimeFormatter> = ArrayList<DateTimeFormatter>()
        knownPatterns.add(DateTimeFormatter.ofPattern("dd/M/yyyy HH:mm"))
        knownPatterns.add(DateTimeFormatter.ofPattern("dd/M/yyyy H:mm"))
        knownPatterns.add(DateTimeFormatter.ofPattern("dd/MM/yyyy H:mm"))
        knownPatterns.add(DateTimeFormatter.ofPattern("dd/M/yyyy HH:m"))
        knownPatterns.add(DateTimeFormatter.ofPattern("d/M/yyyy HH:mm"))
        knownPatterns.add(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
        var localDate: LocalDateTime
        for (pattern in knownPatterns) {
            try {
                localDate = LocalDateTime.parse(date, pattern)
                val timeInMilliseconds: Long =
                    localDate.atOffset(ZoneOffset.UTC).toInstant().toEpochMilli()
                val c = Calendar.getInstance()
                if(timeInMilliseconds > c.timeInMillis){
                    return true
                }
                return false
            } catch (pe: DateTimeParseException) {
                continue
            }
        }
        return false
    }

}