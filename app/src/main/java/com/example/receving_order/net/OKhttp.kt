package com.example.receving_order.net

import android.util.Log
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.zhy.http.okhttp.OkHttpUtils
import com.zhy.http.okhttp.callback.StringCallback
import okhttp3.Call
import okhttp3.MediaType
import java.lang.reflect.Type
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


object OKhttp{
    /**
     * 表示所传参数被忽略
     */
    const val OPTIONAL = "optional"

    const val TAG = "OK_Result"

    const val MEDIA_TYPE = "application/json; charset=utf-8"

    /**
     * post请求
     */
    inline fun <reified T> post(
        url :String,
        crossinline onSuccess:(data : T) ->Unit,
        vararg pair: Pair<String,String>
    ){
        val mapJson=Gson().toJson(HashMap(pair.toMap()).filterValues { it!= OPTIONAL })
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
     * get请求
     */
    inline fun <reified T> get(
            url: String,
            crossinline onSuccess: (data: T) -> Unit,
            vararg params: Pair<String, String>) {
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