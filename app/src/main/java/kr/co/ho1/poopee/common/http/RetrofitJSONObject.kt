package kr.co.ho1.poopee.common.http

import kr.co.ho1.poopee.common.util.LogManager
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RetrofitJSONObject(request: Call<ResponseBody>, onSuccess: (response: JSONObject) -> Unit, onFailed: () -> Unit) {
    companion object {
        const val TAG = "RetrofitHandler"
    }

    init {
        LogManager.e(TAG, request.request().url().toString())
        request.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                try {
                    LogManager.e(TAG, String.format("statusCode : %d", response.code()))

                    if (response.code() == 200) {
                        val result = response.body()!!.string()
                        LogManager.e(TAG, String.format("JSONObject : %s", result))

                        val jsonObject = JSONObject(result)
                        onSuccess(jsonObject)
                    } else {
                        onFailed()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    onFailed()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                LogManager.e(TAG, "onFailure")
                onFailed()
            }
        })
    }

}