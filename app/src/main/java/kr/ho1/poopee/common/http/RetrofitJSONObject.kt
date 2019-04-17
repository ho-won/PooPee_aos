package kr.ho1.poopee.common.http

import kr.ho1.poopee.common.util.LogManager
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class RetrofitJSONObject(request: Call<ResponseBody>, onSuccess: (response: JSONObject) -> Unit, onFailed: () -> Unit) {
    companion object {
        const val TAG = "RetrofitHandler"
    }

    init {
        request.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                try {
                    try {
                        LogManager.e(TAG, "onSuccess")
                        LogManager.e(TAG, String.format("statusCode : %d", response.code()))
                        val result = response.body()!!.string()
                        LogManager.e(TAG, String.format("JSONObject : %s", result))
                        onSuccess(JSONObject(result))
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }

                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                LogManager.e(TAG, "onFailure")
                onFailed()
            }
        })
    }

}