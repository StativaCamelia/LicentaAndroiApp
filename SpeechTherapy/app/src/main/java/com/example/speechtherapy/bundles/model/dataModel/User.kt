package com.example.speechtherapy.bundles.model.dataModel

class User {

    private var photo = ""
    var type = ""
    var email = ""

    fun User(photo: String, type: String) {
        this.photo = photo
        this.type = type
    }


}
