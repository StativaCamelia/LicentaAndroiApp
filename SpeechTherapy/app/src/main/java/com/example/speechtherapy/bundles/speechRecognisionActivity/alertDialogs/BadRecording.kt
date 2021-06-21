package com.example.speechtherapy.bundles.speechRecognisionActivity.alertDialogs

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import com.example.speechtherapy.R

class BadRecording(val context: Context, val activity: Activity) {

    private val nextButton = { _: DialogInterface, _: Int ->
        val position = activity.intent.extras?.getInt("item")
        activity.finish()
        val intent = activity.intent
        val b = Bundle()
        b.putInt("item", position!! + 1)
        intent.putExtras(b)
        activity.startActivity(activity.intent)
    }

    fun showDialog() {
        val dialogBuilder = AlertDialog.Builder(context).create()
        dialogBuilder.setMessage("The recording was a little bit noisy!")
        dialogBuilder.setTitle("Please try again!")
        dialogBuilder.setIcon(R.drawable.exercise_mistake)
        dialogBuilder.setButton(
            AlertDialog.BUTTON_POSITIVE, "Try Again"
        ) { dialog, _ -> dialog.dismiss() }
        dialogBuilder.setButton(
            AlertDialog.BUTTON_NEUTRAL,
            "Next",
            DialogInterface.OnClickListener(function = nextButton)
        )
        dialogBuilder.show()
    }
}