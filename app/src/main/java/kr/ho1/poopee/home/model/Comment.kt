package kr.ho1.poopee.home.model

class Comment {
    var comment_id: String = ""
    var member_id: String = ""
    var gender: String = "0" // 0(남자) 1(여자)
    var name: String = ""
    var content: String = ""
    var created: String = ""
    var view_type: Int = Toilet.VIEW_COMMENT
}