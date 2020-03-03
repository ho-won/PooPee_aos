package kr.co.ho1.poopee.common.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kr.co.ho1.poopee.common.MainActivity
import kr.co.ho1.poopee.common.base.BaseApp

class RestartBroadCastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        // 앱 재시작
        context!!.startActivity(Intent(context, MainActivity::class.java)
                .setAction(BaseApp.ACTION_RESTART)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_USER_ACTION)
        )
    }

}