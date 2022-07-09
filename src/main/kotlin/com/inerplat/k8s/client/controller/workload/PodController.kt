package com.inerplat.k8s.client.controller.workload

import com.inerplat.k8s.client.model.dto.PodResponseSummary
import com.inerplat.k8s.client.service.workload.PodService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class PodController(
    val podService: PodService,
) {
    @GetMapping("/api/v1/private/workload/pod/list")
    fun getNamespacedPod(
        @RequestParam(defaultValue = "default") namespace: String,
        @RequestParam(defaultValue = "500") limit: Int
    ): List<PodResponseSummary> {
        return podService.getNamespacedPod(namespace, limit).map {
            PodResponseSummary(it)
        }
    }

    @GetMapping("/api/v1/private/workload/pod/list/all")
    fun getAll(
        @RequestParam(defaultValue = "500") limit: Int
    ): List<PodResponseSummary> {
        return podService.getAll(limit).map{
            PodResponseSummary(it)
        }
    }
}