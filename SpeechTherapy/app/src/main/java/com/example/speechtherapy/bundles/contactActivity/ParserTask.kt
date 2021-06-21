package com.example.speechtherapy.bundles.contactActivity

import android.os.AsyncTask
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import org.json.JSONObject


public class ParserTask(var myMap : GoogleMap) :
    AsyncTask<String?, Int?, List<HashMap<String, String>?>?>() {
    var jObject: JSONObject? = null

    override fun onPostExecute(list: List<HashMap<String, String>?>?) {
        myMap.clear()
        if (list != null) {
            for (i in list.indices) {
                val markerOptions = MarkerOptions()
                val hmPlace = list[i]
                val lat = hmPlace!!["lat"]!!.toDouble()
                val lng = hmPlace["lng"]!!.toDouble()

                val name = hmPlace["place_name"]
                val vicinity = hmPlace["vicinity"]
                val latLng = LatLng(lat, lng)

                markerOptions.position(latLng)
                markerOptions.title("$name : $vicinity")
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE))

                myMap.addMarker(markerOptions)
            }
        }
    }

    override fun doInBackground(vararg params: String?): List<HashMap<String, String>?>? {
        var places: List<HashMap<String, String>?>? = null
        val placeJson = Place_JSON()
        try {
            jObject = JSONObject(params[0])
            places = placeJson.parse(jObject!!)
        } catch (e: Exception) {
            println(e.toString())
        }
        return places
    }
}