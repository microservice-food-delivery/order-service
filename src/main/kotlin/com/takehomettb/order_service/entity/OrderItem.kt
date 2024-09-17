package com.takehomettb.order_service.entity

import jakarta.persistence.*

@Entity
@Table(name = "order_item")
data class OrderItem(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    var orderItemId: Int? = null,

    @Column(name = "order_id")
    var orderId: Int? = null,

    @Column(name = "food_id")
    var foodId: Int? = null,

    @Column(name = "quantity")
    var quantity: Int? = null,
)
