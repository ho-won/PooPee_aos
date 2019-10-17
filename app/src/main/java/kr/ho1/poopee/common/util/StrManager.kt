package kr.ho1.poopee.common.util

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.TextView
import android.widget.Toast
import kr.ho1.poopee.R
import kr.ho1.poopee.common.ObserverManager
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object StrManager {

    fun getComma(str: String): String {
        return if (str == "") {
            str
        } else String.format("%,d", Integer.parseInt(str))
    }

    @SuppressLint("SimpleDateFormat")
    fun getDateTime(str: String): String {
        var result = str
        try {
            val date = SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(str)
            result = SimpleDateFormat("yyyy.MM.dd hh:mm").format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return result
    }

    @SuppressLint("SimpleDateFormat")
    fun getCurrentDate(): String {
        val date = Date(System.currentTimeMillis())
        return (SimpleDateFormat("yyyy-MM-dd")).format(date)
    }

    @SuppressLint("SetTextI18n")
    fun setAddressCopySpan(tv_address: TextView, addressText: String) {
        tv_address.movementMethod = LinkMovementMethod.getInstance()
        tv_address.text = "$addressText #IMAGE"

        val span = SpannableString(tv_address.text.toString())
        val icon = ObserverManager.context!!.resources.getDrawable(R.drawable.ic_copy)
        icon.setBounds(0, 0, MyUtil.dpToPx(12), MyUtil.dpToPx(14))
        val image = VerticalImageSpan(icon)
        span.setSpan(image, addressText.length + 1, addressText.length + 7, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)

        val copyClickSpan = object : MyClickableSpan() {
            override fun onClick(p0: View) {
                try {
                    val clipboard = ObserverManager.context!!.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
                    val clip = ClipData.newPlainText("PooPee", tv_address.text)
                    clipboard!!.primaryClip = clip
                    Toast.makeText(ObserverManager.context!!, ObserverManager.context!!.resources.getString(R.string.toast_copy_complete), Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        span.setSpan(copyClickSpan, addressText.length + 1, tv_address.text.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        tv_address.text = span
    }

}