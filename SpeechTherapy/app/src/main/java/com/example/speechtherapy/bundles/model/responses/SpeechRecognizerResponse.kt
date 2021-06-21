package com.example.speechtherapy.bundles.model.responses

data class SpeechRecognizerResponse(val recording_status: String, val phones_sequence: List<String>, val correct_phones_sequence:List<String>, val pitch:String, val spectogram:String, val native_spectogram: String)
