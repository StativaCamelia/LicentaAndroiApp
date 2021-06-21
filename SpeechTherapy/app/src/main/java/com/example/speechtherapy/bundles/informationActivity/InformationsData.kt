package com.example.speechtherapy.bundles.informationActivity

import com.example.speechtherapy.R

class InformationsData{
    companion object {
        fun <ArrayList> getListViewModelList(): ArrayList {
            val listViewModelArrayList = ArrayList<InformationsViewModel>()
            listViewModelArrayList.add(InformationsViewModel(1, "What are speech disorders?", R.drawable.info_speecharticle, "Medically reviewed by Sara Minnis, M.S., CCC-SLP — Written by April Kahn — Updated on September 20, 2019", R.raw.article1))
            listViewModelArrayList.add(InformationsViewModel(2, "Stuttering: Symptoms and causes", R.drawable.info_stuttering, "By Mayo Clinic Staff", R.raw.article2))
            listViewModelArrayList.add(InformationsViewModel(3, "Apraxia: Symptoms, Causes, Tests, Treatments", R.drawable.info_apraxia, "WebMD Medical Reference Reviewed by Neil Lava, MD on May 05, 2019", R.raw.article3))
            listViewModelArrayList.add(InformationsViewModel(4, "Dysarthria (Slurred Speech): Symptoms and causes", R.drawable.info_dysarthria, "By Mayo Clinic Staff", R.raw.article4))
            listViewModelArrayList.add(InformationsViewModel(5, "How to diagnose speech disorders", R.drawable.info_diagnosis, "Medically reviewed by Sara Minnis, M.S., CCC-SLP — Written by April Kahn — Updated on September 20, 2019", R.raw.article5))
            listViewModelArrayList.add(InformationsViewModel(6, "10 Tips for perfect english pronunciation", R.drawable.info_tips, "https://englishlive.ef.com/blog/language-lab/10-tips-perfect-english-pronunciation/", R.raw.article6))
            listViewModelArrayList.add(InformationsViewModel(7, "Speech Sound Disorders in Children", R.drawable.info_kidarticle, "© 2020 STANFORD CHILDREN'S HEALTH", R.raw.article7))
            listViewModelArrayList.add(InformationsViewModel(8, "Common mistakes in spoken english made by Romanian Speakers", R.drawable.info_common, "https://gcalers.wordpress.com/2011/06/15/its-my-sister-mistakes-romanians-commonly-make-in-english/", R.raw.article8))
            listViewModelArrayList.add(InformationsViewModel(9, "Helping children learn language", R.drawable.info_helpchilds, "https://raisingchildren.net.au/babies/development/language-development/language-development-0-8", R.raw.article9))
            listViewModelArrayList.add(InformationsViewModel(10, "Specific Language Impairment", R.drawable.info_impediments, "https://www.nidcd.nih.gov/health/specific-language-impairment", R.raw.article10))
            return listViewModelArrayList as ArrayList
        }
    }
}