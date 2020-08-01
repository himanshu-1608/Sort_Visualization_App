package com.himanshu.sortvisualizationapp

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import androidx.core.content.ContextCompat

class BarChart(context: Context, attributes: AttributeSet) : View(context, attributes) {

    data class Bar(val value: Int = 0, var level: Int = 0) :
        Comparable<Bar> {

        companion object {
            const val ORDINARY = 0
            const val HIGH_1 = 1
            const val HIGH_2 = 2
            const val SECONDARY = 3

            const val RANGE_MAX = 100
        }

        val heightInPercent = value.toFloat() / RANGE_MAX

        override fun compareTo(other: Bar): Int {
            if (heightInPercent == other.heightInPercent)
                return 0

            if (heightInPercent > other.heightInPercent)
                return 1

            return -1
        }
    }

    var dataSet = emptyArray<Bar>()
        set(value) {
            val sizeChanged = value.size != field.size
            field = value
            if (sizeChanged)
                requestLayout()
            else
                postInvalidate()
        }

    private val colorArray = arrayOf(
        R.color.colorBar,
        R.color.colorBarHighlight,
        R.color.colorBarHighlight2,
        R.color.colorBarHighlight3
    )
    private val paintLevel = Array(colorArray.size) {
        Paint().apply {
            color = ContextCompat.getColor(context, colorArray[it])
            strokeWidth =
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3f, resources.displayMetrics)
            isAntiAlias = true
            style = Paint.Style.FILL_AND_STROKE
        }
    }

    private var barWidth = 0

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        barWidth = width / (dataSet.size * 2 - 1)
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.let {
            var x = 0f
            for (d in dataSet) {
                it.drawRect(
                    x,
                    height - height * d.heightInPercent,
                    x + barWidth,
                    height.toFloat(),
                    paintLevel[d.level % paintLevel.size]
                )
                x += 2 * barWidth
            }
        }
    }

}