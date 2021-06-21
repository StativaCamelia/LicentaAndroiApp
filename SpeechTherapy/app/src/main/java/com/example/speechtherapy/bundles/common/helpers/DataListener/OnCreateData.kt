package com.example.speechtherapy.bundles.common.helpers.DataListener

import com.example.speechtherapy.bundles.services.ClassService.ConflictException

interface OnCreateData {
    fun onSuccess()
    fun onFailure(e: ConflictException)
}