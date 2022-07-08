package com.inerplat.k8s.client.controller

import com.inerplat.k8s.client.service.workload.PodService
import io.kubernetes.client.openapi.models.V1Pod
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpSession

@RestController
class RootController {
    @GetMapping("/")
    fun index(): String {
        return "Hello World1!"
    }

    @GetMapping("/private/content")
    fun privateContent(): String {
        return "Hello World2!"
    }

    @GetMapping("/public/content")
    fun publicContent(): String {
        return "Hello World3!"
    }
}