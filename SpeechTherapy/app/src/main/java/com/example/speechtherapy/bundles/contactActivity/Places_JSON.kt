package com.example.speechtherapy.bundles.contactActivity

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


class Place_JSON {
    /**
     * Receives a JSONObject and returns a list
     */
    fun parse(jObject: JSONObject): List<HashMap<String, String>?> {
        var jPlaces: JSONArray? = null
        try {
            /** Retrieves all the elements in the 'places' array  */
            jPlaces = jObject.getJSONArray("results")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        /** Invoking getPlaces with the array of json object
         * where each json object represent a place
         */
        return getPlaces(jPlaces)
    }

    private fun getPlaces(jPlaces: JSONArray?): List<HashMap<String, String>?> {
        val placesCount = jPlaces!!.length()
        val placesList: MutableList<HashMap<String, String>?> = ArrayList()
        var place: HashMap<String, String>? = null
        /** Taking each place, parses and adds to list object  */
        for (i in 0 until placesCount) {
            try {
                /** Call getPlace with place JSON object to parse the place  */
                place = getPlace(jPlaces[i] as JSONObject)
                placesList.add(place)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        return placesList
    }

    /**
     * Parsing the Place JSON object
     */
    private fun getPlace(jPlace: JSONObject): HashMap<String, String> {
        val place = HashMap<String, String>()
        var placeName = "-NA-"
        var vicinity = "-NA-"
        var latitude = ""
        var longitude = ""
        var reference = ""
        try {
            // Extracting Place name, if available
            if (!jPlace.isNull("name")) {
                placeName = jPlace.getString("name")
            }

            // Extracting Place Vicinity, if available
            if (!jPlace.isNull("vicinity")) {
                vicinity = jPlace.getString("vicinity")
            }
            latitude = jPlace.getJSONObject("geometry").getJSONObject("location").getString("lat")
            longitude = jPlace.getJSONObject("geometry").getJSONObject("location").getString("lng")
            reference = jPlace.getString("reference")
            place["place_name"] = placeName
            place["vicinity"] = vicinity
            place["lat"] = latitude
            place["lng"] = longitude
            place["reference"] = reference
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return place
    }
}