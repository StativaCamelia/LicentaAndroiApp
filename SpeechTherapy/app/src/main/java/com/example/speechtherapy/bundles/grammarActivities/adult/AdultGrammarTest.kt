package com.example.speechtherapy.bundles.grammarActivities.adult

import android.os.Bundle
import com.example.speechtherapy.bundles.grammarActivities.BaseGrammarTestActivity

class AdultGrammarTest: BaseGrammarTestActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        super.onCreateParams(AdultGrammarTestFragment(), "AdultGrammarHomeworkFragment")
    }



}