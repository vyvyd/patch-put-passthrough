package com.demo.patchputpassthrough.target.io

import com.demo.patchputpassthrough.target.OrderEntity
import org.springframework.stereotype.Component

@Component
class Output {
    fun store(order: OrderEntity)  {
        println(order.toString())
    }

}