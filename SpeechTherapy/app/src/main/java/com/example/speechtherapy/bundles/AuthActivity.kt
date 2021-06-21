@file:Suppress("DEPRECATION")

package com.example.speechtherapy.bundles

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.speechtherapy.R
import com.example.speechtherapy.bundles.services.UserServices.Authentification
import com.example.speechtherapy.bundles.services.UserServices.UserServices
import com.example.speechtherapy.bundles.registerActivities.LanguageActivity
import com.example.speechtherapy.bundles.registerActivities.SelectCategory
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceId


class AuthActivity : AppCompatActivity() {

    private val authService: Authentification = Authentification()
    var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val RC_SIGN_IN = 123
    private val userService: UserServices = UserServices()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (authService.getUser() != null) {
            sendToSelectCategory()
            finish()
        } else {
            startActivityForResult(
                AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(
                        listOf(
                            AuthUI.IdpConfig.EmailBuilder().build(),
                            AuthUI.IdpConfig.GoogleBuilder().build(),
                        )
                    )
                    .setTheme(R.style.LoginTheme)
                    .setIsSmartLockEnabled(false)
                    .setLogo(R.drawable.logo)
                    .build(),
                RC_SIGN_IN
            )
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK) {
                if (response != null) {
                    if(response.isNewUser) {
                        startActivity(Intent(this, LanguageActivity::class.java))
                        val user = authService.getUser()
                        FirebaseInstanceId.getInstance().instanceId
                            .addOnCompleteListener(OnCompleteListener { task ->
                                if (!task.isSuccessful) {
                                    return@OnCompleteListener
                                }
                                val token = task.result?.token
                                userService.updateUser(user?.uid, mapOf("email" to user?.email, "token" to token))
                            })

                    }
                    else{
                        startActivity(Intent(this, SelectCategory::class.java))
                    }
                }
                finish()
                return
            } else {
                if (response == null) {
                    Log.e("Login", "Login canceled by User")
                    return
                }
                if (response.error!!.errorCode == ErrorCodes.NO_NETWORK) {
                    Log.e("Login", "No Internet Connection")
                    return
                }
                if (response.error!!.errorCode == ErrorCodes.UNKNOWN_ERROR) {
                    Log.e("Login", "Unknown Error")
                    return
                }
            }
            Log.e("Login", "Unknown sign in response")
        }
    }

    private fun sendToSelectCategory() {
        val intentCategory = Intent(applicationContext, SelectCategory::class.java)
        startActivity(intentCategory)
    }
}