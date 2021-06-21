package com.example.speechtherapy.bundles.common.helpers

import android.app.AlertDialog
import android.content.Context
import androidx.fragment.app.FragmentActivity
import com.example.speechtherapy.R

class ServerError(
    val context: Context?,
    val activity: FragmentActivity?,
) {

    fun showDialog() {
        val dialogBuilder = AlertDialog.Builder(context).create()
        dialogBuilder.setMessage("Can't connect to the server")
        dialogBuilder.setTitle("Try again")
        dialogBuilder.setIcon(R.drawable.error)
        dialogBuilder.setButton(AlertDialog.BUTTON_POSITIVE, "Try Again"
        ) { dialog, _ -> dialog.dismiss() }
        dialogBuilder.show()
    }
}