package com.example.speechtherapy.bundles.contactActivity

import android.os.AsyncTask
import com.google.android.gms.maps.GoogleMap
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


class PlacesTask(var myMap : GoogleMap) : AsyncTask<String?, Int?, String?>() {
    var data: String? = null


    override fun onPostExecute(result: String?) {
        val parserTask = ParserTask(myMap)
        parserTask.execute(result)
    }

    override fun doInBackground(vararg params: String?): String? {
        try {
            data = params[0]?.let { downloadUrl(it) }
        } catch (e: Exception) {
        }
        return data
    }

    @Throws(IOException::class)
    private fun downloadUrl(strUrl: String): String? {
        var data = ""
        var iStream: InputStream? = null
        var urlConnection: HttpURLConnection? = null
        try {
            val url = URL(strUrl)
            urlConnection = url.openConnection() as HttpURLConnection
            urlConnection.connect()
            iStream = urlConnection.getInputStream()
            val br = BufferedReader(InputStreamReader(iStream))
            val sb = StringBuffer()
            var line: String? = ""
            while (br.readLine().also { line = it } != null) {
                sb.append(line)
            }
            println(sb)
            data = sb.toString()
            br.close()
        } catch (e: java.lang.Exception) {
        } finally {
            iStream?.close()
            urlConnection?.disconnect()
        }
        return data
    }
}