package kr.ho1.poopee.common.http

import kr.ho1.poopee.common.ObserverManager
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface RetrofitService {
    companion object {
        val BASE_APP = if (ObserverManager.testServer) "http://poopee.ho1.co.kr/" else "http://poopee.ho1.co.kr/"

        const val TEST_API = "etcs/test" // test
        const val DB_CHECK = "etcs/dbCheck" // [GET] toilet db 버전체크
        const val SERVER_CHECK = "etcs/serverCheck" // [GET] 서버상태체크
        const val LOGIN = "members/login" // [POST] 로그인
        const val JOIN = "members/join" // [POST] 회원가입
        const val USER_UPDATE = "members/updateUser" // [PUT] 회원정보수정
        const val OVER_LAP = "members/getOverlap" // [GET] 아이디 중복체크
        const val NOTICE_LIST = "notices/getNoticeList" // [GET] 공지사항목록
        const val TOILET_INFO = "toilets/getToiletInfo" // [GET] 화장실 정보
        const val TOILET_LIKE = "toiletLikes/setToiletLike" // [POST] 좋아요
        const val COMMENT_LIST = "comments/getCommentList" // [GET] 댓글목록
        const val COMMENT_CREATE = "comments/createComment" // [POST] 댓글작성
        const val COMMENT_DELETE = "comments/deleteComment" // [DELETE] 댓글삭제
        const val COMMENT_UPDATE = "comments/updateComment" // [PUT] 댓글수정
        const val COMMENT_REPORT_CREATE = "commentReports/createReport" // [POST] 댓글신고

        const val TERMS_01 = "etcs/getTerms01" // 개인정보 처리방침
        const val TERMS_02 = "etcs/getTerms02" // 서비스 이용약관
        const val TERMS_03 = "etcs/getTerms03" // 위치정보기반 서비스 이용약관

        const val KAKAO_LOCAL = "https://dapi.kakao.com/v2/"
        const val KAKAO_API_KEY = "dff7010c98c6542a9977f13c10d71a91"
        const val KAKAO_LOCAL_SEARCH = "local/search/keyword.json" // 카카오지도 키워드 검색
        const val KAKAO_COORD_TO_ADDRESS = "local/geo/coord2address.json" // 카카오 좌표 -> 주소 변환
    }

    @FormUrlEncoded
    @POST(TEST_API)
    fun test(@FieldMap params: Map<String, @JvmSuppressWildcards Any>): Call<ResponseBody>

    @GET(KAKAO_LOCAL_SEARCH)
    fun kakaoLocalSearch(@QueryMap params: Map<String, @JvmSuppressWildcards Any>): Call<ResponseBody>

    @GET(KAKAO_COORD_TO_ADDRESS)
    fun kakaoLocalCoordToAddress(@QueryMap params: Map<String, @JvmSuppressWildcards Any>): Call<ResponseBody>

    @GET(DB_CHECK)
    fun dbCheck(@QueryMap params: Map<String, @JvmSuppressWildcards Any>): Call<ResponseBody>

    @GET(SERVER_CHECK)
    fun serverCheck(@QueryMap params: Map<String, @JvmSuppressWildcards Any>): Call<ResponseBody>

    @FormUrlEncoded
    @POST(LOGIN)
    fun login(@FieldMap params: Map<String, @JvmSuppressWildcards Any>): Call<ResponseBody>

    @FormUrlEncoded
    @POST(JOIN)
    fun join(@FieldMap params: Map<String, @JvmSuppressWildcards Any>): Call<ResponseBody>

    @FormUrlEncoded
    @PUT(USER_UPDATE)
    fun updateUser(@FieldMap params: Map<String, @JvmSuppressWildcards Any>): Call<ResponseBody>

    @GET(OVER_LAP)
    fun overLap(@QueryMap params: Map<String, @JvmSuppressWildcards Any>): Call<ResponseBody>

    @GET(NOTICE_LIST)
    fun noticeList(@QueryMap params: Map<String, @JvmSuppressWildcards Any>): Call<ResponseBody>

    @GET(COMMENT_LIST)
    fun commentList(@QueryMap params: Map<String, @JvmSuppressWildcards Any>): Call<ResponseBody>

    @FormUrlEncoded
    @POST(COMMENT_CREATE)
    fun commentCreate(@FieldMap params: Map<String, @JvmSuppressWildcards Any>): Call<ResponseBody>

    @DELETE(COMMENT_DELETE)
    fun commentDelete(@QueryMap params: Map<String, @JvmSuppressWildcards Any>): Call<ResponseBody>

    @FormUrlEncoded
    @PUT(COMMENT_UPDATE)
    fun commentUpdate(@FieldMap params: Map<String, @JvmSuppressWildcards Any>): Call<ResponseBody>

    @FormUrlEncoded
    @POST(COMMENT_REPORT_CREATE)
    fun createCommentReport(@FieldMap params: Map<String, @JvmSuppressWildcards Any>): Call<ResponseBody>

    @GET(TOILET_INFO)
    fun toiletInfo(@QueryMap params: Map<String, @JvmSuppressWildcards Any>): Call<ResponseBody>

    @FormUrlEncoded
    @POST(TOILET_LIKE)
    fun toiletLike(@FieldMap params: Map<String, @JvmSuppressWildcards Any>): Call<ResponseBody>

}