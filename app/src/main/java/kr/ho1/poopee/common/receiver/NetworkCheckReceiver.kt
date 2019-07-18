package kr.ho1.poopee.common.receiver

import android.annotation.TargetApi
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Build
import kr.ho1.poopee.common.ObserverManager

class NetworkCheckReceiver : BroadcastReceiver() {
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent!!.action

        if (action == ConnectivityManager.CONNECTIVITY_ACTION) {
            try {
                val connectivityManager = context!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val activeNetInfo = connectivityManager.activeNetworkInfo

                if (activeNetInfo != null) {
                    // wifi, 3g 둘 중 하나라도 있을 경우
                    if (ObserverManager.root != null) {
                        ObserverManager.root!!.onNetworkChanged()
                    }
                } else {
                    // wifi, 3g 둘 다 없을 경우
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }
}