package com.example.speechtherapy.bundles.informationActivity

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import com.example.speechtherapy.R
import com.example.speechtherapy.bundles.common.BaseActivity


@Suppress("DEPRECATION")
class InformationActivity : BaseActivity(){

    private lateinit var listView: ListView
    private lateinit var listViewAdapter: ListViewModelAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.adult_activity_informations)
        initlizeViews()
        initView()
        initAction()
    }


    override fun onStart() {
        super.onStart()
        super.auth()
    }

    private fun initView() {
        listView = findViewById(R.id.listView)
        listViewAdapter = ListViewModelAdapter(this, InformationsData.getListViewModelList())
        listView.adapter = listViewAdapter
    }

    private fun initAction() {
        listView.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                onClickItem(position)
            }
    }

    private fun onClickItem(position: Int) {
        val bundle = Bundle()
        bundle.putInt("position", position)
        val fragment = InformationsDetailsFragment()
        fragment.arguments = bundle
        supportFragmentManager.beginTransaction().addToBackStack(null)
            .replace(R.id.frameLayout, fragment)
            .commit()
        listView.visibility = View.GONE
    }

    override fun onBackPressed() {
        val count = supportFragmentManager.backStackEntryCount
        if (count == 0) {
            listView.visibility = View.VISIBLE
            super.onBackPressed()
        } else {
            for (fragment in supportFragmentManager.fragments) {
                supportFragmentManager.beginTransaction().remove(fragment).commit()
            }
            listView.visibility = View.VISIBLE
            super.onBackPressed()
        }
    }

    private fun initlizeViews() {
        setScreenTitle("Informations")
    }

}