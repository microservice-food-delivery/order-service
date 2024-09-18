package com.takehomettb.order_service.service

import com.takehomettb.order_service.entity.FoodResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable


@FeignClient(name = "inventory-service", url = "http://inventory-service:8082")
//@FeignClient(name = "inventory-service", url = "http://localhost:8082")
interface InventoryServiceClient {

    @GetMapping("/api/inventory/getFood/{foodId}")
    fun orderFoodById(@PathVariable foodId: Int): FoodResponse
}