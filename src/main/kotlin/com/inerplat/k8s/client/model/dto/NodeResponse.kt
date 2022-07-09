package com.inerplat.k8s.client.model.dto

import io.kubernetes.client.openapi.models.V1Node
import java.math.BigDecimal


abstract class NodeResponseSummary(
    open val name: String,
    open val nodeIps: List<String>,
    open val taints: List<Map<String, String?>>?,
) {
    constructor(nodeResponse: NodeResponse) : this(
        nodeResponse.name,
        nodeResponse.nodeIps,
        nodeResponse.taints
    )
}

data class NodeResponse(
    override val name: String,
    override val nodeIps: List<String>,
    override val taints: List<Map<String, String?>>?,
    val arch: String?,
    val os: String?,
    val kernelVersion: String?,
    val containerRuntimeVersion: String?,
    val kubeletVersion: String?,
    val kubeProxyVersion: String?,
    val cpu: BigDecimal?,
    val memory: BigDecimal?,
    val disk: BigDecimal?,
    val podCnt: BigDecimal?
) : NodeResponseSummary(name, nodeIps, taints) {
    constructor(node: V1Node) : this(
        node.metadata!!.name!!,
        node.status!!.addresses!!.filter { it.type == "InternalIP" }.map { it.address!! },
        node.spec!!.taints?.map {
            mapOf(
                "key" to it.key,
                "value" to it.value,
                "effect" to it.effect
            )
        },
        node.status!!.nodeInfo!!.architecture!!,
        node.status!!.nodeInfo!!.osImage!!,
        node.status!!.nodeInfo!!.kernelVersion!!,
        node.status!!.nodeInfo!!.containerRuntimeVersion!!,
        node.status!!.nodeInfo!!.kubeletVersion!!,
        node.status!!.nodeInfo!!.kubeProxyVersion!!,
        node.status!!.capacity!!["cpu"]["number"] as BigDecimal,
        node.status!!.capacity!!["memory"]["number"] as BigDecimal,
        node.status!!.capacity!!["ephemeral-storage"]["number"] as BigDecimal,
        node.status!!.capacity!!["pods"]["number"] as BigDecimal?
    )
}

operator fun Any?.get(s: String): Any? {
    return this?.let {
        it.javaClass.getDeclaredField(s).apply {
            isAccessible = true
            return get(it)
        }
    }
}

