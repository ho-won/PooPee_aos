package kr.ho1.poopee.common.util

import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import kr.ho1.poopee.common.ObserverManager

object PermissionManager {

    fun permissionCheck(permission: String): Boolean {
        val permissionCheck = ContextCompat.checkSelfPermission(ObserverManager.context!!, permission)

        return permissionCheck != PackageManager.PERMISSION_DENIED
    }

    fun requestPermissions(permissions: Array<String>, requestCode: Int) {
        ActivityCompat.requestPermissions(ObserverManager.root!!, permissions, requestCode)
    }

}