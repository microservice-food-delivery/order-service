package com.takehomettb.order_service.entity

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.math.BigDecimal
import java.security.Timestamp
import java.sql.Date
import java.time.LocalDate

@Entity
@Table(name = "food_order")
data class Order(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    var orderId: Int? = null,

    @Column(name = "customer_name")
    var customerName: String? = null,

    @Column(name = "order_date")
    @CreationTimestamp
    var orderDate: Date? = null,

    @Column(name = "total_amount")
    var totalAmount: BigDecimal? = null,

    @Column(name = "order_status")
    var orderStatus: String? = null,

)
