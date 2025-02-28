package kr.co.ho1.poopee.common.util

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kr.co.ho1.poopee.common.ObserverManager

class SleepTask() {
    private lateinit var onFinish: (() -> Unit)
    private var millis: Int = 0

    constructor(millis: Int, onFinish: (() -> Unit)) : this() {
        this.millis = millis
        this.onFinish = onFinish

        GlobalScope.launch {
            try {
                Thread.sleep(millis.toLong())
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            ObserverManager.root?.runOnUiThread {
                onFinish()
            }
        }
    }

}