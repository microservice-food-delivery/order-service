package com.takehomettb.order_service.service

import com.takehomettb.order_service.entity.CustomerResponse
import com.takehomettb.order_service.entity.FoodResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@FeignClient(name = "customer-service", url = "http://customer-service:8084")
//@FeignClient(name = "customer-service", url = "http://localhost:8084")
interface CustomerServiceClient {

    @GetMapping("/api/customers/getCustomer/{customerId}")
    fun getCustomerById(@PathVariable customerId: Int): CustomerResponse
}