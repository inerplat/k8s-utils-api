package com.inerplat.k8s.client.model.dto

import com.inerplat.k8s.client.model.Role
import com.inerplat.k8s.client.model.entity.UserEntity

class OAuthAttributes constructor(
    val attributes: Map<String, Any>,
    val nameAttributeKey: String,
    val name: String,
    val email: String,
    val picture: String
) {
    fun toEntity(): UserEntity {
        return UserEntity(
            name = name,
            email = email,
            picture = picture,
            role = Role.GUEST
        )
    }

    companion object {
        fun ofGoogle(userNameAttributeName: String, attributes: Map<String, Any>): OAuthAttributes {
            return OAuthAttributes(
                name = attributes["name"] as String,
                email = attributes["email"] as String,
                picture = attributes["picture"] as String,
                attributes = attributes,
                nameAttributeKey = userNameAttributeName,
            )
        }
        fun ofGithub(userNameAttributeName: String, attributes: Map<String, Any>): OAuthAttributes {
            return OAuthAttributes(
                name = attributes["name"] as String,
                email = attributes["email"] as String,
                picture = attributes["avatar_url"] as String,
                attributes = attributes,
                nameAttributeKey = userNameAttributeName,
            )
        }
    }
}
