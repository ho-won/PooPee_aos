package kr.ho1.poopee.common.data

import android.content.Context
import kr.ho1.poopee.common.ObserverManager

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
object SharedManager {
    private const val LOGIN = "LOGIN"
    private const val LOGIN_CHECK = "LOGIN_CHECK" // 로그인체크

    private const val MEMBER = "MEMBER"
    private const val MEMBER_ID = "MEMBER_ID" // 멤버 아이디
    private const val MEMBER_USER_ID = "MEMBER_USER_ID" // 유저 아이디
    private const val MEMBER_USER_PW = "MEMBER_USER_PW" // 유저 비밀번호
    private const val MEMBER_NAME = "MEMBER_NAME" // 유저 닉네임
    private const val MEMBER_GENDER = "MEMBER_GENDER" // 유저 성별 1(남자) 2(여자)

    private const val ETC = "ETC"
    private const val NOTICE_DATE = "NOTICE_DATE" // 서버공지 다시보지않기 체크시간(Y-m-d)

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

    fun getMemberId(): String {
        val pref = ObserverManager.context!!.getSharedPreferences(MEMBER, Context.MODE_PRIVATE)
        return pref.getString(MEMBER_ID, "")
    }

    fun setMemberId(value: String) {
        val pref = ObserverManager.context!!.getSharedPreferences(MEMBER, Context.MODE_PRIVATE)
        val editor = pref.edit()
        editor.putString(MEMBER_ID, value)
        editor.apply()
    }

    fun getMemberUserId(): String {
        val pref = ObserverManager.context!!.getSharedPreferences(MEMBER, Context.MODE_PRIVATE)
        return pref.getString(MEMBER_USER_ID, "")
    }

    fun setMemberUserId(value: String) {
        val pref = ObserverManager.context!!.getSharedPreferences(MEMBER, Context.MODE_PRIVATE)
        val editor = pref.edit()
        editor.putString(MEMBER_USER_ID, value)
        editor.apply()
    }

    fun getMemberUserPw(): String {
        val pref = ObserverManager.context!!.getSharedPreferences(MEMBER, Context.MODE_PRIVATE)
        return pref.getString(MEMBER_USER_PW, "")
    }

    fun setMemberUserPw(value: String) {
        val pref = ObserverManager.context!!.getSharedPreferences(MEMBER, Context.MODE_PRIVATE)
        val editor = pref.edit()
        editor.putString(MEMBER_USER_PW, value)
        editor.apply()
    }

    fun getMemberName(): String {
        val pref = ObserverManager.context!!.getSharedPreferences(MEMBER, Context.MODE_PRIVATE)
        return pref.getString(MEMBER_NAME, "")
    }

    fun setMemberName(value: String) {
        val pref = ObserverManager.context!!.getSharedPreferences(MEMBER, Context.MODE_PRIVATE)
        val editor = pref.edit()
        editor.putString(MEMBER_NAME, value)
        editor.apply()
    }

    fun getMemberGender(): String {
        val pref = ObserverManager.context!!.getSharedPreferences(MEMBER, Context.MODE_PRIVATE)
        return pref.getString(MEMBER_GENDER, "1")
    }

    fun setMemberGender(value: String) {
        val pref = ObserverManager.context!!.getSharedPreferences(MEMBER, Context.MODE_PRIVATE)
        val editor = pref.edit()
        editor.putString(MEMBER_GENDER, value)
        editor.apply()
    }

    fun getNoticeDate(): String {
        val pref = ObserverManager.context!!.getSharedPreferences(ETC, Context.MODE_PRIVATE)
        return pref.getString(NOTICE_DATE, "2000-01-01")
    }

    fun setNoticeDate(value: String) {
        val pref = ObserverManager.context!!.getSharedPreferences(ETC, Context.MODE_PRIVATE)
        val editor = pref.edit()
        editor.putString(NOTICE_DATE, value)
        editor.apply()
    }

}