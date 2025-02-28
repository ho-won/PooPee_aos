package kr.co.ho1.poopee.common.http

import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody

class RetrofitParams {
    private val params = HashMap<String, Any>()
    private val fileParams = ArrayList<MultipartBody.Part>()

    fun getParams(): Map<String, Any> {
        return params
    }

    fun getFileParams(): List<MultipartBody.Part> {
        if (fileParams.size == 0) {
            val requestBody = RequestBody.create(MediaType.parse("text/plain"), "")
            fileParams.add(MultipartBody.Part.createFormData("", "", requestBody))
        }
        return fileParams
    }

    fun put(key: String, value: Any) {
        params[key] = value
    }

}
