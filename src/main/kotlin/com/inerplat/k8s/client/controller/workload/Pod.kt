package com.inerplat.k8s.client.controller.workload

import com.inerplat.k8s.client.service.workload.PodService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class Pod(
    val podService: PodService
) {
    @GetMapping("/private/workload/pod/list")
    fun getPodList(
        @RequestParam(defaultValue="default") namespace: String,
        @RequestParam(defaultValue="500") limit: Int
    ): List<*> {
        val pods = podService.getAllPods(namespace, limit)
        val ml = mutableListOf<Map<String, *>>()
        for (pod in pods) {
            ml.add(mapOf(
                "name" to pod.metadata!!.name.toString(),
                "podIp" to pod.status!!.podIPs!!.map{ it.ip },
                "podPhase" to pod.status!!.phase.toString()
            ))
        }
        return ml
    }
}