package com.nwm2

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.nwm2.databinding.ActivityMainBinding
import com.nwm2.databinding.FactorViewBinding
import com.nwm2.service.Nwm

class MainActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMainBinding
    private lateinit var calculateButton: Button
    private lateinit var linearLayout: LinearLayout
    private lateinit var addFactorButton: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        // calculate button
        calculateButton = binding.calculateButton
        // calculate LCD and binding to resultTextView
        calculateButton.setOnClickListener {
            binding.resultTextView.text = calculateNWM()
        }
        addFactorButton = binding.addFactorButton
        linearLayout = binding.linearLayout
        // add two first FactorView layouts with no delete button, need at least two to calculate
        addFactorViewToLinearLayout(false)
        addFactorViewToLinearLayout(false)
        // add additional FactorView layouts with delete button
        addFactorButton.setOnClickListener {
            addFactorViewToLinearLayout(true)
        }
    }

    private fun addFactorViewToLinearLayout(activateDelete: Boolean) {
        val bindingFactorViewBinding = FactorViewBinding.inflate(layoutInflater)
        val deleteButton = bindingFactorViewBinding.deleteButton
        val addedFactorsQuantity = linearLayout.childCount
        val maxAddedFactorQuantity = 7
        // delete button with clickListener to remove FactorView layout
        if (activateDelete) {
            deleteButton.setOnClickListener {
                linearLayout.removeView(bindingFactorViewBinding.root)
            }
        } else {
            // first two FactorView layouts with delete button not visible
            deleteButton.alpha = 0F
        }
        // adding layout to linear layout, max 7
        if (addedFactorsQuantity < maxAddedFactorQuantity) {
            linearLayout.addView(bindingFactorViewBinding.root)
        } else {
            displayToast("Nie mogę dodać więcej współczynników.")
        }
        setFactorLayoutListeners(bindingFactorViewBinding)
    }

    // set listeners to plus and minus buttons for increment and decrement values
    private fun setFactorLayoutListeners(layout: FactorViewBinding) {
        setListeners(layout)
        layout.plusButton.setOnClickListener {
            increment(layout)
            hideKeyboard()

        }
        layout.minusButton.setOnClickListener {
            decrement(layout)
            hideKeyboard()
        }
    }

    // set listeners for factor InputTextView
    private fun setListeners(layout: FactorViewBinding) {
        layout.factor.doAfterTextChanged {
            // if empty disable minusButton, error message on toast
            if (it.toString() == "") {
                layout.minusButton.setOnClickListener(null)
                calculateButton.setOnClickListener {
                    displayToast("Nie mogę obliczyć z niczego!")
                }
            } else {
                // if ok set listeners to minusButton and calculateButton
                layout.minusButton.setOnClickListener { decrement(layout) }
                calculateButton.setOnClickListener {
                    binding.resultTextView.text = calculateNWM() }
            }
        }
    }

    // use the service Object to calculate LCD
    private fun calculateNWM(): String {
        // for collect all factors values
        val factors = mutableListOf<Int>()
        // getting all layouts from main linearLayout and adding them to List
        val childCount = linearLayout.childCount

        for (i in 0 until childCount) {
            val innerLinearLayout = linearLayout.getChildAt(i) as LinearLayout
            val factorValue = innerLinearLayout.getChildAt(1) as TextView
            factors.add(factorValue.text.toString().toInt())
        }
        hideKeyboard()
        return Nwm.calculate(factors).toString()
    }

    // increment factor value by 1 if empty set value to 1 and setting value to the factor view
    private fun increment(layout: FactorViewBinding) {
        val valueString = layout.factor.text.toString()
        var value = 1
        if (valueString != "") {
            value = valueString.toInt() + 1
        }
        layout.factor.text = Editable.Factory.getInstance().newEditable(value.toString())
    }

    // decrement factor value by 1 but not less than 1 and setting value to the factor view
    private fun decrement(layout: FactorViewBinding) {
        var value = layout.factor.text.toString().toInt()
        value -= if (value == 1) 0 else 1
        layout.factor.text = Editable.Factory.getInstance().newEditable(value.toString())
    }

    private fun displayToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun hideKeyboard() {
        // Check if no view has focus:
        val view: View? = this.currentFocus
        if (view != null) {
            val inputManager: InputMethodManager =
                this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }
}
