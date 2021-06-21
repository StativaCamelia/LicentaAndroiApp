package com.example.speechtherapy.bundles.common

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.speechtherapy.R
import com.example.speechtherapy.bundles.AuthActivity
import com.example.speechtherapy.bundles.common.helpers.DataListener.OnGetDataListener
import com.example.speechtherapy.bundles.services.UserServices.Authentification
import com.example.speechtherapy.bundles.services.UserServices.UserServices
import com.example.speechtherapy.bundles.registerActivities.profile.ProfileActivityTeacher
import com.example.speechtherapy.bundles.registerActivities.profile.ProfileActivityStudent
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase


open class BaseActivity : AppCompatActivity() {

    val authService: Authentification = Authentification()
    private val userServices: UserServices = UserServices()
    var user: FirebaseUser = userServices.getCurrentUser()
    private lateinit var functions: FirebaseFunctions


    fun setScreenTitle(title: String) {
       setTitle(title)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#ef9a9a")))
        menuInflater.inflate(R.menu.profile_menu, menu)
        return super.onCreateOptionsMenu(menu)
        addMessage("Buna ziua")
    }

    private fun addMessage(text: String): com.google.android.gms.tasks.Task<String> {
        functions = Firebase.functions
        val data = hashMapOf(
            "text" to text,
            "push" to true
        )

        return functions
            .getHttpsCallable("addMessage")
            .call(data)
            .continueWith { task ->
                val result = task.result?.data as String
                println(result)
                result
            }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.logout -> {
                authService.logOut()
                finish()
                startNewActivity(AuthActivity())
            }
            R.id.profile -> {
                userServices.getUserById(user.uid, object : OnGetDataListener {
                    override fun onSuccess(documentSnapshot: DocumentSnapshot?) {
                        if(documentSnapshot?.data?.get("type") == "teacher"){
                            startNewActivity(ProfileActivityTeacher())
                        }
                        else{
                            startNewActivity(ProfileActivityStudent())
                        }

                        }
                })
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun auth(){
        if (authService.getUser() == null) {
            startNewActivity(AuthActivity())
        }
    }

    fun startNewActivity(activity: AppCompatActivity){
        val intent = Intent(applicationContext, activity::class.java)
        startActivity(intent)
    }

    fun startActivityWithExtras(activity: BaseActivity, extras: Bundle){
        val intent = Intent(applicationContext, activity::class.java)
        intent.putExtras(extras)
        startActivity(intent)
    }

}