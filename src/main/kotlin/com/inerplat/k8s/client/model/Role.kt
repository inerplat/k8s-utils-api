package com.inerplat.k8s.client.model

enum class Role(val key: String, val title: String) {
    GUEST("ROLE_GUEST", "손님"),
    USER("USER", "사용자"),
    ADMIN("ADMIN", "관리자")
}