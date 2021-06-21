package com.example.speechtherapy.bundles.registerActivities

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import com.example.speechtherapy.R
import com.example.speechtherapy.bundles.common.BaseActivity
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

class LanguageActivity: BaseActivity(), AdapterView.OnItemSelectedListener, View.OnClickListener {

    private lateinit var start: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_language_select)
        start = findViewById(R.id.start)
        start.setOnClickListener(this)
        setSpinner()
    }

    private fun setSpinner(){
        val spinner: Spinner = findViewById(R.id.spinner)
        ArrayAdapter.createFromResource(
            this,
            R.array.languages_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }
        spinner.onItemSelectedListener = this
    }

    @SuppressLint("CommitPrefEdits")
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val language = parent?.getItemAtPosition(position) as String
        val editor = this.getSharedPreferences("prefs", 0).edit()
        editor.putString("language", language)
        val m: Matcher = Pattern.compile("\\(([^)]+)\\)").matcher(language)
        var languageCode = ""
        while (m.find()) {
            languageCode = m.group(1)
        }
        editor.putString("languageCode", languageCode)
        editor.apply()
        val resources: Resources = resources
        val dm: DisplayMetrics = resources.displayMetrics
        val config: Configuration = resources.configuration
        config.setLocale(Locale(languageCode.toLowerCase(Locale.ROOT)))
        resources.updateConfiguration(config, dm)
        }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.start -> startNewActivity(SelectUserType())
        }
    }

}