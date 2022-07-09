package com.inerplat.k8s.client.utility

import com.fasterxml.jackson.databind.ObjectMapper

class StringUtils {
    companion object {
        private val objectMapper = ObjectMapper()
        fun isEmpty(str: String?): Boolean {
            return str == null || str.isEmpty()
        }
        fun writeValueAsString(obj: Any?): String {
            return objectMapper.writeValueAsString(obj)
        }
    }
}