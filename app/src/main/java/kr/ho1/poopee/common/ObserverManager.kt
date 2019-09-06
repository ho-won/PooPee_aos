package kr.ho1.poopee.common

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Environment
import kr.ho1.poopee.common.base.BaseActivity
import kr.ho1.poopee.common.data.SharedManager
import java.io.File

@SuppressLint("StaticFieldLeak")
object ObserverManager {
    var testServer = false // 테스트용인지 체크
    var showLog = true // Log 노출여부 체크
    var serverException = false // Exception 통합관리 및 서버로 보낼지 체크

    var root: BaseActivity? = null  // 현재 Activity
    var context: Context? = null

    fun getPath(): String {
        return Environment.getExternalStorageDirectory().toString() + File.separator + "PooPee" + File.separator
    }

    fun logout() {
        SharedManager.setLoginCheck(false)
        SharedManager.setMemberId("")
        SharedManager.setMemberUsername("")
        SharedManager.setMemberPassword("")
        SharedManager.setMemberName("")
        SharedManager.setMemberGender("1")
    }

    fun restart() {
        context!!.startActivity(Intent(context, MainActivity::class.java)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_USER_ACTION)
        )
        root!!.finish()
    }

}