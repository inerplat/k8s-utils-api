package com.inerplat.k8s.client.service.workload

import com.inerplat.k8s.client.model.dto.PodResponseSummary
import io.kubernetes.client.openapi.apis.CoreV1Api
import io.kubernetes.client.openapi.models.V1Pod
import io.kubernetes.client.openapi.models.V1PodList
import org.springframework.stereotype.Service

@Service
class PodService(
    private val coreV1Api: CoreV1Api
){
    fun getNamespacedPod(namespace: String, limit: Int): MutableList<V1Pod> {
        return coreV1Api.listNamespacedPod(namespace, null, null, null, null, null, limit, null, null, 10, null).items
    }

    fun getAll(limit: Int): MutableList<V1Pod> {
        return coreV1Api.listPodForAllNamespaces(null, null, null, null, limit, null, null, null, 10, null).items
    }
}