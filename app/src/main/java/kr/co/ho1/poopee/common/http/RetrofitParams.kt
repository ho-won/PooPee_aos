package kr.co.ho1.poopee.common.http

import kr.co.ho1.poopee.gallery.util.ImageManager
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.util.*

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

    fun putFile(key: String, path: String) {
        val file = ImageManager.getImageFile(path, 0)
        val requestBody = RequestBody.create(MediaType.parse("image/*"), file)
        fileParams.add(MultipartBody.Part.createFormData(key, file.name, requestBody))
    }

    fun putFile(key: String, file: File) {
        val requestBody = RequestBody.create(MediaType.parse("image/*"), file)
        fileParams.add(MultipartBody.Part.createFormData(key, file.name, requestBody))
    }

}
