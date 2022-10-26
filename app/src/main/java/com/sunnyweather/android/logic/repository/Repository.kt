package com.sunnyweather.android.logic.repository

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.liveData
import com.google.gson.internal.LinkedTreeMap
import com.sunnyweather.android.SunnyWeatherApplication.Companion.context
import com.sunnyweather.android.logic.model.RoomInfo
import com.sunnyweather.android.logic.model.UrlsResponse
import com.sunnyweather.android.logic.model.UserInfo
import com.sunnyweather.android.logic.network.LiveNetwork
import kotlinx.coroutines.Dispatchers


object Repository {
    val bilibiliRepository = BilibiliRepository()
    fun getRecommend(page: Int, size: Int) = liveData(Dispatchers.IO){
        val result = try {
            val liveResponse = LiveNetwork.getRecommend(page, size)
            when (liveResponse.code) {
                "200" -> {
                    val rooms = liveResponse.data
                    Result.success(rooms)
                }
                "400" -> {
                    val rooms = liveResponse.message
                    Result.success(rooms)
                }
                else -> {
                    "请求异常，请联系作者".showToast(context)
                    Result.failure(RuntimeException("response status is ${liveResponse.message}"))
                }
            }
        } catch (e: Exception) {
            Result.failure<List<RoomInfo>>(e)
        }
        emit(result)
    }
    fun getRecommendByPlatform(platform: String, page: Int, size: Int) = liveData(Dispatchers.IO){
        val result = try {
            val liveResponse = LiveNetwork.getRecommendByPlatform(platform, page, size)
            when (liveResponse.code) {
                "200" -> {
                    val rooms = liveResponse.data
                    Result.success(rooms)
                }
                "400" -> {
                    val rooms = liveResponse.message
                    Result.success(rooms)
                }
                else -> {
                    "请求异常，请联系作者".showToast(context)
                    Result.failure(RuntimeException("response status is ${liveResponse.message}"))
                }
            }
        } catch (e: Exception) {
            Result.failure<List<RoomInfo>>(e)
        }
        emit(result)
    }
    fun getRecommendByPlatformArea(platform: String, area: String, page: Int, size: Int) = liveData(Dispatchers.IO){
        val result = try {
            val liveResponse = LiveNetwork.getRecommendByPlatformArea(platform, area, page, size)
            when (liveResponse.code) {
                "200" -> {
                    val rooms = liveResponse.data
                    Result.success(rooms)
                }
                "400" -> {
                    val rooms = liveResponse.message
                    Result.success(rooms)
                }
                else -> {
                    "请求异常，请联系作者".showToast(context)
                    Result.failure(RuntimeException("response status is ${liveResponse.message}"))
                }
            }
        } catch (e: Exception) {
            Result.failure<List<RoomInfo>>(e)
        }
        emit(result)
    }
    fun getRecommendByAreaAll(areaType: String, area: String, page: Int) = liveData(Dispatchers.IO){
        val result = try {
            val liveResponse = LiveNetwork.getRecommendByAreaAll(areaType, area, page)
            when (liveResponse.code) {
                "200" -> {
                    val rooms = liveResponse.data
                    Result.success(rooms)
                }
                "400" -> {
                    val rooms = liveResponse.message
                    Result.success(rooms)
                }
                else -> {
                    "请求异常，请联系作者".showToast(context)
                    Result.failure(RuntimeException("response status is ${liveResponse.message}"))
                }
            }
        } catch (e: Exception) {
            Result.failure<List<RoomInfo>>(e)
        }
        emit(result)
    }

