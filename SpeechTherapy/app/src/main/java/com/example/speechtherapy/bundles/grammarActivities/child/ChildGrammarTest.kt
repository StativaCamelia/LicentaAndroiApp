package com.example.speechtherapy.bundles.grammarActivities.child

import android.os.Bundle
import com.example.speechtherapy.bundles.grammarActivities.BaseGrammarTestActivity

class ChildGrammarTest: BaseGrammarTestActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        super.onCreateParams(ChildGrammarTestFragment(), "ChildGrammarHomeworkFragment")
    }

}