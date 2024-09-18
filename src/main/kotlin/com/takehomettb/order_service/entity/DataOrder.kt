package com.takehomettb.order_service.entity

import com.fasterxml.jackson.annotation.JsonInclude
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.Objects

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ReturnStatus(
    var status: Boolean? = null,
    var data: Any? = null,
    var message: String? = null
){}

data class ListOrder(
    var orderId: Int? = null,
    var customerName: String? = null,
    var orderDate: String? = null,
    var orderItems: List<OrderFood> = emptyList(),
    var totalAmount: BigDecimal? = null,
    var orderStatus: String? = null,
){}

data class OrderFood(
    val foodName: String? = null,
    val quantity: Int? = null,
    val price: BigDecimal? = null,
){}

data class Item(
    val foodId: Int,
    val quantity: Int,
)

data class OrderRequest(
    val customerId: Int? = null,
    val orderItems: List<Item> = emptyList(),
)

data class FoodResponse(
    val foodId: Int,
    val name: String,
    val price: BigDecimal,
)

data class CustomerResponse(
    val customerId: Int? = null,
    val name: String,
    val address: String,
    val phone: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)

data class StatusOrder(
    val status: String
)