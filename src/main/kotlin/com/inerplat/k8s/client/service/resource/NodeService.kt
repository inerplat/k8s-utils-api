package com.inerplat.k8s.client.service.resource

import com.fasterxml.jackson.databind.ObjectMapper
import com.inerplat.k8s.client.model.JsonPatch
import com.inerplat.k8s.client.utility.StringUtils
import io.kubernetes.client.custom.V1Patch
import io.kubernetes.client.openapi.Configuration
import io.kubernetes.client.openapi.apis.CoreV1Api
import io.kubernetes.client.openapi.models.V1Node
import io.kubernetes.client.openapi.models.V1Pod
import io.kubernetes.client.openapi.models.V1Taint
import io.kubernetes.client.util.Config
import org.springframework.dao.DuplicateKeyException
import org.springframework.stereotype.Service

@Service
class NodeService {
    val objectMapper = ObjectMapper()
    fun getAllNodes(limit: Int): List<V1Node> {
        val api = CoreV1Api()
        val nodeList = api.listNode(null, null, null, null, null, limit, null, null, 10, null)
        return nodeList.items
    }

    fun getNode(name: String): V1Node {
        val api = CoreV1Api()
        return api.readNode(name, null)
    }

    fun addTaint(nodeName: String, key: String, value: String?, effect: String): V1Node {
        val api = CoreV1Api()
        val node = api.readNode(nodeName, null)
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
        return api.replaceNode(nodeName, node, null, null, null, null)
    }

    fun deleteTaint(nodeName: String, key: String): V1Node {
        val api = CoreV1Api()
        val node = api.readNode(nodeName, null)
        val taints = node.spec!!.taints
        if (taints == null || taints.let { i -> i.none { it.key == key } }) {
            throw IllegalArgumentException("Taint not found")
        }
        val newTaints = taints.filter { it.key != key }
        node.spec!!.taints = newTaints
        return api.replaceNode(nodeName, node, null, null, null, null)
    }
}