package com.inerplat.k8s.client.controller.resource

import com.inerplat.k8s.client.model.dto.NodeRequest
import com.inerplat.k8s.client.model.dto.NodeResponse
import com.inerplat.k8s.client.model.dto.NodeResponseSummary
import com.inerplat.k8s.client.service.resource.NodeService
import org.springframework.data.crossstore.ChangeSetPersister
import org.springframework.web.bind.annotation.*

@RestController
class Node(
    private val nodeService: NodeService
) {
    @GetMapping("/api/v1/private/resource/node/list")
    fun getList(
        @RequestParam(defaultValue = "500") limit: Int
    ): List<*> {
        val nodes = nodeService.getAllNodes(limit)
        val ml = mutableListOf<NodeResponseSummary>()
        for (node in nodes) {
            ml.add(NodeResponse(node) as NodeResponseSummary)
        }
        return ml
    }

    @GetMapping("/api/v1/private/resource/node/detail")
    fun getDetail(
        @RequestParam name: String
    ): NodeResponse {
        val node = nodeService.getNode(name)
        return NodeResponse(node)
    }

    @PutMapping("/api/v1/private/resource/node/taint")
    fun addTaint(
        @RequestBody node: NodeRequest
    ): NodeResponseSummary {
        val result = nodeService.addTaint(node.name, node.key!!, node.value, node.effect!!)
        return NodeResponse(result) as NodeResponseSummary
    }

    @DeleteMapping("/api/v1/private/resource/node/taint")
    fun deleteTaint(
        @RequestBody node: NodeRequest
    ): NodeResponseSummary {
        val result = nodeService.deleteTaint(node.name, node.key!!)
        return NodeResponse(result) as NodeResponseSummary
    }
}
