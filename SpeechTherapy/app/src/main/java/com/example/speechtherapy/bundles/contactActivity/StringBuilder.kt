package com.example.speechtherapy.bundles.contactActivity

fun sbMethod(latitude: Double, longitude: Double): StringBuilder? {

    val sb = StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?")
    sb.append("location=$latitude,$longitude")
    sb.append("&radius=5000")
    sb.append("&types=" + "hospital")
    sb.append("&sensor=true")
    sb.append("&key=AIzaSyDgWiM33-vlh6j9xbJLxsYpl1OnellkecE")
    return sb
}