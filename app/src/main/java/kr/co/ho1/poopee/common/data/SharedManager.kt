package kr.co.ho1.poopee.common.data

import android.content.Context
import kr.co.ho1.poopee.common.ObserverManager

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
object SharedManager {
    private const val LOGIN = "LOGIN"
    private const val LOGIN_CHECK = "LOGIN_CHECK" // 로그인체크
    private const val FCM_KEY = "FCM_KEY" // push key

    private const val MEMBER = "MEMBER"
    private const val MEMBER_ID = "MEMBER_ID" // 멤버 아이디
    private const val MEMBER_USERNAME = "MEMBER_USERNAME" // 유저 아이디
    private const val MEMBER_PASSWORD = "MEMBER_PASSWORD" // 유저 비밀번호
    private const val MEMBER_NAME = "MEMBER_NAME" // 유저 닉네임
    private const val MEMBER_GENDER = "MEMBER_GENDER" // 유저 성별 0(남자) 1(여자)

    private const val ETC = "ETC"
    private const val DB_VER = "DB_VER" // toilet db 버전
    private const val NOTICE_DATE = "NOTICE_DATE" // 서버공지 다시보지않기 체크시간(Y-m-d)
    private const val NOTICE_IMAGE = "NOTICE_IMAGE" // 서버공지 이미지
    private const val LATITUDE = "LATITUDE" // latitude
    private const val LONGITUDE = "LONGITUDE" // longitude
    private const val PUSH = "PUSH" // 푸쉬알림
    private const val FIRST_CHECK = "FIRST_CHECK" // 앱 최초실행체크

    fun isLoginCheck(): Boolean {
        val pref = ObserverManager.context!!.getSharedPreferences(LOGIN, Context.MODE_PRIVATE)
        return pref.getBoolean(LOGIN_CHECK, false)
    }

    fun setLoginCheck(value: Boolean) {
        val pref = ObserverManager.context!!.getSharedPreferences(LOGIN, Context.MODE_PRIVATE)
        val editor = pref.edit()
        editor.putBoolean(LOGIN_CHECK, value)
        editor.apply()
    }

    fun getFcmKey(): String {
        val pref = ObserverManager.context!!.getSharedPreferences(LOGIN, Context.MODE_PRIVATE)
        return pref.getString(FCM_KEY, "").toString()
    }

    fun setFcmKey(value: String) {
        val pref = ObserverManager.context!!.getSharedPreferences(LOGIN, Context.MODE_PRIVATE)
        val editor = pref.edit()
        editor.putString(FCM_KEY, value)
        editor.apply()
    }

    fun getMemberId(): String {
        val pref = ObserverManager.context!!.getSharedPreferences(MEMBER, Context.MODE_PRIVATE)
        return pref.getString(MEMBER_ID, "").toString()
    }

    fun setMemberId(value: String) {
        val pref = ObserverManager.context!!.getSharedPreferences(MEMBER, Context.MODE_PRIVATE)
        val editor = pref.edit()
        editor.putString(MEMBER_ID, value)
        editor.apply()
    }

    fun getMemberUsername(): String {
        val pref = ObserverManager.context!!.getSharedPreferences(MEMBER, Context.MODE_PRIVATE)
        return pref.getString(MEMBER_USERNAME, "").toString()
    }

    fun setMemberUsername(value: String) {
        val pref = ObserverManager.context!!.getSharedPreferences(MEMBER, Context.MODE_PRIVATE)
        val editor = pref.edit()
        editor.putString(MEMBER_USERNAME, value)
        editor.apply()
    }

    fun getMemberPassword(): String {
        val pref = ObserverManager.context!!.getSharedPreferences(MEMBER, Context.MODE_PRIVATE)
        return pref.getString(MEMBER_PASSWORD, "").toString()
    }

    fun setMemberPassword(value: String) {
        val pref = ObserverManager.context!!.getSharedPreferences(MEMBER, Context.MODE_PRIVATE)
        val editor = pref.edit()
        editor.putString(MEMBER_PASSWORD, value)
        editor.apply()
    }

    fun getMemberName(): String {
        val pref = ObserverManager.context!!.getSharedPreferences(MEMBER, Context.MODE_PRIVATE)
        return pref.getString(MEMBER_NAME, "").toString()
    }

