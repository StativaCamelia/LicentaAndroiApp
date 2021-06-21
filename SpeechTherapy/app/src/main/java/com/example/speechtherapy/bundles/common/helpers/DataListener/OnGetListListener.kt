package com.example.speechtherapy.bundles.common.helpers.DataListener

import com.google.firebase.firestore.DocumentSnapshot

interface OnGetListListener {
    fun onSuccess(result: MutableList<*>)
}