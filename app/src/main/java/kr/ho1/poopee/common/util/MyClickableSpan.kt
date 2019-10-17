package kr.ho1.poopee.common.util

import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View

internal open class MyClickableSpan : ClickableSpan() {
    override fun onClick(p0: View) {

    }

    override fun updateDrawState(ds: TextPaint) {// override updateDrawState
        ds.isUnderlineText = false // set to false to remove underline
    }

}