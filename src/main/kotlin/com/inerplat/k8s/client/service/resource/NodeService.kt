package com.inerplat.k8s.client.service.resource

import com.fasterxml.jackson.databind.ObjectMapper
import com.inerplat.k8s.client.model.JsonPatch
import com.inerplat.k8s.client.model.dto.NodeResponse
import com.inerplat.k8s.client.model.dto.NodeResponseSummary
import com.inerplat.k8s.client.model.dto.get
import com.inerplat.k8s.client.utility.StringUtils
import io.kubernetes.client.custom.V1Patch
import io.kubernetes.client.openapi.apis.CoreV1Api
import io.kubernetes.client.openapi.models.V1Node
import io.kubernetes.client.openapi.models.V1Taint
import org.springframework.dao.DuplicateKeyException
import org.springframework.stereotype.Service

@Service
class NodeService(
    private val coreV1Api: CoreV1Api
) {
    fun getAllNodes(limit: Int, taint: String?, contain: Boolean?): List<V1Node> {
        val nodeList = coreV1Api.listNode(null, null, null, null, null, limit, null, null, 10, null).items
        if (taint == null) return nodeList
        return nodeList.filter {
            val result = it.spec!!.taints?.any { i -> i["key"] == taint }
            when (contain) {
                true -> result == true
                else -> result == false || result == null
            }
        }
    }

    fun getNode(name: String?, ip: String?): V1Node {
        if (ip != null) getAllNodes(0, null, null).forEach {
            if (it.status!!.addresses?.any { i -> i.address == ip } == true) {
                if (name == null) return it
                else if (it.metadata!!.name == name) return it
            }
        }
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