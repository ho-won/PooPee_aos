package kr.ho1.poopee.common.util

import android.util.Log
import kr.ho1.poopee.common.ObserverManager

object LogManager {
    
    fun d(tag: String, message: String) {
        if (ObserverManager.showLog) {
            Log.d(tag, message)
        }
    }

    fun e(tag: String, message: String) {
        if (ObserverManager.showLog) {
            Log.e(tag, message)
        }
    }

    fun i(tag: String, message: String) {
        if (ObserverManager.showLog) {
            Log.i(tag, message)
        }
    }

    fun v(tag: String, message: String) {
        if (ObserverManager.showLog) {
            Log.v(tag, message)
        }
    }

    fun w(tag: String, message: String) {
        if (ObserverManager.showLog) {
            Log.w(tag, message)
        }
    }

}