    fun setMemberName(value: String) {
        val pref = ObserverManager.context!!.getSharedPreferences(MEMBER, Context.MODE_PRIVATE)
        val editor = pref.edit()
        editor.putString(MEMBER_NAME, value)
        editor.apply()
    }

    fun getMemberGender(): String {
        val pref = ObserverManager.context!!.getSharedPreferences(MEMBER, Context.MODE_PRIVATE)
        return pref.getString(MEMBER_GENDER, "1").toString()
    }

    fun setMemberGender(value: String) {
        val pref = ObserverManager.context!!.getSharedPreferences(MEMBER, Context.MODE_PRIVATE)
        val editor = pref.edit()
        editor.putString(MEMBER_GENDER, value)
        editor.apply()
    }

    fun getDbVer(): Int {
        val pref = ObserverManager.context!!.getSharedPreferences(ETC, Context.MODE_PRIVATE)
        return pref.getInt(DB_VER, 0)
    }

    fun setDbVer(value: Int) {
        val pref = ObserverManager.context!!.getSharedPreferences(ETC, Context.MODE_PRIVATE)
        val editor = pref.edit()
        editor.putInt(DB_VER, value)
        editor.apply()
    }

    fun getNoticeDate(): String {
        val pref = ObserverManager.context!!.getSharedPreferences(ETC, Context.MODE_PRIVATE)
        return pref.getString(NOTICE_DATE, "2000-01-01").toString()
    }

    fun setNoticeDate(value: String) {
        val pref = ObserverManager.context!!.getSharedPreferences(ETC, Context.MODE_PRIVATE)
        val editor = pref.edit()
        editor.putString(NOTICE_DATE, value)
        editor.apply()
    }

    fun getNoticeImage(): String {
        val pref = ObserverManager.context!!.getSharedPreferences(ETC, Context.MODE_PRIVATE)
        return pref.getString(NOTICE_IMAGE, "").toString()
    }

    fun setNoticeImage(value: String) {
        val pref = ObserverManager.context!!.getSharedPreferences(ETC, Context.MODE_PRIVATE)
        val editor = pref.edit()
        editor.putString(NOTICE_IMAGE, value)
        editor.apply()
    }

    fun getLatitude(): Double {
        val pref = ObserverManager.context!!.getSharedPreferences(ETC, Context.MODE_PRIVATE)
        return pref.getString(LATITUDE, "0.0").toString().toDouble()
    }

    fun setLatitude(value: Double) {
        val pref = ObserverManager.context!!.getSharedPreferences(ETC, Context.MODE_PRIVATE)
        val editor = pref.edit()
        editor.putString(LATITUDE, value.toString())
        editor.apply()
    }

    fun getLongitude(): Double {
        val pref = ObserverManager.context!!.getSharedPreferences(ETC, Context.MODE_PRIVATE)
        return pref.getString(LONGITUDE, "0.0").toString().toDouble()
    }

    fun setLongitude(value: Double) {
        val pref = ObserverManager.context!!.getSharedPreferences(ETC, Context.MODE_PRIVATE)
        val editor = pref.edit()
        editor.putString(LONGITUDE, value.toString())
        editor.apply()
    }

    fun isPush(): Boolean {
        val pref = ObserverManager.context!!.getSharedPreferences(ETC, Context.MODE_PRIVATE)
        return pref.getBoolean(PUSH, true)
    }

    fun setPush(value: Boolean) {
        val pref = ObserverManager.context!!.getSharedPreferences(ETC, Context.MODE_PRIVATE)
        val editor = pref.edit()
        editor.putBoolean(PUSH, value)
        editor.apply()
    }

    fun isFirstCheck(): Boolean {
        val pref = ObserverManager.context!!.getSharedPreferences(ETC, Context.MODE_PRIVATE)
        return pref.getBoolean(FIRST_CHECK, false)
    }

    fun setFirstCheck(value: Boolean) {
        val pref = ObserverManager.context!!.getSharedPreferences(ETC, Context.MODE_PRIVATE)
        val editor = pref.edit()
        editor.putBoolean(FIRST_CHECK, value)
        editor.apply()
    }

}