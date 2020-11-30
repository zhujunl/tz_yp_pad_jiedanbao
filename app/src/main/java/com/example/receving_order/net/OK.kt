package com.example.receving_order.net

import android.util.Log
import com.google.gson.Gson
import com.yp.baselib.utils.LogUtils
import com.zhy.http.okhttp.OkHttpUtils
import com.zhy.http.okhttp.callback.StringCallback
import okhttp3.Call
import okhttp3.MediaType

/**
 * OKHTTP工具类
 */
object OK {

    /**
     * 表示所传参数被忽略
     */
    const val OPTIONAL = "optional"

    const val TAG = "OK_Result"

    const val MEDIA_TYPE = "application/json; charset=utf-8"

    /**
     * 是否保存日志到SD卡中
     */
    const val SAVE_LOG = false

    /**
     * Post请求（JSON字符串形式）
     */
    inline fun <reified T> post(
            url: String, //URL
            crossinline onSuccess: (data: T) -> Unit, //成功回调
            vararg pairs: Pair<String, String>//参数
    ) {
        val mapJson = Gson().toJson(HashMap(pairs.toMap()).filterValues { it != OPTIONAL })
        val builder = OkHttpUtils
                .postString()
                .url(url)
                .content(mapJson)
                .mediaType(MediaType.parse(MEDIA_TYPE))

        builder.build()
                .connTimeOut(6000)
                .readTimeOut(6000)
                .writeTimeOut(6000)
                .execute(object : StringCallback() {

                    override fun onError(call: Call?, e: Exception?, id: Int) {
                        Log.e(TAG, ".......................................................................................................................................")
                        Log.e(TAG, "×                                                                                                                                  ×")
                        Log.e(TAG, "                                                               请求失败（POST）                                                       ")
                        Log.e(TAG, "×                                                                                                                                  ×")
                        Log.e(TAG, ".......................................................................................................................................")
                        Log.e(
                                TAG,
                                " URL：$url\nJSON：$mapJson\nMessage：${e?.localizedMessage}"
                        );
                        call?.cancel()
                    }

                    override fun onResponse(response: String?, id: Int) {
                        Log.d(TAG, ".......................................................................................................................................")
                        Log.d(TAG, "√                                                                                                                                  √")
                        Log.d(TAG, "                                                               请求成功（POST）                                                       ")
                        Log.d(TAG, "√                                                                                                                                  √")
                        Log.d(TAG, ".......................................................................................................................................")
                        Log.d(TAG, " URL：$url\nJSON：$mapJson\nRESPONSE：$response")
                        onSuccess.invoke(Gson().fromJson(response, T::class.java))
                    }
                })

    }

    /**
     * Post请求（对象形式）
     * @param url String
     * @param json Any
     * @param onSuccess Function1<[@kotlin.ParameterName] T, Unit>
     */
    inline fun <reified T> post(
            url: String, //URL
            json: Any,
            crossinline onSuccess: (data: T) -> Unit//成功回调
    ) {

        val jsonString = Gson().toJson(json)

        val builder = OkHttpUtils
                .postString()
                .url(url)
                .content(jsonString)
                .mediaType(MediaType.parse(MEDIA_TYPE))

        builder.build()
                .connTimeOut(6000)
                .readTimeOut(6000)
                .writeTimeOut(6000)
                .execute(object : StringCallback() {

                    override fun onError(call: Call?, e: Exception?, id: Int) {
                        Log.e(TAG, ".......................................................................................................................................")
                        Log.e(TAG, "×                                                                                                                                  ×")
                        Log.e(TAG, "                                                               请求失败（POST）                                                       ")
                        Log.e(TAG, "×                                                                                                                                  ×")
                        Log.e(TAG, ".......................................................................................................................................")
                        Log.e(
                                TAG,
                                " URL：$url\nJSON：$jsonString\nMessage：${e?.localizedMessage}"
                        );
                        call?.cancel()
                    }

                    override fun onResponse(response: String?, id: Int) {
                        Log.d(TAG, ".......................................................................................................................................")
                        Log.d(TAG, "√                                                                                                                                  √")
                        Log.d(TAG, "                                                               请求成功（POST）                                                       ")
                        Log.d(TAG, "√                                                                                                                                  √")
                        Log.d(TAG, ".......................................................................................................................................")
                        Log.d(TAG, " URL：$url\nJSON：$jsonString\nRESPONSE：$response")
                        onSuccess.invoke(Gson().fromJson(response, T::class.java))
                    }
                })

    }

    /**
     * Get请求
     * @param url String
     * @param onSuccess Function1<[@kotlin.ParameterName] T, Unit>
     * @param params Array<out Pair<String, String>>
     */
    inline fun <reified T> get(
            url: String,
            crossinline onSuccess: (data: T) -> Unit,
            vararg params: Pair<String, String>
    ) {
        val req = OkHttpUtils
                .get()
                .url(url)

        params.forEach {
            req.addParams(it.first, it.second)
        }

        req.build()
                .execute(object : StringCallback() {
                    override fun onError(call: Call?, e: java.lang.Exception?, id: Int) {
                        Log.e(TAG, ".......................................................................................................................................")
                        Log.e(TAG, "×                                                                                                                                  ×")
                        Log.e(TAG, "                                                               请求失败（GET）                                                       ")
                        Log.e(TAG, "×                                                                                                                                  ×")
                        Log.e(TAG, ".......................................................................................................................................")
                        Log.e(
                                TAG,
                                " URL：$url\nMESSAGE：${e?.localizedMessage}"
                        );
                        call?.cancel()
                    }

                    override fun onResponse(response: String?, id: Int) {
                        Log.d(TAG, ".......................................................................................................................................")
                        Log.d(TAG, "√                                                                                                                                  √")
                        Log.d(TAG, "                                                               请求成功（GET）                                                       ")
                        Log.d(TAG, "√                                                                                                                                  √")
                        Log.d(TAG, ".......................................................................................................................................")
                        Log.d(TAG, " URL：$url\nRESPONSE：$response")
                        onSuccess.invoke(Gson().fromJson(response, T::class.java))
                    }
                })
    }

}