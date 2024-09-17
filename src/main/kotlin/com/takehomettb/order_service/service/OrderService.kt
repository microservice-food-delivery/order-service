package com.takehomettb.order_service.service

import com.takehomettb.order_service.entity.Order
import com.takehomettb.order_service.entity.*
import com.takehomettb.order_service.repository.OrderItemRepository
import com.takehomettb.order_service.repository.OrderRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.util.*
import java.time.LocalDateTime
import kotlin.time.times

@Service
class OrderService {
    @Autowired lateinit var orderItemRepository: OrderItemRepository
    @Autowired lateinit var inventoryServiceClient: InventoryServiceClient
    @Autowired lateinit var orderRepository: OrderRepository

    var response = ReturnStatus()

    fun getAll(): List<Order> {
        return orderRepository.findAll()
    }

    fun getAllOrders(): List<ListOrder> {
        var listOrder: MutableList<ListOrder> = mutableListOf()

        val result = orderRepository.findAll()
        for (item in result) {
            var order = ListOrder()
            order.orderId = item.orderId
            order.customerName = item.customerName
            order.orderDate = item.orderDate.toString()
            order.orderItems = getFoodName(item.orderId!!)
            order.totalAmount = item.totalAmount
            order.orderStatus = item.orderStatus
            listOrder.add(order)
        }
        return listOrder
    }

    fun getOrderById(orderId: Int): Any {
        val result = orderRepository.findOrderByOrderId(orderId)
        val order = ListOrder()
        if (result != null) {
            order.orderId = result.orderId
            order.customerName = result.customerName
            order.orderDate = result.orderDate.toString()
            order.orderItems = getFoodName(result.orderId!!)
            order.totalAmount = result.totalAmount
            order.orderStatus = result.orderStatus
        }
        return order
    }

    fun getFoodName(orderId: Int): List<OrderFood>{
        var result: Order? = orderRepository.findById(orderId).orElse(null)
            ?: return emptyList()
        // ดึงข้อมูลรายการอาหารที่เป็นของ orderId
        val orderItems = orderItemRepository.findByOrderId(orderId)
        // สร้างรายการ OrderFood จาก orderItems
        val foods = orderItems.map { item ->
            // ดึงชื่ออาหารจาก inventory-service
            val food = inventoryServiceClient.orderFoodById(item.foodId!!)
            OrderFood(
                foodName = food.name,
                quantity = item.quantity,
                price = food.price,
            )
        }
        return foods
    }

    // สร้างรายการคำสั่งซื้อใน table food_order
    fun createOrder(orderRequest: OrderRequest): ReturnStatus {
        val order = Order()
        order.customerName = orderRequest.customerName
        order.orderStatus = "waiting"
        order.totalAmount = 0.toBigDecimal()
        val orderCount = orderRequest.orderItems.size
        for(i in 0 until orderCount) {
            try {
                val item = inventoryServiceClient.orderFoodById(orderRequest.orderItems[i].foodId)
                val itemPrice = item.price
                order.totalAmount = order.totalAmount!!.add(
                    orderRequest.orderItems[i].quantity.toBigDecimal().multiply(itemPrice)
                )
            }catch (e: Exception){
                return ReturnStatus(
                    status = false,
                    message = "ไม่มีหมายเลข ${orderRequest.orderItems[i].foodId} ในรายการอาหาร",
                )
            }

        }
        try {
            val saveOrder = orderRepository.save(order)
            if(saveOrder.orderId != null){
                createOrderItem(orderRequest, saveOrder.orderId!!)
            }
            response.status = true
            response.data = order
            response.message = "บันทึกคำสั่งซื้อเสร็จสิ้น"
            return response
        }catch (ex: Exception){
            response.status = false
            response.data = order
            return response
        }
    }

    //สร้างรายการอาหารใน table orderItem
    fun createOrderItem(orderRequest: OrderRequest, orderId : Int): ReturnStatus {
        val orderCount = orderRequest.orderItems.size
        for(i in 0 until orderCount) {
            val orderItem = OrderItem().apply {
                this.orderId = orderId
                val item = inventoryServiceClient.orderFoodById(orderRequest.orderItems[i].foodId)
                this.foodId = item.foodId
                this.quantity = orderRequest.orderItems[i].quantity
            }
            orderItemRepository.save(orderItem)
        }
        response.status = true
        response.message = "บันทึกรายการอาหารเสร็จสิ้น"
        return response
    }

    fun changeStatus(orderId: Int, data: StatusOrder): Any {
        val result: Order = orderRepository.findById(orderId).orElse(null)
            ?: return ReturnStatus(
                status = false,
                message = "ไม่มีหมายเลข Order นี้"
            )

        if(data.status.isBlank()){
            return ReturnStatus(
                status = false,
                message = "กรุณากรอกข้อมูลสถานะ"
            )
        }
        if(data.status in listOf("waiting","confirm", "cooking","delivering","completed","cancel")){
            var message = ""
            if (data.status == "cancel") { //เช็คสถานะห้าม cancel ขณะอยู่สถานะอื่นๆที่ไม่ใช่ waiting
                if(result.orderStatus == "waiting") {
                    result.orderStatus = "cancel"
                }else{
                    message = "ไม่สามารถ cancel หมายเลข Order นี้ได้เพราะอยู่ในสถานะ ${result.orderStatus} แล้ว"
                }
            } else {
                // message ค่าเริ่มต้น
                message = "เปลี่ยนสถานะเรียบร้อย"
                // อัปเดตสถานะของ order ตามเงื่อนไขที่กำหนด
                when {
                    data.status == "confirm" && result.orderStatus == "waiting" -> {
                        result.orderStatus = "confirm"
                    }

                    data.status == "cooking" && result.orderStatus == "confirm" -> {
                        result.orderStatus = "cooking"
                    }

                    data.status == "delivering" && result.orderStatus == "cooking" -> {
                        result.orderStatus = "delivering"
                    }

                    data.status == "completed" && result.orderStatus == "delivering" -> {
                        result.orderStatus = "completed"
                    }

                    else -> {
                        message = "ไม่สามารถเปลี่ยนสถานะได้ เนื่องจากสถานะปัจจุบันไม่สอดคล้องกับการเปลี่ยนสถานะ"
                        return ReturnStatus(
                            status = false,
                            data = getOrderById(orderId),
                            message = message
                        )
                    }
                }
            }
            orderRepository.save(result)
            return ReturnStatus(
                status = true,
                data = getOrderById(orderId),
                message = message
            )
        }else{
            return ReturnStatus(
                status = false,
                message = "กรุณากรอกสถานะให้ถูกต้อง Ex. waiting, confirm, cooking, delivering, completed, cancel"
            )
        }

    }
}