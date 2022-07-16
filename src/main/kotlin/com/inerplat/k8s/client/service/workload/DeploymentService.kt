package com.inerplat.k8s.client.service.workload

import com.inerplat.k8s.client.model.JsonPatch
import com.inerplat.k8s.client.utility.StringUtils
import io.kubernetes.client.custom.V1Patch
import io.kubernetes.client.openapi.ApiClient
import org.springframework.stereotype.Service
import io.kubernetes.client.openapi.models.V1Deployment
import io.kubernetes.client.openapi.apis.AppsV1Api
import io.kubernetes.client.openapi.models.V1Toleration
import io.kubernetes.client.util.PatchUtils
import io.kubernetes.client.util.wait.Wait
import org.springframework.dao.DuplicateKeyException
import java.time.Duration
import java.time.LocalDateTime

@Service
class DeploymentService(
    private val appsV1Api: AppsV1Api,
    private val k8sClient: ApiClient
) {
    fun getAll(limit: Int): List<V1Deployment> {
        return appsV1Api.listDeploymentForAllNamespaces(null, null, null, null, limit, null, null, null, 10, null).items
    }

    fun getNamespaced(namespace: String, limit: Int): List<V1Deployment> {
        return appsV1Api.listNamespacedDeployment(namespace, null, null, null, null, null, limit, null, null, 10, null).items
    }

    fun addToleration(namespace: String, name: String, key: String?, value: String?, effect: String, operator: String?): V1Deployment? {
        val deployment = appsV1Api.readNamespacedDeployment(name, namespace, null)
        var tolerations = deployment.spec!!.template.spec!!.tolerations
        if (tolerations == null) {
            tolerations = ArrayList<V1Toleration>()
        }
        if (tolerations.any { it.key == key }) {
            throw DuplicateKeyException("Toleration already exists")
        }
        tolerations.add(
            V1Toleration().apply {
                this.key = key
                this.value = value
                this.effect = effect
                this.operator = operator
            }
        )
        return appsV1Api.replaceNamespacedDeployment(name, namespace, deployment, null, null, null, null)
    }

    fun deleteToleration(namespace: String, name: String, key: String?): V1Deployment? {
        val deployment = appsV1Api.readNamespacedDeployment(name, namespace, null)
        val tolerations = deployment.spec!!.template.spec!!.tolerations
        if (tolerations == null || tolerations.let { i -> i.none { it.key == key } }) {
            throw IllegalArgumentException("Toleration not found")
        }
        val newTolerations = tolerations.filter { it.key != key }
        deployment.spec!!.template.spec!!.tolerations = newTolerations
        return appsV1Api.replaceNamespacedDeployment(name, namespace, deployment, null, null, null, null)
    }

    fun restart(namespace: String, name: String): V1Deployment? {
        val deployment = appsV1Api.readNamespacedDeployment(name, namespace, null)
        deployment.spec?.template?.metadata?.putAnnotationsItem(
            "kubectl.kubernetes.io/restartedAt",
            LocalDateTime.now().toString()
        )
        return appsV1Api.replaceNamespacedDeployment(name, namespace, deployment, null, null, "kubectl-rollout", null)
    }
}