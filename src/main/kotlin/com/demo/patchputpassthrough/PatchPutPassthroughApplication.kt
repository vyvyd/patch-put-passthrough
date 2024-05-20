package com.demo.patchputpassthrough

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients

@SpringBootApplication
@EnableFeignClients
class PatchPutPassthroughApplication

fun main(args: Array<String>) {
    runApplication<PatchPutPassthroughApplication>(*args)
}
