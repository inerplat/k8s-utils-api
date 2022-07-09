package com.inerplat.k8s.client.service.workload

import com.inerplat.k8s.client.model.JsonPatch
import com.inerplat.k8s.client.utility.StringUtils
import io.kubernetes.client.custom.V1Patch
import org.springframework.stereotype.Service
import io.kubernetes.client.openapi.models.V1Deployment
import io.kubernetes.client.openapi.apis.AppsV1Api
import io.kubernetes.client.openapi.models.V1Toleration
import org.springframework.dao.DuplicateKeyException

@Service
class DeploymentService(
    private val appsV1Api: AppsV1Api
) {
    fun getAll(limit: Int): List<V1Deployment> {
        return appsV1Api.listDeploymentForAllNamespaces(null, null, null, null, limit, null, null, null, 10, null).items
    }

    fun getNamespaced(namespace: String, limit: Int): List<V1Deployment> {
        return appsV1Api.listNamespacedDeployment(
            namespace,
            null,
            null,
            null,
            null,
            null,
            limit,
            null,
            null,
            10,
            null
        ).items
    }

    fun addToleration(
        namespace: String,
        name: String,
        key: String?,
        value: String?,
        effect: String,
        operator: String?
    ): V1Deployment? {
        val deployment = appsV1Api.readNamespacedDeployment(name, namespace, null)
        var tolerations = deployment.spec!!.template.spec!!.tolerations
//        val patches = mutableListOf<JsonPatch>()
//        if (tolerations == null) {
//            patches.add(
//                JsonPatch(
//                    op = "add",
//                    path = "/spec/template/spec/tolerations",
//                    value = listOf<Any>()
//                )
//            )
//        }
//        if(tolerations?.any { it.key == key } == true) {
//            throw DuplicateKeyException("Toleration already exists")
//        }
//        patches.add(
//            JsonPatch(
//                op = "add",
//                path = "/spec/template/spec/tolerations/-",
//                value = mapOf(
//                    "key" to key,
//                    "value" to value,
//                    "effect" to effect,
//                    "operator" to operator
//                )
//            )
//        )
//        val v1Patch = V1Patch(StringUtils.writeValueAsString(patches))
//        println(StringUtils.writeValueAsString(patches))
//        return appsV1Api.patchNamespacedDeployment(name, namespace, v1Patch, null, null, null, null, null)
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
}