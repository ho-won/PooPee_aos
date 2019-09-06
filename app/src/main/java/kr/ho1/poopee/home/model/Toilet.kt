package kr.ho1.poopee.home.model

import java.io.Serializable

class Toilet : Serializable {
    companion object {
        const val VIEW_CONTENT = 1001
        const val VIEW_COMMENT = 1002
    }

    var toilet_id: Int = 0
    var type: String = ""
    var name: String = ""
    var address_new: String = ""
    var address_old: String = ""
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    var comment_count: String = "0"
    var like_count: String = "0"
    var like_check: Boolean = false

}