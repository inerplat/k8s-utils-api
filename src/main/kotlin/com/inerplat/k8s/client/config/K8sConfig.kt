package com.inerplat.k8s.client.config

import io.kubernetes.client.openapi.ApiClient
import io.kubernetes.client.openapi.apis.AppsV1Api
import io.kubernetes.client.util.Config
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import io.kubernetes.client.openapi.apis.CoreV1Api
import org.springframework.context.annotation.DependsOn


@Configuration
class K8sConfig {
    @Value("\${k8s.config.host}")
    private lateinit var host: String

    @Value("\${k8s.config.token}")
    private lateinit var token: String

    @Bean
    fun k8sClient(): ApiClient {
        val client = Config.fromToken(host, token, false)!!
        io.kubernetes.client.openapi.Configuration.setDefaultApiClient(client)
        return client
    }

    @Bean
    @DependsOn("k8sClient")
    fun coreV1Api(): CoreV1Api {
        return CoreV1Api()
    }

    @Bean
    @DependsOn("k8sClient")
    fun appsV1Api(): AppsV1Api {
        return AppsV1Api()
    }
}