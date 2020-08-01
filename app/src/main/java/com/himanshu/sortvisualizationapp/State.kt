package com.himanshu.sortvisualizationapp

sealed class State {
    data class Sorting(val dataset: Array<BarChart.Bar>) : State()
}