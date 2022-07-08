package com.inerplat.k8s.client

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class K8sClientApplication

fun main(args: Array<String>) {
    runApplication<K8sClientApplication>(*args)
}
