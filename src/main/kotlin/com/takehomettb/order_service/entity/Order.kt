package com.takehomettb.order_service.entity

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.math.BigDecimal
import java.security.Timestamp
import java.sql.Date
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(name = "food_order")
data class Order(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    var orderId: Int? = null,

    @Column(name = "customer_id")
    var customerId: Int? = null,

    @Column(name = "total_amount")
    var totalAmount: BigDecimal? = null,

    @Column(name = "order_status")
    var orderStatus: String? = null,

    @Column(name = "created_at", updatable = false)
    var createdAt: LocalDateTime? = LocalDateTime.now(),

    @Column(name = "updated_at")
    var updatedAt: LocalDateTime? = LocalDateTime.now(),

)
