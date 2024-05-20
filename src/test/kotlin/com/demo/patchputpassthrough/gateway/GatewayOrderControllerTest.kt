package com.demo.patchputpassthrough.gateway

import com.demo.patchputpassthrough.target.OrderController
import com.demo.patchputpassthrough.target.OrderEntity
import com.demo.patchputpassthrough.target.io.Input
import com.demo.patchputpassthrough.target.io.Output
import com.ninjasquad.springmockk.MockkBean
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
class GatewayOrderControllerTest {

    @MockkBean
    private lateinit var output: Output

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun `passes through a put request for a single order to the target`() {
        every { output.store(any()) } just Runs

        mockMvc
            .perform(
                put("/gateway/order/{id}", 1)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {
                        "consignee": "Mark",
                        "deliveryArea": "Berlin"
                        }
                        """.trimIndent()
                    )
            )
            .andExpect(status().isOk)

        verify { output.store(OrderEntity(
            id = 1,
            consignee = "Mark",
            deliveryArea = "Berlin"
            ))
        }
    }

    @Test
    fun `patching just a single field in the order works`() {
        every { output.store(any()) } just Runs

        mockMvc
            .perform(
                patch("/gateway/order/{id}", 1)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {
                        "consignee": "Mark"
                        }
                    """.trimIndent()
                )
            )
            .andExpect(status().isOk)

        verify { output.store(OrderEntity(
            id = 1,
            consignee = "Mark",
            deliveryArea = "Berlin"
        ))
        }
    }
}

