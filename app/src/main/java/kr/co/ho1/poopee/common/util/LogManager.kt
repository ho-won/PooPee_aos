package kr.co.ho1.poopee.common.util

import android.util.Log
import kr.co.ho1.poopee.common.ObserverManager

object LogManager {
    private const val TAG = "HO1_TEST"

    fun e(message: Any) {
        if (ObserverManager.isShowLog) {
            Log.e(TAG, message.toString())
        }
    }

    fun e(tag: String, message: String) {
        if (ObserverManager.isShowLog) {
            Log.e(tag, message)
        }
    }

}