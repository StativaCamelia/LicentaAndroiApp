package com.example.speechtherapy.bundles.services
import android.content.ContentValues.TAG
import android.util.Log
import com.example.speechtherapy.bundles.model.dataModel.TestWord
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class TestWordService {
    private val db = Firebase.firestore

    fun writeNewWord(wordName: String, URI: String?, s: String) {
        val word = TestWord(wordName, URI)
        db.collection("adult_pronunciation").document(wordName)
            .set(mapOf("name" to wordName))
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
    }

    fun populateDatabase(){
        val words = arrayOf("abstain", "arbitrary", "aristocracy", "bias", "cognitive", "combat", "defunct", "dominant", "elliptical", "environment", "immerse", "indigenous", "legacy", "motivate", "perception", "prestige", "reliant", "stagnant", "strategy", "thesis", "vulnerable")
        val path = "/Children_Time/"
        val uri: MutableList<String> = ArrayList()
        for (item in words){
            uri.add("$path$item.jpg")
        }
        words.zip(uri).forEach {pair ->
           writeNewWord(pair.component1(), pair.component2(), "wild_animal")
        }
    }

}