package com.example.speechtherapy.bundles.classesActivities.teacher

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.speechtherapy.R
import com.example.speechtherapy.bundles.common.helpers.DataListener.OnGetDataListener
import com.example.speechtherapy.bundles.common.helpers.GlideApp
import com.example.speechtherapy.bundles.services.UserServices.UserServices
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class StudentAdapter(private val contextMain: Activity, private val uids: List<String>): ArrayAdapter<String>(contextMain, R.layout.teacher_student_item_view, uids) {

    private val userService: UserServices =  UserServices()
    private var storage: FirebaseStorage =  FirebaseStorage.getInstance()
    private var storageReference: StorageReference = storage.reference

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val viewRow = contextMain.layoutInflater.inflate(R.layout.teacher_student_item_view, null, true)
        userService.getUserById(uids[position], object: OnGetDataListener{
            override fun onSuccess(documentSnapshot: DocumentSnapshot?) {
                val studentName = viewRow.findViewById<TextView>(R.id.studentName)
                studentName.text = documentSnapshot?.data?.get("email").toString()
                val studentPhoto = viewRow.findViewById<ImageView>(R.id.profilePhoto)
                val photo = documentSnapshot?.data?.get("photo")
                if(photo!= null){
                    val gsReference = photo.let { storageReference.child(it as String) }
                    context.let {
                        GlideApp.with(it)
                            .load(gsReference)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .into(studentPhoto)
                    }
                }
            }
        })
        return viewRow
    }

}