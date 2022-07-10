package com.inerplat.k8s.client.controller.resource

import com.inerplat.k8s.client.model.dto.NodeRequest
import com.inerplat.k8s.client.model.dto.NodeResponse
import com.inerplat.k8s.client.model.dto.NodeResponseSummary
import com.inerplat.k8s.client.service.resource.NodeService
import org.springframework.web.bind.annotation.*

@RestController
class NodeController(
    private val nodeService: NodeService
) {
    @GetMapping("/api/v1/private/resource/node/list")
    fun getAll(
        @RequestParam(defaultValue = "500") limit: Int,
        @RequestParam taint: String?,
        @RequestParam(defaultValue = "true") contain: Boolean?
    ): List<NodeResponseSummary> {
        val nodes = nodeService.getAllNodes(limit, taint, contain)
        return nodes.map { NodeResponse(it).summary() }

    }

    @GetMapping("/api/v1/private/resource/node/detail")
    fun getDetail(
        @RequestParam name: String?,
        @RequestParam ip: String?
    ): NodeResponse {
        if (name == null && ip == null)
            throw IllegalArgumentException("name or ip is required")
        val node = nodeService.getNode(name, ip)
        return NodeResponse(node)
    }

    @PutMapping("/api/v1/private/resource/node/taint")
    fun addTaint(
        @RequestBody node: NodeRequest
    ): NodeResponseSummary {
        val result = nodeService.addTaint(node.name!!, node.key!!, node.value, node.effect!!)
        return NodeResponse(result).summary()
    }

    @DeleteMapping("/api/v1/private/resource/node/taint")
    fun deleteTaint(
        @RequestBody node: NodeRequest
    ): NodeResponseSummary {
        val result = nodeService.deleteTaint(node.name!!, node.key!!)
        return NodeResponse(result).summary()
    }

    @PutMapping("/api/v1/private/resource/node/taint/any")
    fun addTaintAny(
        @RequestBody node: NodeRequest
    ): List<NodeResponseSummary> {
        if (node.count == null || node.key == null || node.effect == null)
            throw IllegalArgumentException("count and effect is required")
        val nonTaintNodeList = excludeControlPlaneNode(
            nodeService.getAllNodes(0, node.key, false).map {
                NodeResponse(it).summary()
            }
        )

        if (node.count > nonTaintNodeList.size)
            throw IllegalArgumentException("count is too large: non taint node count is ${nonTaintNodeList.size}")

        nonTaintNodeList.shuffled().slice(0 until node.count).forEach {
            nodeService.addTaint(it.name, node.key, node.value, node.effect)
        }

        return nodeService.getAllNodes(0, node.key, false).map {
            NodeResponse(it).summary()
        }
    }

    @DeleteMapping("/api/v1/private/resource/node/taint/any")
    fun deleteTaintAny(
        @RequestBody node: NodeRequest
    ): List<NodeResponseSummary> {
        if (node.count == null || node.key == null)
            throw IllegalArgumentException("count and effect is required")
        val taintNodeList = excludeControlPlaneNode(
            nodeService.getAllNodes(0, node.key, true).map {
                NodeResponse(it).summary()
            }, false
        )
        if (node.count > taintNodeList.size)
            throw IllegalArgumentException("count is too large: tainted node count is ${taintNodeList.size}")

        val result = mutableListOf<String>()
        taintNodeList.shuffled().slice(0 until node.count).forEach {
            nodeService.deleteTaint(it.name, node.key)
            result.add(it.name)
        }
        return result.map { nodeService.getNode(it, null) }.map { NodeResponse(it).summary() }
    }

    private fun excludeControlPlaneNode(
        nodeList: List<NodeResponseSummary>,
        nullable: Boolean = true
    ): List<NodeResponseSummary> {
        return nodeList.filter {
            val result = it.taints?.filter { i ->
                when (i["key"]) {
                    "node.node-role.kubernetes.io/control-plane" -> false
                    "node.node-role.kubernetes.io/master" -> false
                    else -> true
                }
            }
            when (nullable) {
                true -> result == null
                else -> result != null
            }
        }
    }
}
