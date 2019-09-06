package kr.ho1.poopee.common.util

import java.net.URI
import java.net.URL

object PicassoManager {
    fun getImageUrl(urlStr: String): String {
        return if (urlStr.trim { it <= ' ' }.isEmpty()) {
            "no_image"
        } else {
            try {
                val url = URL(urlStr)
                val uri = URI(url.protocol, url.userInfo, url.host, url.port, url.path, url.query, url.ref)
                uri.toString()
            } catch (e: Exception) {
                urlStr
            }
        }
    }
}