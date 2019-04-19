package kr.ho1.poopee.home.model

import java.io.Serializable

class Toilet : Serializable {
    companion object {
        const val VIEW_CONTENT = 1001
        const val VIEW_COMMENT = 1002
    }

    var id: String = ""
    var toilet_id: String = ""
    var title: String = ""
    var content: String = ""
    var comment_count: String = "0"
    var like_count: String = "0"
    var like_check: Boolean = false

}