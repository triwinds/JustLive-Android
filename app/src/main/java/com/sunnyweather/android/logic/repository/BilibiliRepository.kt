package com.sunnyweather.android.logic.repository

import android.util.Log
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.sunnyweather.android.logic.model.UrlsResponse
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Request

class BilibiliRepository {
    fun getBilibiliRealUrlWithQn(roomId: String, qn: String): String? {
        val client = OkHttpClient.Builder().build()
        val url =
            "https://api.live.bilibili.com/xlive/web-room/v2/index/getRoomPlayInfo".toHttpUrlOrNull()
                ?.newBuilder()
                ?.addQueryParameter("room_id", roomId)
                ?.addQueryParameter("protocol", "0,1")
                ?.addQueryParameter("format", "0,1,2")
                ?.addQueryParameter("codec", "0,1")
                ?.addQueryParameter("qn", qn)
                ?.addQueryParameter("platform", "web")
                ?.addQueryParameter("ptype", "8")
                ?.build()
        val request = Request.Builder()
            .url(url!!)
            .build()
        val result = client.newCall(request).execute()
        val body = result.body
        val response = JSONObject.parseObject(body!!.string())
        if (0 != response.getInteger("code")) {
            Log.e("bilierror",
                "BILIBILI---获取真实地址异常---roomId：${roomId}, code: ${response.getInteger("code")}")
            return null
        }
        val streamInfos: JSONArray = response.getJSONObject("data").getJSONObject("playurl_info")
            .getJSONObject("playurl").getJSONArray("stream")
        for (i in 0 until streamInfos.size) {
            val streamInfo: JSONObject = streamInfos.getJSONObject(i)
            val format: JSONArray = streamInfo.getJSONArray("format")
            val formatName: String = format.getJSONObject(0).getString("format_name")
            if ("ts" == formatName) {
                val formatInfo: JSONObject = format.getJSONObject(format.size - 1)
                val codecInfo = formatInfo.getJSONArray("codec").getJSONObject(0)
//                if (CollectionUtils.isEmpty(acceptQn)) {
//                    for (qno in codecInfo.getJSONArray("accept_qn")) {
//                        acceptQn.add(qno.toString())
//                    }
//                }
                if (!qn.equals(codecInfo.getString("current_qn"))) {
                    Log.e("bilierror",
                        "BILIBILI---无效的qn---roomId：${roomId}, qn: ${qn}, acceptQn: {}"
                    )
                    break
                }
                val baseUrl = codecInfo.getString("base_url")
                val urlInfo = codecInfo.getJSONArray("url_info").getJSONObject(0)
                val host = urlInfo.getString("host")
                val extra = urlInfo.getString("extra")
                return host + baseUrl + extra
            }
        }
        return null
    }
    fun getBilibiliRealUrl(roomId: String): UrlsResponse {
//        val qnMap = mapOf("OD" to "10000", "HD" to "250")
        val qnMap = mapOf("OD" to "10000")
        val data = mutableMapOf<String, String>()
        qnMap.forEach { e ->
            run {
                e.key
                val url = getBilibiliRealUrlWithQn(roomId, e.value)
                if (url != null) {
                    data[e.key] = url
                }
            }
        }
        return UrlsResponse("200", "success", data)
    }
}