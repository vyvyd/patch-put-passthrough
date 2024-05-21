package com.demo.patchputpassthrough.target

import com.demo.patchputpassthrough.target.io.Input
import com.demo.patchputpassthrough.target.io.Output
import com.fasterxml.jackson.annotation.JsonInclude
import org.springframework.http.HttpStatusCode
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.util.*

data class OrderEntity(
    val id: Int? = null,
    val consignee:String,
    val deliveryArea: String,
    val comment: String?
)

@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class OrderPatchDTO(
    val consignee: Optional<String> = Optional.empty(),
    val deliveryArea: Optional<String> = Optional.empty(),
    val comment: Optional<String?>? = null
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
                deliveryArea = order.deliveryArea,
                comment = order.comment
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
                consignee = mandatoryField(order.consignee, storedOrder.consignee),
                deliveryArea = mandatoryField(order.deliveryArea, storedOrder.deliveryArea),
                comment = optionalField(order.comment, storedOrder.comment)
            )
        )

    }

    private fun <T> optionalField(first: Optional<T>?, second: T?): T? {
        if ((first == null)) {
            // means the field is explicitly set to null
            return null
        }
        return first.orElse(second)
    }

    private fun <T> mandatoryField(first: Optional<T>, second: T): T {
        return first.orElse(second)
    }

}