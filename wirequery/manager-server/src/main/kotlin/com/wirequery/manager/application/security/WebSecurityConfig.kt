package com.wirequery.manager.application.security

import org.springframework.boot.autoconfigure.security.SecurityProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.AuditorAware
import org.springframework.data.jdbc.repository.config.EnableJdbcAuditing
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy.IF_REQUIRED
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.SecurityFilterChain
import java.util.*

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@EnableJdbcAuditing(auditorAwareRef = "myAuditorAware")
@EnableConfigurationProperties(SecurityProperties::class)
class WebSecurityConfig(
    private val authenticationProvider: AuthenticationProvider,
) {
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .authenticationProvider(authenticationProvider)
            .cors { it.disable() }
            .csrf { it.disable() }
            .authorizeHttpRequests {
                it.requestMatchers("/graphql").permitAll()
                it.requestMatchers("/actuator/health").permitAll()
                it.requestMatchers("/subscriptions").permitAll()
                it.requestMatchers("/graphiql/**").permitAll()
                it.requestMatchers("/api/v1/**").permitAll()
                it.requestMatchers("/api/internal/**").permitAll()
            }
            .sessionManagement { it.sessionCreationPolicy(IF_REQUIRED) }
            .securityContext { it.requireExplicitSave(false) }
            .build()
    }

    @Bean
    fun myAuditorAware(): AuditorAware<String> {
        return MyAuditAware()
    }

    class MyAuditAware : AuditorAware<String> {
        override fun getCurrentAuditor(): Optional<String> {
            val authentication = SecurityContextHolder.getContext().authentication

            return if (authentication == null || !authentication.isAuthenticated) {
                Optional.empty()
            } else {
                Optional.ofNullable((authentication.principal as? CustomUserDetails)?.username)
            }
        }
    }
}
