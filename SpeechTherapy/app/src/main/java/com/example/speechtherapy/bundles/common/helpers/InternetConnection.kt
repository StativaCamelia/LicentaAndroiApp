package com.example.speechtherapy.bundles.common.helpers

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.ConnectivityManager
import com.example.speechtherapy.R


class InternetConnection(
    val context: Context,
    val activity: Activity,
) {

    fun checkConnection():Boolean {
        val conMgr = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = conMgr.activeNetworkInfo
        if (netInfo == null || !netInfo.isConnected || !netInfo.isAvailable) {
            return false
        }
        return true
    }

    private val refreshButton = { _: DialogInterface, _: Int ->
        activity.finish()
        val i = Intent(context, activity.javaClass)
        context.startActivity(i)
    }

    fun showDialog() {
        val dialogBuilder = AlertDialog.Builder(context).create()
        dialogBuilder.setMessage("Connect to the internet and try again")
        dialogBuilder.setTitle("You are not connected to the internet!")
        dialogBuilder.setIcon(R.drawable.error)
        dialogBuilder.setButton(
            "Refresh",
            DialogInterface.OnClickListener(function = refreshButton)
        )
        dialogBuilder.show()
    }

}