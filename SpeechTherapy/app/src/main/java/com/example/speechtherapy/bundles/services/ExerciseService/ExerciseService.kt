package com.example.speechtherapy.bundles.services.ExerciseService

import android.content.ContentValues
import android.content.Context
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import com.example.speechtherapy.bundles.common.helpers.DataListener.OnGetDataListener
import com.example.speechtherapy.bundles.common.helpers.DataListener.OnGetListListener
import com.example.speechtherapy.bundles.common.helpers.GlideApp
import com.example.speechtherapy.bundles.common.helpers.TextToSpeechUtils
import com.example.speechtherapy.bundles.model.dataModel.Exercise
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

class ExerciseService() {
    private val db = Firebase.firestore

    fun getExerciseWithImage(context: Context, textToSpeech: TextToSpeechUtils, collectionPath: String, imageContainer: ImageView, textContainer: TextView, isTextToSpeech: Boolean) {
        db.collection(collectionPath)
            .get().addOnSuccessListener { result ->
                val result = result.shuffled()
                val storage = FirebaseStorage.getInstance()
                val gsReference = storage.reference.child(result[0].data["uri"] as String)
                context.let {
                    GlideApp.with(it)
                        .load(gsReference)
                        .into(imageContainer)
                }
                val word = result[0].data["name"] as CharSequence?
                textContainer.text = word
                textToSpeech.listenWithGoogleLogic(isTextToSpeech, word.toString())
            }.addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting documents.", exception)
            }
    }

        fun getExerciseWithoutImage(textToSpeech: TextToSpeechUtils, collectionPath: String, wordDefinition:TextView, textContainer: TextView, isTextToSpeech: Boolean){
                db.collection(collectionPath).get().addOnSuccessListener { result ->
                    val result = result.shuffled()
                    val word = result[0].data["name"] as CharSequence?
                    textContainer.text = word
                    wordDefinition.text = result[0].data["definition"] as CharSequence?
                    textToSpeech.listenWithGoogleLogic(isTextToSpeech, word.toString())
                }.addOnFailureListener { exception ->
                    Log.w(ContentValues.TAG, "Error getting documents.", exception)
                }
        }

    fun getExerciseByName(
        wordDefinition: TextView,
        textContainer: TextView,
        isTextToSpeech: Boolean,
        name: String,
        description: String
    ){
        textContainer.text = name
        wordDefinition.text = description
    }

    fun getExerciseWithImageByName(
        context: Context,
        imageContainer: ImageView,
        textContainer: TextView,
        name: String,
        uri: String
    ) {
                val storage = FirebaseStorage.getInstance()
                val gsReference = storage.reference.child(uri)
                context.let {
                    GlideApp.with(it)
                        .load(gsReference)
                        .into(imageContainer)
                }
                val word = name
                textContainer.text = word
    }


    fun getExercises(collectionPath: String, listener: OnGetListListener){
        db.collection(collectionPath).get().addOnSuccessListener {
            result ->
            val exercises = mutableListOf<Exercise>()
            for(exerciseResult in result?.documents!!){
                var exercise: Exercise
                if(exerciseResult["uri"] != null)
                    exercise = Exercise(collectionPath, exerciseResult["name"] as String, exerciseResult["uri"] as String)
                else
                    exercise = Exercise(collectionPath, exerciseResult["name"] as String, exerciseResult["definition"] as String)
                exercises.add(exercise)
            }
            listener.onSuccess(exercises)
        }.addOnFailureListener { exception ->
            Log.w(ContentValues.TAG, "Error getting documents.", exception)
        }
    }

    fun getExercise(collectionPath: String,exercise: String,  listener: OnGetDataListener){
        db.collection(collectionPath).document(exercise).get().addOnSuccessListener {
            result -> listener.onSuccess(result)
        }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting documents.", exception)
            }
    }


}