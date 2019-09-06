package kr.ho1.poopee.common.util

import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kr.ho1.poopee.common.ObserverManager
import java.security.Permission

object PermissionManager {
    const val Permission = 9000
    const val ACCESS_FINE_LOCATION = 9001

    fun permissionCheck(permission: String): Boolean {
        val permissionCheck = ContextCompat.checkSelfPermission(ObserverManager.context!!, permission)

        return permissionCheck != PackageManager.PERMISSION_DENIED
    }

    fun requestPermissions(permissions: Array<String>, requestCode: Int) {
        ActivityCompat.requestPermissions(ObserverManager.root!!, permissions, requestCode)
    }

}