package com.sunnyweather.android.logic.repository

import android.util.Log
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.sunnyweather.android.logic.model.UrlsResponse
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Request

class BilibiliRepository {
    fun getBilibiliFlvHeaders(roomId: String): Map<String, String> {
        return mutableMapOf(
            "sec-ch-ua" to "\"Chromium\";v=\"106\", \"Google Chrome\";v=\"106\", \"Not;A=Brand\";v=\"99\"",
            "sec-ch-ua-mobile" to "?0",
            "User-Agent" to "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/106.0.0.0 Safari/537.36",
            "sec-ch-ua-platform" to "Windows",
            "Accept" to "*/*",
            "Origin" to "https://live.bilibili.com",
            "Sec-Fetch-Site" to "cross-site",
            "Sec-Fetch-Mode" to "cors",
            "Sec-Fetch-Dest" to "empty",
            "Referer" to "https://live.bilibili.com/${roomId}",
            "Accept-Encoding" to "gzip, deflate, br",
            "Accept-Language" to "zh-CN,zh;q=0.9",
        )
    }

    fun getBilibiliRealUrlWithQn(roomId: String, qn: String): Map<String, String> {
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
            return mapOf()
        }
        val streamInfos: JSONArray = response.getJSONObject("data").getJSONObject("playurl_info")
            .getJSONObject("playurl").getJSONArray("stream")
        val acceptQn = mutableSetOf<String>()
        for (item in streamInfos.getJSONObject(0).getJSONArray("format").getJSONObject(0)
            .getJSONArray("codec").getJSONObject(0).getJSONArray("accept_qn")) {
            acceptQn.add(item.toString())
        }
//        val targetProtocol = if (acceptQn.size == 1) "http_stream" else "http_hls"
//        val targetFormat = if (acceptQn.size < 2) "flv" else "ts"
        val targetFormats = listOf("flv", "ts")
        val urlData = LinkedHashMap<String, String>()
        for (targetFormat in targetFormats) {
            Log.i("biliFormat", "using format: $targetFormat")
            for (i in 0 until streamInfos.size) {
                val streamInfo: JSONObject = streamInfos.getJSONObject(i)
                val format = streamInfo.getJSONArray("format")
                val formatName = format.getJSONObject(0).getString("format_name")
                if (targetFormat != formatName) {
                    continue
                }
                val codecInfo = format.getJSONObject(format.size-1).getJSONArray("codec").getJSONObject(0)
                if (qn != codecInfo.getString("current_qn")) {
                    Log.e("bilierror",
                        "BILIBILI---无效的qn---roomId：${roomId}, qn: ${qn}, acceptQn: $acceptQn"
                    )
                    break
                }
                Log.i("biliCodec", codecInfo.toJSONString())
                val baseUrl = codecInfo.getString("base_url")
                val urlInfo = codecInfo.getJSONArray("url_info").getJSONObject(0)
                val host = urlInfo.getString("host")
                val extra = urlInfo.getString("extra")
                Log.i("biliUrl", "format: $formatName, url: ${host + baseUrl + extra}")
                urlData[formatName] = host + baseUrl + extra
            }
        }
        return urlData
    }
    fun getBilibiliRealUrl(roomId: String): UrlsResponse {
//        val qnMap = mapOf("OD" to "10000", "HD" to "250")
        val qnMap = mapOf("原画" to "10000")
        val data = LinkedHashMap<String, String>()
        qnMap.forEach { e ->
            run {
                val urlData = getBilibiliRealUrlWithQn(roomId, e.value)
                data[e.key] = if (urlData.containsKey("flv")) urlData["flv"]!! else urlData["ts"]!!
                urlData.forEach {(k, v) ->
                    data[e.key + k] = v
                }
            }
        }
        return UrlsResponse("200", "success", data)
    }
}