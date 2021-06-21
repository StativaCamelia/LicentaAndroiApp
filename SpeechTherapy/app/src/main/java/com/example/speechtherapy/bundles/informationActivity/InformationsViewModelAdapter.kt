package com.example.speechtherapy.bundles.informationActivity

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

class ListViewModelAdapter(val context: Context, private val listModelArrayList: ArrayList<InformationsViewModel>) : BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        val view: View?
        val vh: ViewHolder

        if (convertView == null) {
            val layoutInflater = LayoutInflater.from(context)
            view = layoutInflater.inflate(R.layout.adult_info_list_item_layout, parent, false)
            vh = ViewHolder(view)
            view.tag = vh
        } else {
            view = convertView
            vh = view.tag as ViewHolder
        }
        if(position%2 == 0)
            vh.tvCard.setCardBackgroundColor(Color.parseColor("#cdece7"))
        else
            vh.tvCard.setCardBackgroundColor(Color.parseColor("#ef9a9a"))
        vh.tvTitle.text = listModelArrayList[position].title
        vh.tvImage.setImageResource(listModelArrayList[position].image)
        return view
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
    val tvTitle: TextView = view?.findViewById(R.id.titleItem) as TextView
    val tvCard: CardView = view?.findViewById(R.id.cardInfo) as CardView
    val tvImage: ImageView = view?.findViewById(R.id.articleImage) as ImageView
}