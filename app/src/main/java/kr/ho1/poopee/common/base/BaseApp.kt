package kr.ho1.poopee.common.base

import android.app.Activity
import android.app.Application
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.multidex.MultiDexApplication
import android.support.v4.app.ActivityCompat
import kr.ho1.poopee.common.ObserverManager
import kr.ho1.poopee.common.receiver.NetworkCheckReceiver

class BaseApp : MultiDexApplication() {
    companion object {
        const val ACTION_EXIT = "kr.co.oncar.hiautoclub.EXIT"
        const val ACTION_RESTART = "kr.co.oncar.hiautoclub.RESTART"
        var APP_VERSION = "0" // 앱 버전
        var APP_STATUS = AppStatus.FOREGROUND // 앱 BACKGROUND, RETURNED_TO_FOREGROUND, FOREGROUND 상태
    }

    override fun onCreate() {
        super.onCreate()

        ObserverManager.context = this // context 저장

        // 네트워크 변경 체크
        val receiver = NetworkCheckReceiver()
        registerReceiver(receiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))

        // 앱 BACKGROUND, RETURNED_TO_FOREGROUND, FOREGROUND 체크
        registerActivityLifecycleCallbacks(MyActivityLifecycleCallback())

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
                System.exit(0)
            }
        }
    }

    /**
     * 앱 Foreground 체크.
     */
    fun isReturnedToForeground(): Boolean {
        return APP_STATUS.ordinal == AppStatus.RETURNED_TO_FOREGROUND.ordinal
    }

    enum class AppStatus {
        BACKGROUND,
        RETURNED_TO_FOREGROUND,
        FOREGROUND
    }

    inner class MyActivityLifecycleCallback : Application.ActivityLifecycleCallbacks {
        private var running = 0

        override fun onActivityPaused(activity: Activity?) {
        }

        override fun onActivityResumed(activity: Activity?) {
        }

        override fun onActivityStarted(activity: Activity?) {
            if (++running == 1) {
                APP_STATUS = AppStatus.RETURNED_TO_FOREGROUND
            } else if (running > 1) {
                APP_STATUS = AppStatus.FOREGROUND
            }
        }

        override fun onActivityDestroyed(activity: Activity?) {
        }

        override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
        }

        override fun onActivityStopped(activity: Activity?) {
            if (--running == 0) {
                APP_STATUS = AppStatus.BACKGROUND
            }
        }

        override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
        }

    }

}