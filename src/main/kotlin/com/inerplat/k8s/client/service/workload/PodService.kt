package com.inerplat.k8s.client.service.workload

import io.kubernetes.client.util.Config
import io.kubernetes.client.openapi.Configuration
import io.kubernetes.client.openapi.apis.CoreV1Api
import io.kubernetes.client.openapi.models.V1Pod
import io.kubernetes.client.openapi.models.V1PodList
import org.springframework.stereotype.Service

@Service
class PodService {
    fun getAllPods(namespace: String, limit: Int): List<V1Pod> {
        val api = CoreV1Api()
        val podList = api.listNamespacedPod(
            namespace,
            null,
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
        return podList.items
    }
}