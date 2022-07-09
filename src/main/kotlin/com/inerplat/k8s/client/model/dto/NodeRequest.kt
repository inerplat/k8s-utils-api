package com.inerplat.k8s.client.model.dto

data class NodeRequest(
    val name: String,
    val key: String?,
    val value: String?,
    val effect: String?
)