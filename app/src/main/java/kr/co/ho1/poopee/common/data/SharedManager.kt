package kr.co.ho1.poopee.common.data

import android.content.Context
import android.content.SharedPreferences
import kr.co.ho1.poopee.common.ObserverManager
import androidx.core.content.edit

object SharedManager {
    private const val PREF_LOGIN = "LOGIN"
    private const val PREF_MEMBER = "MEMBER"
    private const val PREF_ETC = "ETC"

    private val loginPrefs: SharedPreferences by lazy {
        ObserverManager.context!!.getSharedPreferences(PREF_LOGIN, Context.MODE_PRIVATE)
    }

    private val memberPrefs: SharedPreferences by lazy {
        ObserverManager.context!!.getSharedPreferences(PREF_MEMBER, Context.MODE_PRIVATE)
    }

    private val etcPrefs: SharedPreferences by lazy {
        ObserverManager.context!!.getSharedPreferences(PREF_ETC, Context.MODE_PRIVATE)
    }

    // 로그인 체크 여부
    var isLoginCheck: Boolean
        get() = loginPrefs.getBoolean("LOGIN_CHECK", false)
        set(value) = loginPrefs.edit { putBoolean("LOGIN_CHECK", value) }

    // FCM Push 키
    var fcmKey: String
        get() = loginPrefs.getString("FCM_KEY", "").orEmpty()
        set(value) = loginPrefs.edit { putString("FCM_KEY", value) }

    // 멤버 ID
    var memberId: String
        get() = memberPrefs.getString("MEMBER_ID", "").orEmpty()
        set(value) = memberPrefs.edit { putString("MEMBER_ID", value) }

    // 유저 아이디 (Username)
    var memberUsername: String
        get() = memberPrefs.getString("MEMBER_USERNAME", "").orEmpty()
        set(value) = memberPrefs.edit { putString("MEMBER_USERNAME", value) }

    // 유저 비밀번호
    var memberPassword: String
        get() = memberPrefs.getString("MEMBER_PASSWORD", "").orEmpty()
        set(value) = memberPrefs.edit { putString("MEMBER_PASSWORD", value) }

    // 유저 닉네임
    var memberName: String
        get() = memberPrefs.getString("MEMBER_NAME", "").orEmpty()
        set(value) = memberPrefs.edit { putString("MEMBER_NAME", value) }

    // 유저 성별 (0: 남자, 1: 여자)
    var memberGender: String
        get() = memberPrefs.getString("MEMBER_GENDER", "1").orEmpty()
        set(value) = memberPrefs.edit { putString("MEMBER_GENDER", value) }

    // Toilet DB 버전
    var dbVer: Int
        get() = etcPrefs.getInt("DB_VER", 0)
        set(value) = etcPrefs.edit { putInt("DB_VER", value) }

    // 서버 공지 다시보지 않기 체크 날짜 (Y-m-d)
    var noticeDate: String
        get() = etcPrefs.getString("NOTICE_DATE", "2000-01-01").orEmpty()
        set(value) = etcPrefs.edit { putString("NOTICE_DATE", value) }

    // 서버 공지 이미지
    var noticeImage: String
        get() = etcPrefs.getString("NOTICE_IMAGE", "").orEmpty()
        set(value) = etcPrefs.edit { putString("NOTICE_IMAGE", value) }

    // 현재 위도
    var latitude: Double
        get() = etcPrefs.getString("LATITUDE", "0.0").orEmpty().toDouble()
        set(value) = etcPrefs.edit { putString("LATITUDE", value.toString()) }

    // 현재 경도
    var longitude: Double
        get() = etcPrefs.getString("LONGITUDE", "0.0").orEmpty().toDouble()
        set(value) = etcPrefs.edit { putString("LONGITUDE", value.toString()) }

    // 푸시 알림 설정 여부
    var isPush: Boolean
        get() = etcPrefs.getBoolean("PUSH", true)
        set(value) = etcPrefs.edit { putBoolean("PUSH", value) }

    // 앱 최초 실행 여부 체크
    var isFirstCheck: Boolean
        get() = etcPrefs.getBoolean("FIRST_CHECK", false)
        set(value) = etcPrefs.edit { putBoolean("FIRST_CHECK", value) }

    // 리뷰 팝업 카운트 (조건 체크용)
    var reviewCount: Int
        get() = etcPrefs.getInt("REVIEW_COUNT", 0)
        set(value) = etcPrefs.edit { putInt("REVIEW_COUNT", value) }

    // 리워드
    var rewardEarnedTime: Long
        get() = etcPrefs.getLong("REWORD_EARNED_TIME", 0)
        set(value) = etcPrefs.edit { putLong("REWORD_EARNED_TIME", value) }
}
