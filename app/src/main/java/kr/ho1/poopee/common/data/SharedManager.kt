package kr.ho1.poopee.common.data

import android.content.Context
import kr.ho1.poopee.common.ObserverManager

object SharedManager {
    private const val ANDROID = "ANDROID"
    private const val ANDROID_KEY = "ANDROID_KEY" // 기기키

    private const val MEMBER = "MEMBER"
    private const val MEMBER_ID = "MEMBER_ID" // 멤버 아이디
    private const val MEMBER_USER_ID = "MEMBER_USER_ID" // 유저 아이디
    private const val MEMBER_USER_PW = "MEMBER_USER_PW" // 유저 비밀번호
    private const val MEMBER_NAME = "MEMBER_NAME" // 유저 닉네임
    private const val MEMBER_GENDER = "MEMBER_GENDER" // 유저 성별

    fun getAndroidKey(): String {
        val pref = ObserverManager.context!!.getSharedPreferences(ANDROID, Context.MODE_PRIVATE)
        return pref.getString(ANDROID_KEY, "")
    }

    fun setAndroidKey(value: String) {
        val pref = ObserverManager.context!!.getSharedPreferences(ANDROID, Context.MODE_PRIVATE)
        val editor = pref.edit()
        editor.putString(ANDROID_KEY, value)
        editor.apply()
    }

}