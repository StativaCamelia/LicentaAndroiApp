package com.example.speechtherapy.bundles.registerActivities.profile

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.net.Uri
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.speechtherapy.R
import com.example.speechtherapy.bundles.common.BaseActivity
import com.example.speechtherapy.bundles.common.helpers.DataListener.OnGetDataListener
import com.example.speechtherapy.bundles.common.helpers.GlideApp
import com.example.speechtherapy.bundles.services.UserServices.UserServices
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

open class BaseProfileActivity: BaseActivity(){

    private var filePath: Uri? = null
    private val userServices: UserServices = UserServices()
    private var storage: FirebaseStorage =  FirebaseStorage.getInstance()
    private var storageReference: StorageReference = storage.reference
    lateinit var  profilePhoto: ImageView
    open lateinit var userType: String

    fun setUserData(email: TextView, userTypeContainer: TextView, profilePhotoContainer: ImageView){
        email.text = user.email
        userServices.getUserById(user.uid, object : OnGetDataListener {
            override fun onSuccess(documentSnapshot: DocumentSnapshot?) {
                userType = documentSnapshot?.data?.get("type").toString()
                setPhoto(documentSnapshot?.data?.get("photo").toString(), profilePhotoContainer)
                setUserType(documentSnapshot?.data?.get("type").toString(), userTypeContainer)
            }
        })
    }

    fun setUserType(type: String?, userTypeContainer: TextView){
        userTypeContainer.text = type
    }

    override fun setTitle(title: CharSequence?) {
        super.setTitle("Profile")
    }

    fun setPhoto(resource: String?, profilePhotoContainer: ImageView){
        profilePhoto = profilePhotoContainer
        if(!resource.equals("null")){
            val gsReference = resource?.let { storageReference.child(it) }
            applicationContext.let {
                GlideApp.with(it)
                    .load(gsReference)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(profilePhotoContainer)
            }
        }
        else{
            profilePhotoContainer.setImageResource(R.drawable.avatar1)
        }
    }

    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        when(v?.id){
            R.id.profilePhoto -> menuInflater.inflate(R.menu.image_change, menu)
            R.id.language -> menuInflater.inflate(R.menu.language_menu, menu)
        }
    }

    @SuppressLint("ResourceType")
    override fun onContextItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.changeImage -> selectImage()
            R.id.ro -> {
                setLanguage(item.title.toString())
            }
            R.id.en -> setLanguage(item.title.toString())
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setLanguage(language: String) {
        val editor = this.getSharedPreferences("prefs", 0).edit()
        editor.putString("language", language)
        val m: Matcher = Pattern.compile("\\(([^)]+)\\)").matcher(language)
        var languageCode = ""
        while (m.find()) {
            languageCode = m.group(1)
        }
        editor.putString("languageCode", languageCode)
        editor.apply()
        val resources: Resources = resources
        val dm: DisplayMetrics = resources.displayMetrics
        val config: Configuration = resources.configuration
        config.setLocale(Locale(languageCode.toLowerCase(Locale.ROOT)))
        resources.updateConfiguration(config, dm)
        startNewActivity(this)
    }

    private fun selectImage() {
        val i = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        startActivityForResult(i, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK && null != data) {
            filePath = data.data
            if(filePath != null)
                uploadImage()
        }
    }

    private fun uploadImage() {
        val progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Uploading...")
        progressDialog.show()
        val ref: StorageReference = storageReference.child("users/" + user.uid)
        filePath?.let {
            ref.putFile(it)
                .addOnSuccessListener {
                    progressDialog.dismiss()
                    Toast.makeText(this, "Profile Image Updated", Toast.LENGTH_SHORT).show()
                    when(userType){
                        "teacher" -> refresh(ProfileActivityTeacher())
                        "student" -> refresh(ProfileActivityStudent())
                    }
                    userServices.updateUser(user.uid, mapOf("photo" to  "users/" + user.uid))
                }
                .addOnFailureListener { e -> progressDialog.dismiss()
                    Toast.makeText(this, "Failed " + e.message, Toast.LENGTH_SHORT).show()
                }
                .addOnProgressListener { taskSnapshot ->
                    val progress = (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount)
                    progressDialog.setMessage("Uploaded " + progress.toInt() + "%")}
        }
    }

    fun refresh(activity: AppCompatActivity){
        finish()
        startNewActivity(activity)
    }
}