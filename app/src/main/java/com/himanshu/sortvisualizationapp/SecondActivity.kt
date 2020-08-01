package com.himanshu.sortvisualizationapp

import android.content.res.Resources
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity


class SecondActivity : AppCompatActivity() {

    var dHeight = 0f
    var dWidth = 0f
    private lateinit var btnStart: Button
    private lateinit var etCount: EditText
    private lateinit var linearLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
        linearLayout = findViewById(R.id.linearLayout)
        btnStart = findViewById(R.id.btnStart)
        etCount = findViewById(R.id.etCount)
        getDimensions()


        val viewArray = ArrayList<View>()
        for(i in 0..etCount.text.toString().trim().toInt()) {
            val view = View(this)
            view.setBackgroundColor(resources.getColor(R.color.colorAccent))
            val llp = LinearLayout.LayoutParams((dWidth/100).toInt(),5*i)
            llp.setMargins(mC(0f),mC(7f),mC(0f),mC(7f))
            view.layoutParams = llp
            viewArray.add(view)
        }
        for(i in 0..etCount.text.toString().trim().toInt()) {
            linearLayout.addView(viewArray[i])
        }
    }

    private fun mC(dp: Float): Int {
        val r: Resources = this.resources
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            r.displayMetrics
        ).toInt()
    }

    private fun getDimensions() {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        dHeight = (displayMetrics.heightPixels * 0.9).toFloat()
        dWidth = displayMetrics.widthPixels.toFloat()
    }
}