package com.example.speechtherapy.bundles.informationActivity

import android.annotation.SuppressLint
import android.content.res.Resources
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.speechtherapy.R
import java.io.InputStream
import java.util.*


class InformationsDetailsFragment : Fragment(), View.OnClickListener {

    private val list = InformationsData.getListViewModelList<ArrayList<InformationsViewModel>>()
    private var position: Int = 0
    private lateinit var image: ImageView
    lateinit var title: TextView
    private lateinit var author: TextView
    lateinit var content: TextView
    lateinit var next: Button
    private lateinit var back: Button

    companion object;

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val fragment = inflater.inflate(R.layout.adult_fragment_informations_details, container, false)
        initView(fragment)
        return fragment
    }

    fun initView(fragment: View) {
        position = arguments?.getInt("position")!!
        image = fragment.findViewById(R.id.details_image)!!
        title = fragment.findViewById(R.id.text_title)
        author = fragment.findViewById(R.id.details_author)
        content = fragment.findViewById(R.id.details_content)
        next = fragment.findViewById(R.id.details_next)
        back = fragment.findViewById(R.id.details_back)

        val imageResource = list[position].image
        val textResource = list[position].title
        val authorResource = list[position].author
        val contentResource = list[position].content
        image.setImageResource(imageResource)
        title.text = textResource
        author.text = authorResource

        if (contentResource != null) {
            setArticleContent(contentResource)
        }
        movingLogic(position)
    }

    private fun movingLogic(position: Int) {
        when (position) {
            0 -> {
                back.visibility = View.GONE
            }
            list.size - 1 -> {
                next.visibility = View.GONE
            }
        }
        next.setOnClickListener(this)
        back.setOnClickListener(this)
    }

    @SuppressLint("SetTextI18n")
    private fun setArticleContent(rawResource: Int) {
        try {
            val res: Resources = resources
            val inStream: InputStream = res.openRawResource(rawResource)
            val articleContent = ByteArray(inStream.available())
            inStream.read(articleContent)
            content.text = Html.fromHtml(String(articleContent))
        } catch (e: Exception) {
            content.text = "Error: can't show article."
        }
    }

    private fun changeFragment(newPosition: Int) {
        val bundle = Bundle()
        bundle.putInt("position", newPosition)
        val nextFragment = InformationsDetailsFragment()
        nextFragment.arguments = bundle
        parentFragmentManager.beginTransaction().replace(R.id.frameLayout, nextFragment)
            .commit()
    }

    override fun onClick(view: View?) {
        if (view != null) {
            when (view.id) {
                R.id.details_back -> {
                    changeFragment(position - 1)
                }
                R.id.details_next -> {
                    changeFragment(position + 1)
                }
            }
        }
    }


}