package com.example.speechtherapy.bundles.common.helpers.DataListener

import com.google.firebase.firestore.DocumentSnapshot


interface OnGetDataListener {
        fun onSuccess(documentSnapshot: DocumentSnapshot?)
}