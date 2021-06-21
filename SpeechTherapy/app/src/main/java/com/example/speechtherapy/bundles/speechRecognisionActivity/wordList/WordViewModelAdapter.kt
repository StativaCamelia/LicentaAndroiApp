package com.example.speechtherapy.bundles.speechRecognisionActivity.wordList

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.example.speechtherapy.R
import com.example.speechtherapy.bundles.common.helpers.GlideApp
import com.google.firebase.storage.FirebaseStorage

class WordViewModelAdapter(val context: Context, private val listModelArrayList: ArrayList<WordViewModel>, private val type: String) : BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        val view: View?
        val vh: ViewHolder

        if (convertView == null) {
            val layoutInflater = LayoutInflater.from(context)
            view = layoutInflater.inflate(R.layout.child_word_list_item_layout, parent, false)
            vh = ViewHolder(view)
            view.tag = vh
        } else {
            view = convertView
            vh = view.tag as ViewHolder
        }
        vh.title.text = listModelArrayList[position].title
        if(type != "adult_pronunciation")
            setImage(position, vh.image)
        return view
    }

    private fun setImage(position: Int, imagePlace: ImageView ){
        val storage = FirebaseStorage.getInstance()
        val gsReference = listModelArrayList[position].image?.let { storage.reference.child(it) }
        context.let {
            GlideApp.with(it)
                .load(gsReference)
                .into(imagePlace)
        }
    }

    override fun getItem(position: Int): Any {
        return listModelArrayList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return listModelArrayList.size
    }
}

private class ViewHolder(view: View?) {
    val title: TextView = view?.findViewById(R.id.titleItem) as TextView
    val card: CardView = view?.findViewById(R.id.cardInfo) as CardView
    val image: ImageView = view?.findViewById(R.id.articleImage) as ImageView
}