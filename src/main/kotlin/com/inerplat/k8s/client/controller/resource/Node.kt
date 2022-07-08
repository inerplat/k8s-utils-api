package com.inerplat.k8s.client.controller.resource

import com.inerplat.k8s.client.service.resource.NodeService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class Node(
    private val nodeService: NodeService
) {
    @GetMapping("/private/resource/node/list")
    fun getNodeList(
        @RequestParam(defaultValue = "default") namespace: String,
        @RequestParam(defaultValue = "500") limit: Int
    ): List<*> {
        val nodes = nodeService.getAllNodes(namespace, limit)
        val ml = mutableListOf<Map<String, *>>()
        for (node in nodes) {
            ml.add(
                mutableMapOf<String, Any?>(
                    "name" to node.metadata!!.name!!,
                    "nodeIps" to node.status!!.addresses!!.filter { it.type == "InternalIP" }.map { it.address!! },
                    "taints" to node.spec!!.taints?.map {
                        mapOf(
                            "key" to it.key,
                            "value" to it.value,
                            "effect" to it.effect
                        )
                    },
                )
            )
        }
        return ml
    }
}