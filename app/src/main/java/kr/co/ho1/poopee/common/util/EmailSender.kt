package kr.co.ho1.poopee.common.util

import android.content.Intent
import kr.co.ho1.poopee.R
import kr.co.ho1.poopee.common.ObserverManager

object EmailSender {

    fun send(email: String, subject: String) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.flags = Intent.FLAG_ACTIVITY_NO_USER_ACTION or Intent.FLAG_ACTIVITY_NEW_TASK
        intent.type = "plain/text"
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
        intent.putExtra(Intent.EXTRA_SUBJECT, subject)
        try {
            ObserverManager.root!!.startActivity(Intent.createChooser(intent, ObserverManager.context!!.getString(R.string.send_mail)))
        } catch (e: android.content.ActivityNotFoundException) {
            e.printStackTrace()
        }

    }

}