package com.himanshu.sortvisualizationapp

import android.text.InputFilter
import android.text.Spanned
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.himanshu.sortvisualizationapp.BarChart.Bar.Companion.RANGE_MAX
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

class InputFilterMinMax(
    private var min: Int,
    private var max: Int
) : InputFilter {

    override fun filter(
        source: CharSequence,
        start: Int,
        end: Int,
        dest: Spanned,
        dstart: Int,
        dend: Int
    ): CharSequence? {
        try {
            val input = (dest.toString() + source.toString()).toInt()
            if (isInRange(min, max, input)) return null
        } catch (nfe: NumberFormatException) {
        }

        return ""
    }

    private fun isInRange(a: Int, b: Int, c: Int): Boolean {
        return if (b > a) c in a..b else c in b..a
    }
}








class BarViewModel : ViewModel() {

    enum class SortingType {
        BUBBLE, MERGE, RADIX
    }

    companion object {
        const val SIZE_MIN = 10
        const val SIZE_MAX = 1000
    }

    val sortingType = arrayOf(SortingType.BUBBLE, SortingType.MERGE, SortingType.RADIX)

    private var isAnimatingGoingOn = false
    private var size: Int = 0
    private var type: SortingType = SortingType.BUBBLE
    private var delayDuration = 10L
    private lateinit var randomDataSet: Array<BarChart.Bar>

    val state = MutableLiveData<State>()

    private fun generateRandomDataSet() {
        randomDataSet =
            Array(size) { BarChart.Bar(Random.nextInt(RANGE_MAX)) }
    }

    private suspend fun startSorting() {
        when (type) {
            SortingType.BUBBLE -> startBubbleSort()
            SortingType.MERGE -> startMergeSort()
            SortingType.RADIX -> startRadixSort()
        }
    }

    private suspend fun startBubbleSort() {
        var swapped: Boolean
        for (i in 0 until randomDataSet.size - 1) {
            swapped = false
            for (j in 0 until randomDataSet.size - i - 1) {
                randomDataSet[j].level = BarChart.Bar.HIGH_2

                if (randomDataSet[j] > randomDataSet[j + 1]) {
                    randomDataSet[j + 1].level = BarChart.Bar.HIGH_2

                    val temp = randomDataSet[j]
                    randomDataSet[j] = randomDataSet[j + 1]
                    randomDataSet[j + 1] = temp
                    swapped = true
                }

                displayChange()

                randomDataSet[j].level = BarChart.Bar.ORDINARY
                randomDataSet[j + 1].level = BarChart.Bar.ORDINARY
            }

            if (swapped.not())
                break
        }
    }

    private suspend fun startMergeSort() = mergeSort(0, randomDataSet.size - 1)

    private suspend fun mergeSort(l: Int, r: Int) {
        if (l < r) {
            val m = l + (r - l) / 2
            mergeSort(l, m)
            mergeSort(m + 1, r)
            merge(l, m, r)
        }
    }

    private suspend fun merge(left: Int, middle: Int, right: Int) {
        val n1 = middle - left + 1
        val n2 = right - middle

        val leftArray = Array(n1) { BarChart.Bar() }
        val rightArray = Array(n2) { BarChart.Bar() }

        for (i in 0 until n1)
            leftArray[i] = randomDataSet[left + i]
        for (j in 0 until n2)
            rightArray[j] = randomDataSet[middle + 1 + j]

        var i = 0
        var j = 0
        var k: Int = left
        var temp: BarChart.Bar
        while (i < n1 && j < n2) {
            temp = randomDataSet[k]

            if (leftArray[i] <= rightArray[j]) {
                randomDataSet[k] = leftArray[i]
                i++
            } else {
                randomDataSet[k] = rightArray[j]
                j++
            }

            randomDataSet[k].level = BarChart.Bar.SECONDARY
            temp.level = BarChart.Bar.SECONDARY
            displayChange()
            randomDataSet[k].level = BarChart.Bar.ORDINARY
            temp.level = BarChart.Bar.ORDINARY

            k++
        }

        while (i < n1) {
            temp = randomDataSet[k]
            randomDataSet[k] = leftArray[i]

            randomDataSet[k].level = BarChart.Bar.SECONDARY
            temp.level = BarChart.Bar.SECONDARY
            displayChange()
            randomDataSet[k].level = BarChart.Bar.ORDINARY
            temp.level = BarChart.Bar.ORDINARY

            i++
            k++
        }

        while (j < n2) {
            temp = randomDataSet[k]
            randomDataSet[k] = rightArray[j]

            randomDataSet[k].level = BarChart.Bar.SECONDARY
            temp.level = BarChart.Bar.SECONDARY
            displayChange()
            randomDataSet[k].level = BarChart.Bar.ORDINARY
            temp.level = BarChart.Bar.ORDINARY

            j++
            k++
        }
    }

    private suspend fun startRadixSort() {

        val m = getMax()

        var exp = 1
        while ((m.value / exp) > 0) {
            countSort(exp)
            exp *= 10
        }
    }

    private fun getMax(): BarChart.Bar {
        var mx = randomDataSet[0]
        for (i in 1 until randomDataSet.size)
            if (randomDataSet[i] > mx)
                mx = randomDataSet[i]

        return mx
    }

    private suspend fun countSort(exp: Int) {
        val output = Array(randomDataSet.size) { BarChart.Bar(0) }

        // Store count of occurrences in count[]
        val count = Array(10) { 0 }
        for (element in randomDataSet)
            count[(element.value / exp) % 10]++


        for (i in 1 until 10)
            count[i] += count[i - 1]

        for (i in randomDataSet.size - 1 downTo 0) {
            val actualPosition = count[(randomDataSet[i].value / exp) % 10] - 1
            output[actualPosition] = randomDataSet[i]
            count[(randomDataSet[i].value / exp) % 10]--

            randomDataSet[actualPosition].level = BarChart.Bar.HIGH_1
            randomDataSet[i].level = BarChart.Bar.HIGH_1

            displayChange()

            randomDataSet[actualPosition].level = BarChart.Bar.ORDINARY
            randomDataSet[i].level = BarChart.Bar.ORDINARY
        }

        for (i in randomDataSet.indices)
            randomDataSet[i] = output[i]
    }

    private suspend fun displayChange() {
        delay(delayDuration)
        state.postValue(State.Sorting(randomDataSet))
    }

    private fun resetLevel() {
        for (i in randomDataSet)
            i.level = BarChart.Bar.ORDINARY
    }

    fun startAnimating(size: Int?, type: String?) {
        if (isAnimatingGoingOn)
            return

        this.size = size ?: SIZE_MIN
        this.type = type?.let { SortingType.valueOf(it) } ?: SortingType.BUBBLE
        this.isAnimatingGoingOn = true

        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                generateRandomDataSet()
                state.postValue(State.Sorting(randomDataSet))
                startSorting()
                resetLevel()
                state.postValue(State.Sorting(randomDataSet))
                isAnimatingGoingOn = false
            }
        }
    }
}