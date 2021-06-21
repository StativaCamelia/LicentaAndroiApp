package com.example.speechtherapy.bundles.services.UserServices

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class Authentification {
    var auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun getUser(): FirebaseUser? {
        return auth.currentUser
    }

    fun logOut(){
        auth.signOut()
    }

}