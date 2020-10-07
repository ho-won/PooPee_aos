package kr.co.ho1.poopee.home.model

import java.io.Serializable

class Toilet : Serializable {
    companion object {
        const val VIEW_CONTENT = 1001
        const val VIEW_COMMENT = 1002
    }

    var toilet_id: Int = 0
    var member_id: String = "0"
    var type: String = "" // 개방화장실, 졸음쉼터, 휴게소, 유저
    var m_name: String = "" // 유저명(유저화장실일경우)
    var name: String = "" // 화장실명
    var content: String = "" // 화장실설명
    var address_new: String = "" // 도로명주소
    var address_old: String = "" // 지번주소
    var unisex: String = "" // 남녀공용화장실여부
    var m_poo: String = "0" // 남성용-대변기수
    var m_pee: String = "0" // 남성용-소변기수
    var m_d_poo: String = "0" // 남성용-장애인용대변기수
    var m_d_pee: String = "0" // 남성용-장애인용소변기수
    var m_c_poo: String = "0" // 남성용-어린이용대변기수
    var m_c_pee: String = "0" // 남성용-어린이용소변기수
    var w_poo: String = "0" // 여성용-대변기수
    var w_d_poo: String = "0" // 여성용-장애인용대변기수
    var w_c_poo: String = "0" // 여성용-어린이용대변기수
    var manager_name: String = "" // 관리기관명
    var manager_tel: String = "" // 관리기관전화번호
    var open_time: String = "" // 개방시간
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    var comment_count: String = "0"
    var like_count: String = "0"
    var like_check: Boolean = false

}