package com.inerplat.k8s.client.service.resource

import io.kubernetes.client.openapi.Configuration
import io.kubernetes.client.openapi.apis.CoreV1Api
import io.kubernetes.client.openapi.models.V1Node
import io.kubernetes.client.openapi.models.V1Pod
import io.kubernetes.client.util.Config
import org.springframework.stereotype.Service

@Service
class NodeService {
    fun getAllNodes(namespace: String, limit: Int): List<V1Node> {
        val api = CoreV1Api()
        val nodeList = api.listNode(
            namespace,
            null,
            null,
            null,
            null,
            limit,
            null,
            null,
            10,
            null
        )
        return nodeList.items
    }
}