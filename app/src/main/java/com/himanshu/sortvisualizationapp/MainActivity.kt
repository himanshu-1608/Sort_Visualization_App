package com.himanshu.sortvisualizationapp

import android.content.res.Resources
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.coroutines.*
import java.util.*
import kotlin.collections.ArrayList

class Randomiser {
    companion object {
        fun doRandom(count: Int): ArrayList<Int> {
            val list = ArrayList<Int>()
            val ans = ArrayList<Int>()
            for (i in 1..count) {
                list.add(i)
            }
            val random = Random()
            for (i in 0 until count) {
                val rem = random.nextInt(list.size)
                ans.add(list[rem])
                list.removeAt(rem)
            }
            return ans
        }
    }
}

class SecondActivity : AppCompatActivity() {

    private var isSorting = false
    private var dHeight = 0
    private var dWidth = 0
    private var count = 0
    private var sleepTime = 0
    private lateinit var sortType: String
    private lateinit var listView: ArrayList<View>
    private lateinit var listInt: ArrayList<Int>

    private lateinit var btnStart: Button
    private lateinit var etCount: EditText
    private lateinit var spType: Spinner
    private lateinit var spDesign: Spinner
    private lateinit var linearLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        linearLayout = findViewById(R.id.linearLayout)
        btnStart = findViewById(R.id.btnStart)
        etCount = findViewById(R.id.etCount)
        spType = findViewById(R.id.spType)
        spDesign = findViewById(R.id.spDesign)

        getDimensions()
        setSpinners()

