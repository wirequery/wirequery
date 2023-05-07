package com.balancecalculator

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BalanceCalculatorApplication

fun main(args: Array<String>) {
	runApplication<BalanceCalculatorApplication>(*args)
}
