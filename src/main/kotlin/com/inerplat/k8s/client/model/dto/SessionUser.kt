package com.inerplat.k8s.client.model.dto

import java.io.Serializable

data class SessionUser(val name: String, val email: String, val picture: String?) : Serializable