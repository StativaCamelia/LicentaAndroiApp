package com.example.speechtherapy.bundles.common.helpers.DataListener

import com.google.firebase.firestore.QuerySnapshot

interface OnGetDataListenerQuery  {
    fun onSuccess(querySnapshot: QuerySnapshot?)
}