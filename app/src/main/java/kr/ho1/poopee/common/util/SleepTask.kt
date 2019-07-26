package kr.ho1.poopee.common.util

import android.os.AsyncTask

class SleepTask() : AsyncTask<String, String, String>() {
    private lateinit var onFinish: (() -> Unit)
    private var millis: Int = 0

    constructor(millis: Int, onFinish: (() -> Unit)) : this() {
        this.millis = millis
        this.onFinish = onFinish
    }

    override fun doInBackground(vararg params: String): String {
        try {
            Thread.sleep(millis.toLong())
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        return ""
    }

    override fun onPostExecute(result: String) {
        onFinish()
    }

}