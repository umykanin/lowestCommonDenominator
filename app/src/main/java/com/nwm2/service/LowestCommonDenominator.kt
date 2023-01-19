package com.nwm2.service

import kotlin.math.pow

object LowestCommonDenominator {

    private lateinit var mainMap: MutableMap<Int, Int>

    fun calculate(list: MutableList<Int>): Long {
        mainMap = mutableMapOf()
        var result: Long = 1L
        list.forEach {
            factorize(it)
        }
        for (n in mainMap) {
            result *= (n.key.toDouble().pow(n.value.toDouble())).toInt()
        }
        mainMap = mutableMapOf()
        return result
    }

    private fun factorize(number: Int) {
        if (number < 2) return
        var numberCalc = number
        var factor = 2
        var index = 0
        var exit = false
        while (!exit) {
            if (numberCalc % factor == 0) {
                index++
                numberCalc /= factor
            } else {
                if (index > 0) {
                    if (mainMap.containsKey(factor)) {
                        if (mainMap[factor]!! < index) {
                            mainMap[factor] = index
                        }
                    } else {
                        mainMap[factor] = index
                    }
                    if (numberCalc == 1) {
                        exit = true
                    }
                    index = 0
                }
                factor++
            }
        }
    }
}
