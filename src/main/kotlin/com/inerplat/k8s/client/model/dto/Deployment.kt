package com.inerplat.k8s.client.model.dto

import io.kubernetes.client.openapi.models.V1Deployment

data class DeploymentRequest(
  val name: String,
  val namespace: String = "default",
  val replicas: Int?,
  val key: String?,
  val operator: String?,
  val value: String?,
  val effect: String?,
)

data class DeploymentResponse(
    val name: String,
    val namespace: String,
    val creationTimestamp: String,
    val replicas: Int,
    val readyReplicas: Int,
    val tolerations: List<Map<String, String?>>?,
) {
    constructor(dp: V1Deployment) : this(
        name = dp.metadata!!.name!!,
        namespace = dp.metadata!!.namespace!!,
        creationTimestamp = dp.metadata!!.creationTimestamp!!.toString(),
        replicas = dp.spec!!.replicas ?: 0,
        readyReplicas = dp.status!!.readyReplicas ?: 0,
        tolerations = dp.spec!!.template!!.spec!!.tolerations?.map {
            mapOf(
                "key" to it.key,
                "operator" to it.operator,
                "value" to it.value,
                "effect" to it.effect
            )
        }
    )
}
