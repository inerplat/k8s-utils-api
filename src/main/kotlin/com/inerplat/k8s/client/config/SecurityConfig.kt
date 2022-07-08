package com.inerplat.k8s.client.config

import com.inerplat.k8s.client.model.Role
import com.inerplat.k8s.client.service.GoogleOAuth2UserService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityConfig(
    val customOAuth2UserService: GoogleOAuth2UserService
) {
    @Bean
    fun filterChain(http: HttpSecurity) : SecurityFilterChain {
        http
            .csrf().disable()
            .headers().frameOptions().disable()
            .and()
            .authorizeRequests()
            .antMatchers("/", "/login", "/logout").permitAll()
            .antMatchers("/private/**").hasRole(Role.GUEST.name)
            .antMatchers("/api/**").hasRole(Role.USER.name)
            .antMatchers("/public/**").permitAll()
            .anyRequest().authenticated()
            .and()
            .logout().logoutSuccessUrl("/").permitAll()
            .and()
            .oauth2Login().userInfoEndpoint().userService(customOAuth2UserService)
        return http.build()
    }
}
