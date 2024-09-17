package com.takehomettb.order_service.repository

import com.takehomettb.order_service.entity.OrderItem
import org.springframework.data.jpa.repository.JpaRepository

interface OrderItemRepository : JpaRepository<OrderItem, Int> {

    fun findByOrderId(orderId: Int): List<OrderItem>
}