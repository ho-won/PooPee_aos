package kr.co.ho1.poopee.common.util

import android.graphics.Canvas
import android.graphics.Paint
import android.text.style.ReplacementSpan

class CustomBackgroundSpan(private val backgroundColor: Int) : ReplacementSpan() {

    override fun getSize(
        paint: Paint,
        text: CharSequence,
        start: Int,
        end: Int,
        fm: Paint.FontMetricsInt?
    ): Int {
        return paint.measureText(text, start, end).toInt()
    }

    override fun draw(
        canvas: Canvas,
        text: CharSequence,
        start: Int,
        end: Int,
        x: Float,
        top: Int,
        y: Int,
        bottom: Int,
        paint: Paint
    ) {
        // 원래 텍스트 페인트 정보 저장
        val originalColor = paint.color

        // 텍스트 높이 계산
        val textHeight = bottom - top
        val thirdHeight = textHeight / 3

        // 배경 그리기 (아래쪽 1/3만)
        paint.color = backgroundColor
        canvas.drawRect(
            x, bottom - thirdHeight.toFloat(), // 시작 X, Y
            x + paint.measureText(text, start, end), bottom.toFloat(), // 끝 X, Y
            paint
        )

        // 원래 색상으로 돌리고 텍스트 그리기
        paint.color = originalColor
        canvas.drawText(text, start, end, x, y.toFloat(), paint)
    }
}
