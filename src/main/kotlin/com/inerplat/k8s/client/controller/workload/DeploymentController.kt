package com.inerplat.k8s.client.controller.workload

import com.inerplat.k8s.client.model.dto.DeploymentRequest
import com.inerplat.k8s.client.model.dto.DeploymentResponse
import com.inerplat.k8s.client.service.workload.DeploymentService
import org.springframework.web.bind.annotation.*

@RestController
class DeploymentController(
    private val deploymentService: DeploymentService
) {
    @GetMapping("/api/v1/private/workload/deployment/list")
    fun getNamespacedDeployment(
        @RequestParam(defaultValue = "default") namespace: String,
        @RequestParam(defaultValue = "500") limit: Int
    ): List<*> {
        return deploymentService.getNamespaced(namespace, limit).map {
            DeploymentResponse(it)
        }
    }

    @GetMapping("/api/v1/private/workload/deployment/list/all")
    fun getAll(
        @RequestParam(defaultValue = "500") limit: Int
    ): List<*> {
        return deploymentService.getAll(limit).map {
            DeploymentResponse(it)
        }
    }

    @PutMapping("/api/v1/private/workload/deployment/toleration")
    fun putToleration(
        @RequestBody body: DeploymentRequest
    ): DeploymentResponse {
        if(body.operator.equals("Exists") && body.value != null) {
            throw IllegalArgumentException("Value must be empty when operator is Exists")
        }
        return DeploymentResponse(
            deploymentService.addToleration(
                body.namespace,
                body.name,
                body.key,
                body.value,
                body.effect!!,
                body.operator
            )!!
        )
    }

    @DeleteMapping("/api/v1/private/workload/deployment/toleration")
    fun deleteToleration(
        @RequestBody body: DeploymentRequest
    ): DeploymentResponse {
        return DeploymentResponse(
            deploymentService.deleteToleration(body.namespace, body.name, body.key)!!
        )
    }

    @PostMapping("/api/v1/private/workload/deployment/restart")
    fun restartDeployment(
        @RequestBody body: DeploymentRequest
    ): DeploymentResponse {
        return DeploymentResponse(
            deploymentService.restart(body.namespace, body.name)!!
        )
    }
}