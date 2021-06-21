package com.example.speechtherapy.bundles.informationActivity

import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.example.speechtherapy.R
import com.example.speechtherapy.bundles.common.BaseActivity


class ArpabetChart: BaseActivity(), View.OnClickListener {

    private val sounds = mutableListOf<String>(
        "IY",
        "UH",
        "UW",
        "UX",
        "AA",
        "AE",
        "AH",
        "AO",
        "AX",
        "AXR",
        "EH",
        "ER",
        "IH",
        "IX",
        "B",
        "CH",
        "D",
        "DH",
        "DX",
        "F",
        "G",
        "HH",
        "JH",
        "K",
        "L",
        "M",
        "N",
        "NG",
        "P",
        "Q",
        "R",
        "S",
        "SH",
        "T",
        "TH",
        "V",
        "W",
        "WH",
        "Y"
    )
    private var uri = "https://03ce3db4bff3.ngrok.io"
    val player = MediaPlayer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.arpabet_chart)
        setActions()
    }

    private fun setActions() {
        for (i in sounds)
        {
            println(i)
            val resID = resources.getIdentifier(i, "id", getPackageName())
            findViewById<Button>(resID).setOnClickListener(this)
        }
    }

    override fun onClick(v: View?) {
        val soundName = this.getResources().getResourceEntryName(v!!.getId());
        try {
            player.reset();
            player.setAudioStreamType(AudioManager.STREAM_MUSIC)
            player.setDataSource(
                "$uri/sounds/$soundName"
            )
            player.prepare()
            player.start()
        } catch (e: Exception) {
            println(e.message)
        }
    }
}