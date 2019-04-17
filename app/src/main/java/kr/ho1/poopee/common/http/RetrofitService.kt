package kr.ho1.poopee.common.http

import kr.ho1.poopee.common.ObserverManager
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.QueryMap

interface RetrofitService {
    companion object {
        val BASE_APP = if (ObserverManager.testServer) "http://test5.car2b.com/api/" else "http://test5.car2b.com/api/"

        const val TEST_API = "test.php" // test
    }

    @POST(TEST_API)
    fun test(@QueryMap params: Map<String, Any>): Call<ResponseBody>

    @Multipart
    @POST(TEST_API)
    fun testImage(@QueryMap params: Map<String, Any>, @Part files: List<MultipartBody.Part>): Call<ResponseBody>

}