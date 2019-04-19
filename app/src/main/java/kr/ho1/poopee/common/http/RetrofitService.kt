package kr.ho1.poopee.common.http

import kr.ho1.poopee.common.ObserverManager
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface RetrofitService {
    companion object {
        val BASE_APP = if (ObserverManager.testServer) "http://test5.car2b.com/api/" else "http://test5.car2b.com/api/"

        const val TEST_API = "test.php" // test
        const val LOGIN = "poopeeTest/login.php" // 로그인
        const val JOIN = "poopeeTest/join.php" // 회원가입
        const val NOTICE_LIST = "poopeeTest/noticeList.php" // 공지사항목록
        const val COMMENT_LIST = "poopeeTest/commentList.php" // 댓글목록
        const val COMMENT_CREATE = "poopeeTest/commentCreate.php" // 댓글작성
        const val COMMENT_DELETE = "poopeeTest/commentDelete.php" // 댓글삭제
        const val COMMENT_UPDATE = "poopeeTest/commentUpdate.php" // 댓글수정
        const val TOILET_COUNT = "poopeeTest/toiletCount.php" // 좋아요수 댓글수
        const val TOILET_LIKE = "poopeeTest/toiletLike.php" // 좋아요
    }

    @POST(TEST_API)
    fun test(@QueryMap params: Map<String, @JvmSuppressWildcards Any>): Call<ResponseBody>

    @Multipart
    @POST(TEST_API)
    fun testImage(@QueryMap params: Map<String, @JvmSuppressWildcards Any>, @Part files: List<MultipartBody.Part>): Call<ResponseBody>

    @GET(LOGIN)
    fun login(@QueryMap params: Map<String, @JvmSuppressWildcards Any>): Call<ResponseBody>

    @POST(JOIN)
    fun join(@QueryMap params: Map<String, @JvmSuppressWildcards Any>): Call<ResponseBody>

    @GET(NOTICE_LIST)
    fun noticeList(@QueryMap params: Map<String, @JvmSuppressWildcards Any>): Call<ResponseBody>

    @GET(COMMENT_LIST)
    fun commentList(@QueryMap params: Map<String, @JvmSuppressWildcards Any>): Call<ResponseBody>

    @POST(COMMENT_CREATE)
    fun commentCreate(@QueryMap params: Map<String, @JvmSuppressWildcards Any>): Call<ResponseBody>

    @DELETE(COMMENT_DELETE)
    fun commentDelete(@QueryMap params: Map<String, @JvmSuppressWildcards Any>): Call<ResponseBody>

    @PUT(COMMENT_UPDATE)
    fun commentUpdate(@QueryMap params: Map<String, @JvmSuppressWildcards Any>): Call<ResponseBody>

    @GET(TOILET_COUNT)
    fun toiletCount(@QueryMap params: Map<String, @JvmSuppressWildcards Any>): Call<ResponseBody>

    @POST(TOILET_LIKE)
    fun toiletLike(@QueryMap params: Map<String, @JvmSuppressWildcards Any>): Call<ResponseBody>

}