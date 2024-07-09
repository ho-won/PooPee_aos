package kr.co.ho1.poopee.common.base

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.multidex.MultiDexApplication
import com.kakao.sdk.common.KakaoSdk
import com.kakao.vectormap.KakaoMapSdk
import kr.co.ho1.poopee.common.ObserverManager
import kotlin.system.exitProcess

class BaseApp : MultiDexApplication() {
    companion object {
        const val ACTION_EXIT = "kr.co.ho1.poopee.EXIT"
        const val ACTION_RESTART = "kr.co.ho1.poopee.RESTART"
        var APP_VERSION = "0" // 앱 버전
        var APP_STATUS = AppStatus.FOREGROUND // 앱 BACKGROUND, RETURNED_TO_FOREGROUND, FOREGROUND 상태
    }

    override fun onCreate() {
        super.onCreate()

        ObserverManager.context = this // context 저장
        KakaoSdk.init(this, "53565fe7d8355fff626939459a32e7d0")
        KakaoMapSdk.init(this, "53565fe7d8355fff626939459a32e7d0")

        // 앱버전체크
        try {
            val packageInfo = packageManager.getPackageInfo(packageName, 0)
            APP_VERSION = packageInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }


        // Exception 발생하면 앱 종료
        if (ObserverManager.serverException) {
            Thread.setDefaultUncaughtExceptionHandler { _, _ ->
                ActivityCompat.finishAffinity(ObserverManager.root!!)
                exitProcess(0)
            }
        }
    }

    enum class AppStatus {
        BACKGROUND,
        RETURNED_TO_FOREGROUND,
        FOREGROUND
    }

}
