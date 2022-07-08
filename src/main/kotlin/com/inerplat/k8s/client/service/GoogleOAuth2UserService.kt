package com.inerplat.k8s.client.service

import com.inerplat.k8s.client.model.Role
import com.inerplat.k8s.client.model.dto.OAuthAttributes
import com.inerplat.k8s.client.model.dto.SessionUser
import com.inerplat.k8s.client.model.entity.UserEntity
import com.inerplat.k8s.client.repository.UserRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService
import org.springframework.security.oauth2.core.user.DefaultOAuth2User
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service
import java.util.*
import javax.servlet.http.HttpSession

@Service
class GoogleOAuth2UserService(
    private val userRepository: UserRepository,
    private val httpSession: HttpSession
) : OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    override fun loadUser(userRequest: OAuth2UserRequest?): OAuth2User {
        val delegate: OAuth2UserService<OAuth2UserRequest, OAuth2User> = DefaultOAuth2UserService()
        val oAuth2User = delegate.loadUser(userRequest)

        val clientId = userRequest!!.clientRegistration.clientId
        val userNameAttributeName =
            userRequest.clientRegistration.providerDetails.userInfoEndpoint.userNameAttributeName
        val attributes = OAuthAttributes.ofGoogle(userNameAttributeName, oAuth2User.attributes)
        val user = saveOrUpdate(attributes)

        httpSession.setAttribute("user", SessionUser(user.name, user.email, user.picture))

        return DefaultOAuth2User(
            Collections.singleton(SimpleGrantedAuthority(user.role.key)),
            attributes.attributes,
            attributes.nameAttributeKey
        )
    }

    private fun saveOrUpdate(attributes: OAuthAttributes): UserEntity {
        val user = userRepository.findByEmail(attributes.email) ?: UserEntity(
            name = attributes.name,
            email = attributes.email,
            picture = attributes.picture,
            role = Role.GUEST
        )
        return userRepository.save(user)
    }
}