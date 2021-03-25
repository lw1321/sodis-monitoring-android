package de.sodis.monitoring.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


class MyViewModelFactory(private val mApplication: Application, private val mParams: List<Any>) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
            modelClass === PlaceViewModel::class.java -> PlaceViewModel(mApplication) as T
            modelClass === TodoPointModel::class.java -> TodoPointModel(mApplication) as T
            modelClass === SurveyViewModel::class.java -> SurveyViewModel(mApplication) as T
            modelClass === RegisterViewModel::class.java -> RegisterViewModel(mApplication) as T
            modelClass === RootViewModel::class.java -> RootViewModel(mApplication) as T
            modelClass === VillageModel::class.java -> VillageModel(mApplication) as T
            modelClass === QuestionViewModel::class.java -> QuestionViewModel(
                mApplication,
                mParams[0] as Int
            ) as T
            else -> super.create(modelClass)
        }
    }

}