        btnStart.setOnClickListener {
            if(isSorting) {
                Toast.makeText(this,"A sorting algorithm is working right now",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            count = etCount.text.toString().trim().toInt()
            if(count !in 10..400){
                Toast.makeText(this,"Enter list size between 5-400",Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            isSorting = true
            btnStart.isClickable = false
            sleepTime = if(sortType=="Bubble Sort") {
                when (count) {
                    in 1..100 -> 10000 / ((count * (count + 1)) / 2)
                    else -> 0
                }
            } else {
                when (count) {
                    in 5..50 -> 150
                    in 51..100 -> 55
                    in 101..300 -> 25
                    else -> 10
                }
            }
            listInt = Randomiser.doRandom(count)

            listView = ArrayList()
            for (i in 0 until count) {
                listView.add(makeView(i))
            }

            linearLayout.removeAllViews()
            for (i in 0 until count) {
                linearLayout.addView(listView[i])
            }
            linearLayout.invalidate()
            when (sortType) {
                "Bubble Sort" -> doBubbleSorting()
                "Merge Sort" -> doMergeSorting()
            }
            btnStart.isClickable = true
        }
    }

    private fun setSpinners() {
        val adapterType:ArrayAdapter<CharSequence> =
            ArrayAdapter.createFromResource(this,R.array.typeOfSort,android.R.layout.simple_spinner_item)
        adapterType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spType.adapter = adapterType

        spType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) { sortType = "Bubble Sort" }
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long ) { sortType = parent?.getItemAtPosition(position).toString() }
        }

        val adapterDesign:ArrayAdapter<CharSequence> =
            ArrayAdapter.createFromResource(this,R.array.typeOfGraph,android.R.layout.simple_spinner_item)
        adapterType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spDesign.adapter = adapterDesign

        spDesign.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                linearLayout.setHorizontalGravity(Gravity.CENTER)
                linearLayout.setVerticalGravity(Gravity.BOTTOM)
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when(parent?.getItemAtPosition(position).toString()) {
                    "Bar Graph Design" -> {
                        linearLayout.setHorizontalGravity(Gravity.CENTER)
                        linearLayout.setVerticalGravity(Gravity.BOTTOM)
                    }
                    else -> { linearLayout.gravity = Gravity.CENTER }
                }
            }
        }
    }

    private fun makeView(index: Int): View {
        val view = View(this@SecondActivity)
        view.setBackgroundColor(ContextCompat.getColor(this@SecondActivity, R.color.colorAccent))
        val llp = LinearLayout.LayoutParams((dWidth / count), (dHeight * listInt[index] / count))
        view.layoutParams = llp
        return view
    }

    private fun makeViewByHeight(height: Int): View {
        val view = View(this@SecondActivity)
        view.setBackgroundColor(ContextCompat.getColor(this@SecondActivity, R.color.colorAccent))
        val llp = LinearLayout.LayoutParams((dWidth / count),height)
        view.layoutParams = llp
        return view
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
        dHeight = displayMetrics.heightPixels - mC(200f)
        dWidth = displayMetrics.widthPixels - mC(30f)
    }

    private fun doBubbleSorting() {
        GlobalScope.launch(Dispatchers.IO) {
            var tempInt: Int
            var tempView: View
            for (i in 0 until count) {
                for (j in 0 until count - i - 1) {
                    if (listInt[j] > listInt[j + 1]) {
                        withContext(Dispatchers.Main) {
                            linearLayout.getChildAt(j)
                                .setBackgroundColor(
                                    ContextCompat.getColor(
                                        this@SecondActivity,
                                        R.color.colorRed
                                    )
                                )
                            linearLayout.getChildAt(j + 1)
                                .setBackgroundColor(
                                    ContextCompat.getColor(
                                        this@SecondActivity,
                                        R.color.colorRed
                                    )
                                )
                            linearLayout.invalidate()
                        }
                        Thread.sleep(sleepTime.toLong())
                        tempInt = listInt[j]
                        listInt[j] = listInt[j + 1]
                        listInt[j + 1] = tempInt
                        withContext(Dispatchers.Main) {
                            tempView = listView[j]
                            listView[j] = listView[j + 1]
                            listView[j + 1] = tempView
                            listView[j]
                                .setBackgroundColor(
                                    ContextCompat.getColor(
                                        this@SecondActivity,
                                        R.color.colorAccent
                                    )
                                )
                            listView[j + 1]
                                .setBackgroundColor(
                                    ContextCompat.getColor(
                                        this@SecondActivity,
                                        R.color.colorAccent
                                    )
                                )
                            linearLayout.removeAllViews()
                            (0 until count).forEach { i ->
                                linearLayout.addView(listView[i])
                            }
                            linearLayout.invalidate()
                        }
                    }
                }
            }
            isSorting = false
        }
    }

    private fun doMergeSorting() {
        GlobalScope.launch(Dispatchers.IO) {
            mergeSort(0, count - 1)
            isSorting = false
        }
    }

    private suspend fun mergeSort(l: Int, r: Int) {
        if (l < r) {
            val m = l + (r - l) / 2
            mergeSort(l, m)
            mergeSort(m + 1, r)
            merge(l, m, r)
        }
    }

    private suspend fun merge(l: Int, m: Int, r: Int) {
        val n1 = m-l+1
        val n2 = r-m
        val lis1 = ArrayList<Int>()
        val lis2 = ArrayList<Int>()
        val view1 = ArrayList<View>()
        val view2 = ArrayList<View>()
        for(i in 0 until n1) {
            lis1.add(listInt[l+i])
            view1.add(listView[l+i])
            withContext(Dispatchers.Main) {
                linearLayout.getChildAt(l+i)
                    .setBackgroundColor(ContextCompat.getColor(this@SecondActivity,R.color.colorRed))
                linearLayout.invalidate()
            }
            Thread.sleep(sleepTime.toLong())
            withContext(Dispatchers.Main) {
                linearLayout.getChildAt(l+i)
                    .setBackgroundColor(ContextCompat.getColor(this@SecondActivity,R.color.colorAccent))
                linearLayout.invalidate()
            }
        }
        for(j in 0 until n2) {
            lis2.add(listInt[m+1+j])
            view2.add(listView[m+1+j])
            withContext(Dispatchers.Main) {
                linearLayout.getChildAt(m+1+j)
                    .setBackgroundColor(ContextCompat.getColor(this@SecondActivity,R.color.colorRed))
                linearLayout.invalidate()
            }
            Thread.sleep(sleepTime.toLong())
            withContext(Dispatchers.Main) {
                linearLayout.getChildAt(m+1+j)
                    .setBackgroundColor(ContextCompat.getColor(this@SecondActivity,R.color.colorAccent))
                linearLayout.invalidate()
            }
        }
        var i = 0
        var j = 0
        var k = l
        while(i<n1 && j<n2) {
            if(lis1[i]< lis2[j]) {
                listInt[k] = lis1[i]
                listView[k] = view1[i]
                i++
            } else {
                listInt[k] = lis2[j]
                listView[k] = view2[j]
                j++
            }
            k++
        }

        while(i<n1) {
            listInt[k] = lis1[i]
            listView[k] = view1[i]
            k++
            i++
        }

        while(j<n2) {
            listInt[k] = lis2[j]
            listView[k] = view2[j]
            k++
            j++
        }

        withContext(Dispatchers.Main) {
            linearLayout.removeAllViews()
            (0 until count).forEach { i ->
                linearLayout.addView(makeViewByHeight(listView[i].height))
            }
            linearLayout.invalidate()
        }

    }
}