    fun getRealUrl(platform: String, roomId: String) = liveData(Dispatchers.IO){
        val result = try {
            var liveResponse: UrlsResponse = if ("bilibili".equals(platform, true)) {
                bilibiliRepository.getBilibiliRealUrl(roomId)
            } else {
                LiveNetwork.getRealUrl(platform, roomId)
            }
            when (liveResponse.code) {
                "200" -> {
                    val rooms = liveResponse.data
                    val resultRooms = LinkedTreeMap<String, String>()
                    rooms.forEach {(k, v) ->
                        val rk = when (k) {
                            "OD" -> "原画"
                            "HD" -> "超清"
                            "SD" -> "高清"
                            "LD" -> "清晰"
                            "FD" -> "流畅"
                            else -> k
                        }
                        resultRooms[rk] = v
                    }
                    Result.success(resultRooms)
                }
                "400" -> {
                    val rooms = liveResponse.message
                    Result.success(rooms)
                }
                else -> {
                    "请求异常，请联系作者".showToast(context)
                    Result.failure(RuntimeException("response status is ${liveResponse.message}"))
                }
            }
        } catch (e: Exception) {
            Result.failure<List<RoomInfo>>(e)
        }
        emit(result)
    }
    fun getRoomInfo(uid: String, platform: String, roomId: String) = liveData(Dispatchers.IO){
        val result = try {
            val liveResponse = LiveNetwork.getRoomInfo(uid, platform, roomId)
            when (liveResponse.code) {
                "200" -> {
                    val rooms = liveResponse.data
                    Result.success(rooms)
                }
                "400" -> {
                    val rooms = liveResponse.message
                    Result.success(rooms)
                }
                else -> {
                    "请求异常，请联系作者".showToast(context)
                    Result.failure(RuntimeException("response status is ${liveResponse.message}"))
                }
            }
        } catch (e: Exception) {
            Result.failure<List<RoomInfo>>(e)
        }
        emit(result)
    }
    fun getRoomsOn(uid: String) = liveData(Dispatchers.IO){
        val result = try {
            val liveResponse = LiveNetwork.getRoomsOn(uid)
            when (liveResponse.code) {
                "200" -> {
                    val rooms = liveResponse.data
                    Result.success(rooms)
                }
                "400" -> {
                    val rooms = liveResponse.message
                    Result.success(rooms)
                }
                else -> {
                    "请求异常，请联系作者".showToast(context)
                    Result.failure(RuntimeException("response status is ${liveResponse.message}"))
                }
            }
        } catch (e: Exception) {
            Result.failure<List<RoomInfo>>(e)
        }
        emit(result)
    }
    fun search(platform: String, keyWords: String, isLive: String) = liveData(Dispatchers.IO){
        val result = try {
            val liveResponse = LiveNetwork.Search(platform, keyWords, isLive)
            when (liveResponse.code) {
                "200" -> {
                    val rooms = liveResponse.data
                    Result.success(rooms)
                }
                "400" -> {
                    val rooms = liveResponse.message
                    Result.success(rooms)
                }
                else -> {
                    "请求异常，请联系作者".showToast(context)
                    Result.failure(RuntimeException("response status is ${liveResponse.message}"))
                }
            }
        } catch (e: Exception) {
            Result.failure<List<RoomInfo>>(e)
        }
        emit(result)
    }
    fun getAllAreas() = liveData(Dispatchers.IO){
        val result = try {
            val liveResponse = LiveNetwork.getAllAreas()
            when (liveResponse.code) {
                "200" -> {
                    val rooms = liveResponse.data
                    Result.success(rooms)
                }
                "400" -> {
                    val rooms = liveResponse.message
                    Result.success(rooms)
                }
                else -> {
                    "请求异常，请联系作者".showToast(context)
                    Result.failure(RuntimeException("response status is ${liveResponse.message}"))
                }
            }
        } catch (e: Exception) {
            Result.failure<List<RoomInfo>>(e)
        }
        emit(result)
    }
    fun login(username: String, password: String) = liveData(Dispatchers.IO){
        val result = try {
            val liveResponse = LiveNetwork.login(username, password)
            when (liveResponse.code) {
                "200" -> {
                    val rooms = liveResponse.data
                    Result.success(rooms)
                }
                "400" -> {
                    val rooms = liveResponse.message
                    Result.success(rooms)
                }
                else -> {
                    "请求异常，请联系作者".showToast(context)
                    Result.failure(RuntimeException("response status is ${liveResponse.message}"))
                }
            }
        } catch (e: Exception) {
            Result.failure<List<RoomInfo>>(e)
        }
        emit(result)
    }
    fun changeUserInfo(userInfo: UserInfo) = liveData(Dispatchers.IO){
        val result = try {
            val liveResponse = LiveNetwork.changeUserInfo(userInfo)
            when (liveResponse.code) {
                "200" -> {
                    val rooms = liveResponse.data
                    Result.success(rooms)
                }
                "400" -> {
                    val rooms = liveResponse.message
                    Result.success(rooms)
                }
                else -> {
                    "请求异常，请联系作者".showToast(context)
                    Result.failure(RuntimeException("response status is ${liveResponse.message}"))
                }
            }
        } catch (e: Exception) {
            Result.failure<List<RoomInfo>>(e)
        }
        emit(result)
    }
    fun register(username: String, nickname: String, password: String) = liveData(Dispatchers.IO){
        val result = try {
            val liveResponse = LiveNetwork.register(username, nickname, password)
            when (liveResponse.code) {
                "200" -> {
                    val rooms = liveResponse.data
                    Result.success(rooms)
                }
                "400" -> {
                    val rooms = liveResponse.message
                    Result.success(rooms)
                }
                else -> {
                    "请求异常，请联系作者".showToast(context)
                    Result.failure(RuntimeException("response status is ${liveResponse.message}"))
                }
            }
        } catch (e: Exception) {
            Result.failure<List<RoomInfo>>(e)
        }
        emit(result)
    }
    fun follow(platform: String, roomId: String, uid: String) = liveData(Dispatchers.IO){
        val result = try {
            val liveResponse = LiveNetwork.follow(platform, roomId, uid)
            when (liveResponse.code) {
                "200" -> {
                    val rooms = liveResponse.data
                    Result.success(rooms)
                }
                "400" -> {
                    val rooms = liveResponse.message
                    Result.success(rooms)
                }
                else -> {
                    "请求异常，请联系作者".showToast(context)
                    Result.failure(RuntimeException("response status is ${liveResponse.message}"))
                }
            }
        } catch (e: Exception) {
            Result.failure<List<RoomInfo>>(e)
        }
        emit(result)
    }
    fun unFollow(platform: String, roomId: String, uid: String) = liveData(Dispatchers.IO){
        val result = try {
            val liveResponse = LiveNetwork.unFollow(platform, roomId, uid)
            when (liveResponse.code) {
                "200" -> {
                    val rooms = liveResponse.data
                    Result.success(rooms)
                }
                "400" -> {
                    val rooms = liveResponse.message
                    Result.success(rooms)
                }
                else -> {
                    "请求异常，请联系作者".showToast(context)
                    Result.failure(RuntimeException("response status is ${liveResponse.message}"))
                }
            }
        } catch (e: Exception) {
            Result.failure<List<RoomInfo>>(e)
        }
        emit(result)
    }
    fun versionUpdate() = liveData(Dispatchers.IO){
        val result = try {
            val liveResponse = LiveNetwork.versionUpdate()
            when (liveResponse.code) {
                "200" -> {
                    val rooms = liveResponse.data
                    Result.success(rooms)
                }
                "400" -> {
                    val rooms = liveResponse.message
                    Result.success(rooms)
                }
                else -> {
                    "请求异常，请联系作者".showToast(context)
                    Result.failure(RuntimeException("response status is ${liveResponse.message}"))
                }
            }
        } catch (e: Exception) {
            Result.failure<List<RoomInfo>>(e)
        }
        emit(result)
    }
    //toast
    private fun String.showToast(context: Context) {
        Toast.makeText(context, this, Toast.LENGTH_SHORT).show()
    }
}