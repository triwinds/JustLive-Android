package com.sunnyweather.android.ui.liveRoom

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.sunnyweather.android.logic.danmu.DanmuService
import com.sunnyweather.android.logic.model.DanmuSetting
import com.sunnyweather.android.logic.repository.Repository

class LiveRoomViewModel : ViewModel() {
    class UrlRequest (val platform: String, val roomId: String)
    class RoomInfoRequest (val uid: String, val platform: String, val roomId: String)
    class DanmuInfo(val userName: String, val content: String)
    class FollowRequest(val toFollow: Boolean, val platform: String, val roomId: String, val uid: String)

    var danmuNum = MutableLiveData<Int>()
    val danmuList = ArrayList<DanmuInfo>()
    var danmuSetting = MutableLiveData<DanmuSetting>()
    var danmuService: DanmuService? = null
    private val followLiveData = MutableLiveData<FollowRequest>()

    val followResponseLiveDate = Transformations.switchMap(followLiveData) {
            value ->
            if (value.toFollow) {
                Repository.follow(value.platform, value.roomId, value.uid)
            } else {
                Repository.unFollow(value.platform, value.roomId, value.uid)
            }
    }
    private val urlsRequestData = MutableLiveData<UrlRequest>()
    private val roomInfoRequestData = MutableLiveData<RoomInfoRequest>()

    val urlResponseData = Transformations.switchMap(urlsRequestData) {
        value -> Repository.getRealUrl(value.platform, value.roomId)
    }
    val roomInfoResponseData = Transformations.switchMap(roomInfoRequestData) {
            value -> Repository.getRoomInfo(value.uid, value.platform, value.roomId)
    }

    fun getRealUrl(platform: String, roomId: String) {
        urlsRequestData.value = UrlRequest(platform, roomId)
    }

    fun getRoomInfo(uid: String, platform: String, roomId: String) {
        roomInfoRequestData.value = RoomInfoRequest(uid, platform, roomId)
    }

    fun startDanmu(platform: String, roomId: String, banStrings: String?, isActive: Boolean) {
        if (danmuService != null) {
            danmuService?.stop()
        }
        danmuService = DanmuService(platform, roomId)
        danmuService?.changeActive(isActive)
        var isSelectedArray = ArrayList<String>()
        if (banStrings != "" && banStrings != null){
            if (banStrings.contains(";")) {
                isSelectedArray = banStrings.split(";") as ArrayList<String>
            } else {
                isSelectedArray.add(banStrings)
            }

        }
        danmuService?.connect(danmuList, danmuNum, isSelectedArray)
    }

    fun isConnecting(): Boolean {
        return danmuService != null && danmuService?.isConnected == true
    }

    fun banChanged(isActiveArray: ArrayList<String>) {
        danmuService?.changeBan(isActiveArray)
    }

    fun activeChange(isActive: Boolean) {
        danmuService?.changeActive(isActive)
    }

    fun stopDanmu(){
        danmuService?.stop()
    }

    fun follow(platform: String, roomId: String, uid: String){
        followLiveData.value = FollowRequest(true, platform, roomId, uid)
    }
    fun unFollow(platform: String, roomId: String, uid: String){
        followLiveData.value = FollowRequest(false, platform, roomId, uid)
    }
}