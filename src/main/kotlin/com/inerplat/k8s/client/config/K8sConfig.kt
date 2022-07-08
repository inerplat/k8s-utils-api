package com.inerplat.k8s.client.config

import io.kubernetes.client.util.Config
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class K8sConfig {
    @Value("\${k8s.config.host}")
    private lateinit var host: String

    @Value("\${k8s.config.token}")
    private lateinit var token: String

    @Bean
    fun k8sClientConfig() {
        val client = Config.fromToken(
            host,
            token,
            false
        )!!
        io.kubernetes.client.openapi.Configuration.setDefaultApiClient(client)
    }
}