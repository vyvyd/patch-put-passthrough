package com.demo.patchputpassthrough.target

import com.demo.patchputpassthrough.target.io.Input
import com.demo.patchputpassthrough.target.io.Output
import org.springframework.http.HttpStatusCode
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.util.*

data class OrderEntity(
    val id: Int? = null,
    val consignee:String?,
    val deliveryArea: String?
)

data class OrderPatchDTO(
    val consignee: Optional<String>? = null,
    val deliveryArea: Optional<String>? = null
)

@RestController
class OrderController(
    private val input: Input,
    private val output: Output
) {

    @PutMapping("/order/{id}")
    fun putOrder(
        @PathVariable id: Int,
        @RequestBody order: OrderEntity
    ) {
        val storedOrder = input.getById(id)
            ?: throw ResponseStatusException(HttpStatusCode.valueOf(404))

        // map all fields except id
        output.store(
            storedOrder.copy(
                consignee = order.consignee,
                deliveryArea = order.deliveryArea
            )
        )

    }

    @PatchMapping("/order/{id}")
    fun patchOrder(
        @PathVariable id: Int,
        @RequestBody order: OrderPatchDTO
    ) {
        val storedOrder = input.getById(id)
            ?: throw ResponseStatusException(HttpStatusCode.valueOf(404))

        output.store(
            storedOrder.copy(
                consignee = nonEmptyField(order.consignee, storedOrder.consignee),
                deliveryArea = nonEmptyField(order.deliveryArea, storedOrder.deliveryArea)
            )
        )

    }

    private fun <T> nonEmptyField(first: Optional<T>?, second: T?): T? {
        if ((first == null)) {
            // means the field is empty
            return second
        }
        if (first.isEmpty) {
            return null
        }
        return first.get()
    }


}