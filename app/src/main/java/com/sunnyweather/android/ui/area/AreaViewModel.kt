package com.sunnyweather.android.ui.area

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.sunnyweather.android.logic.model.AreaInfo
import com.sunnyweather.android.logic.repository.Repository

class AreaViewModel : ViewModel() {
    private val temp = MutableLiveData<Int>()

    var areaList = ArrayList<AreaInfo>()

    var areaListLiveDate = Transformations.switchMap(temp) {
        Repository.getAllAreas()
    }

    fun getAllAreas() {
        temp.value = 1
    }
}