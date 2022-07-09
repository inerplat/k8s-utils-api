package com.inerplat.k8s.client.model

data class JsonPatch (
    val op: String,
    val path: String,
    val value: Any
)