package kr.ho1.poopee.common.util

import android.annotation.SuppressLint
import java.text.ParseException
import java.text.SimpleDateFormat

object StrManager {

    fun getComma(str: String): String {
        return if (str == "") {
            str
        } else String.format("%,d", Integer.parseInt(str))
    }

    @SuppressLint("SimpleDateFormat")
    fun getDate(str: String): String {
        var result = str
        try {
            val date = SimpleDateFormat("yyyyMMdd").parse(str)
            result = SimpleDateFormat("yyyy-MM-dd").format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return result
    }

}