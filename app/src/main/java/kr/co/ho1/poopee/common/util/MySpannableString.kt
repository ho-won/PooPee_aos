package kr.co.ho1.poopee.common.util

import android.graphics.Color
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan

class MySpannableString(private val str: String) {
    private var boldString: Array<String> = arrayOf()

    private var lineString: Array<String> = arrayOf()

    private var sizeString: Array<String> = arrayOf()
    private var size: Int = 0

    private var colorString: Array<String> = arrayOf()
    private var color: String = "#000000"

    fun setBold(boldString: Array<String> = arrayOf()) {
        this.boldString = boldString
    }

    fun setLine(lineString: Array<String> = arrayOf()) {
        this.lineString = lineString
    }

    fun setSize(sizeString: Array<String> = arrayOf(), size: Int = 0) {
        this.sizeString = sizeString
        this.size = size
    }

    fun setColor(colorString: Array<String> = arrayOf(), color: String = "#000000") {
        this.colorString = colorString
        this.color = color
    }

    fun getSpannableString(): SpannableStringBuilder {
        val ssb = SpannableStringBuilder(str)

        boldString.forEach {
            ssb.setSpan(StyleSpan(Typeface.BOLD), str.indexOf(it), str.indexOf(it) + it.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        lineString.forEach {
            ssb.setSpan(UnderlineSpan(), str.indexOf(it), str.indexOf(it) + it.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        sizeString.forEach {
            ssb.setSpan(AbsoluteSizeSpan(MyUtil.dpToPx(size)), str.indexOf(it), str.indexOf(it) + it.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        colorString.forEach {
            ssb.setSpan(ForegroundColorSpan(Color.parseColor(color)), str.indexOf(it), str.indexOf(it) + it.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        return ssb
    }

}