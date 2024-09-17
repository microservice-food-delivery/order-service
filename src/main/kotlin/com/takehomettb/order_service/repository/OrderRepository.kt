package com.takehomettb.order_service.repository

import com.takehomettb.order_service.entity.Order
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface OrderRepository : JpaRepository<Order,Int> {
    fun findOrderByOrderId(id: Int): Order?

}