package kr.co.ho1.poopee.common.util

import android.os.Handler
import android.text.InputFilter
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.widget.Toast
import kr.co.ho1.poopee.R
import kr.co.ho1.poopee.common.ObserverManager
import java.util.regex.Pattern

object StringFilter {
    private const val PATTERN_ALPHANUMERIC = "^[a-zA-Z0-9]+$"
    private const val PATTERN_ALPHANUMERIC_HANGUL = "^[a-zA-Z0-9가-힣ㄱ-ㅎㅏ-ㅣ\u318D\u119E\u11A2\u2022\u2025a\u00B7\uFE55]+$"

    private const val ALLOW_ALPHANUMERIC = 0
    private const val ALLOW_ALPHANUMERIC_HANGUL = 1
    private const val TOAST_LENGTH = 600

    fun getAlphanumeric(): Array<InputFilter> {
        return arrayOf(
                allowAlphanumeric // 영문, 숫자만 입력 가능
        )
    }

    fun getAlphanumeric(maxLength: Int): Array<InputFilter> {
        return arrayOf(InputFilter.LengthFilter(maxLength), // maxLength
                allowAlphanumeric // 영문, 숫자만 입력 가능
        )
    }

    fun getAlphanumericHangul(): Array<InputFilter> {
        return arrayOf(allowAlphanumericHangul // 한글, 영문, 숫자만 입력 가능
        )
    }

    fun getAlphanumericHangul(maxLength: Int): Array<InputFilter> {
        return arrayOf(InputFilter.LengthFilter(maxLength), // maxLength
                allowAlphanumericHangul // 한글, 영문, 숫자만 입력 가능
        )
    }

    // Allows only alphanumeric characters. Filters special and hangul
    private val allowAlphanumeric = InputFilter { source, start, end, dest, dstart, dend -> filteredString(source, start, end, ALLOW_ALPHANUMERIC) }

    // Allows only alphanumeric and hangul characters. Filters special
    private val allowAlphanumericHangul = InputFilter { source, start, end, dest, dstart, dend -> filteredString(source, start, end, ALLOW_ALPHANUMERIC_HANGUL) }

    // Returns the string result which is filtered by the given mode
    private fun filteredString(source: CharSequence, start: Int, end: Int, mode: Int): CharSequence? {
        val pattern: Pattern
        if (mode == ALLOW_ALPHANUMERIC) {
            pattern = Pattern.compile(PATTERN_ALPHANUMERIC)
        } else {
            pattern = Pattern.compile(PATTERN_ALPHANUMERIC_HANGUL)
        }

        var keepOriginal = true
        val stringBuilder = StringBuilder(end - start)
        for (i in start until end) {
            val c = source[i]
            if (pattern.matcher(Character.toString(c)).matches()) {
                stringBuilder.append(c)
            } else {
                if (mode == ALLOW_ALPHANUMERIC) {
                    showToast(ObserverManager.context!!.getResources().getString(R.string.toast_alphanumeric))
                } else {
                    showToast(ObserverManager.context!!.getResources().getString(R.string.toast_alphanumeric_hangul))
                }

                keepOriginal = false
            }
        }

        if (keepOriginal) {
            return null
        } else {
            if (source is Spanned) {
                val spannableString = SpannableString(stringBuilder)
                TextUtils.copySpansFrom(source, start, stringBuilder.length, null, spannableString, 0)
                return spannableString
            } else {
                return stringBuilder
            }
        }
    }

    // Shows toast with specify delay that is shorter than Toast.LENGTH_SHORT
    private fun showToast(msg: String) {
        val toast = Toast.makeText(ObserverManager.context!!, msg, Toast.LENGTH_SHORT)
        toast.show()

        val handler = Handler()
        handler.postDelayed({ toast.cancel() }, TOAST_LENGTH.toLong())
    }
}