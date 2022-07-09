package com.inerplat.k8s.client.model.dto

import io.kubernetes.client.openapi.models.V1Pod

data class PodResponseSummary(
    val name: String,
    val podIp: List<*>,
    val podPhase: String
) {
    constructor(v1Pod: V1Pod) : this(
        name = v1Pod.metadata!!.name!!,
        podIp = v1Pod.status!!.podIPs!!.map{ it.ip },
        podPhase = v1Pod.status!!.phase!!
    )
}