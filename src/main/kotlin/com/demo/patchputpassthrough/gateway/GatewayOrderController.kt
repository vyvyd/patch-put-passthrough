package com.demo.patchputpassthrough.gateway

import feign.okhttp.OkHttpClient
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*


data class PassThroughOrderDTO(
    val id: Optional<Int> ? = null,
    val consignee: Optional<String>? = null,
    val deliveryArea: Optional<String>? = null
)

@RestController
@RequestMapping("/gateway")
class GatewayOrderController(
    private val client: OrderClient
) {

    @PutMapping("/order/{id}")
    fun putOrder(
        @PathVariable id: Int,
        @RequestBody order: PassThroughOrderDTO
    )  {
        require(order.id == null) { "Order id must be null in a PUT request" }
        client.put(id, order)
    }

    @PatchMapping("/order/{id}")
    fun patchOrder(
        @PathVariable id: Int,
        @RequestBody order: PassThroughOrderDTO
    )  {
        require(order.id == null) { "Order id must be null in a PUT request" }
        client.patch(id, order)
    }

}

@FeignClient(name = "targetClient", url = "http://localhost:9999")
interface OrderClient {

    @RequestMapping(method = [RequestMethod.PUT], value = ["/order/{id}"])
    fun put(@PathVariable id: Int, @RequestBody order: PassThroughOrderDTO) : ResponseEntity<Map<String, Any>>

    @RequestMapping(method = [RequestMethod.PATCH], value = ["/order/{id}"])
    fun patch(@PathVariable id: Int, @RequestBody order: PassThroughOrderDTO) : ResponseEntity<Map<String, Any>>

}

@Configuration
class FeignConfiguration {

    @Bean
    fun client(): OkHttpClient {
        return OkHttpClient()
    }
}