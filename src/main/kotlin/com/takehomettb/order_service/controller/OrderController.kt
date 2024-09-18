package com.takehomettb.order_service.controller

import com.takehomettb.order_service.entity.Order
import com.takehomettb.order_service.entity.OrderRequest
import com.takehomettb.order_service.entity.StatusOrder
import com.takehomettb.order_service.service.OrderService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.view.RedirectView


@RestController
@CrossOrigin(origins = ["*"])
@RequestMapping("/api/orders")
class OrderController {
    @Autowired
    lateinit var orderService: OrderService


    @GetMapping("/check")
    fun order(): ResponseEntity<Any> {
        return ResponseEntity.ok().body("Order")
    }

    @GetMapping("/get")
    fun getOrder(): ResponseEntity<Any> {
        return ResponseEntity.ok().body(orderService.getAll())
    }

    @GetMapping("/getOrders")
    fun getAllOrder(): ResponseEntity<Any> {
        return ResponseEntity.ok().body(orderService.getAllOrders())
    }
    @GetMapping("/getOrder/{orderId}")
    fun getOrderByOrderId(@PathVariable orderId: Int): ResponseEntity<Any> {
        return ResponseEntity.ok().body(orderService.getOrderById(orderId))
    }

    @PostMapping("/createOrder")
    fun createFood(@RequestBody orderRequest: OrderRequest): ResponseEntity<Any> {
        return ResponseEntity.ok().body(orderService.createOrder(orderRequest))
    }

    @PutMapping("/changeStatus/{orderId}")
    fun updateStatus(@PathVariable orderId: Int, @RequestBody data: StatusOrder): ResponseEntity<Any> {
        return ResponseEntity.ok().body(orderService.changeStatus(orderId,data))
    }
}