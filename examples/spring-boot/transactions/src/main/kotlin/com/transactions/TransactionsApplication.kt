package com.transactions

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TransactionsApplication

fun main(args: Array<String>) {
	runApplication<TransactionsApplication>(*args)
}
