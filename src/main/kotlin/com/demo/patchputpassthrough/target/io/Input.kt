package com.demo.patchputpassthrough.target.io

import com.demo.patchputpassthrough.target.OrderEntity
import org.springframework.stereotype.Component

@Component
class Input {

    private val orders : List<OrderEntity> = listOf(
        OrderEntity(id = 1, consignee = "Max", deliveryArea = "Munich")
    )

    fun getById(id: Int) : OrderEntity? {
        return orders.firstOrNull { it.id == id }
    }
}

