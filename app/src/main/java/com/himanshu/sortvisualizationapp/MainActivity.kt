package com.himanshu.sortvisualizationapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val viewModel by lazy {
        ViewModelProvider(this).get(BarViewModel::class.java) }

    private val typeAdapter by lazy {
        ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            viewModel.sortingType
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sizeEditText.filters =
            arrayOf(InputFilterMinMax(BarViewModel.SIZE_MIN, BarViewModel.SIZE_MAX))
        typeSpinner.adapter = typeAdapter

        viewModel.state.observe(this, Observer {
            if (it is State.Sorting) {
                barChart.dataSet = it.dataset
            }
        })

        startBtn.setOnClickListener {
            viewModel.startAnimating(
                sizeEditText.text?.toString()?.toInt(),
                typeSpinner.selectedItem?.toString()
            )
        }
    }
}