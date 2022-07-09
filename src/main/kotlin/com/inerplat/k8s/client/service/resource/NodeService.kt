package com.inerplat.k8s.client.service.resource

import com.fasterxml.jackson.databind.ObjectMapper
import com.inerplat.k8s.client.model.dto.NodeResponse
import com.inerplat.k8s.client.model.dto.NodeResponseSummary
import io.kubernetes.client.openapi.apis.CoreV1Api
import io.kubernetes.client.openapi.models.V1Node
import io.kubernetes.client.openapi.models.V1Taint
import org.springframework.dao.DuplicateKeyException
import org.springframework.stereotype.Service

@Service
class NodeService(
    private val coreV1Api: CoreV1Api
) {
    fun getAllNodes(limit: Int): List<V1Node> {
        return coreV1Api.listNode(null, null, null, null, null, limit, null, null, 10, null).items
    }

    fun getNode(name: String): V1Node {
        return coreV1Api.readNode(name, null)
    }

    fun addTaint(nodeName: String, key: String, value: String?, effect: String): V1Node {
        val node = coreV1Api.readNode(nodeName, null)
        var taints = node.spec!!.taints
        if (taints == null) {
            taints = ArrayList<V1Taint>()
        }
        if (taints.any { it.key == key }) {
            throw DuplicateKeyException("Taint already exists")
        }
        taints.add(
            V1Taint().apply {
                this.key = key
                this.value = value
                this.effect = effect
            }
        )
        node.spec!!.taints = taints
        return coreV1Api.replaceNode(nodeName, node, null, null, null, null)
    }

    fun deleteTaint(nodeName: String, key: String): V1Node {
        val node = coreV1Api.readNode(nodeName, null)
        val taints = node.spec!!.taints
        if (taints == null || taints.let { i -> i.none { it.key == key } }) {
            throw IllegalArgumentException("Taint not found")
        }
        val newTaints = taints.filter { it.key != key }
        node.spec!!.taints = newTaints
        return coreV1Api.replaceNode(nodeName, node, null, null, null, null)
    }
}