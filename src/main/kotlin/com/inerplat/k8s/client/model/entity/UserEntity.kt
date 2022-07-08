package com.inerplat.k8s.client.model.entity

import com.inerplat.k8s.client.model.Role
import javax.persistence.*

@Entity
@Table(name = "users")
class UserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Column(nullable = false)
    val name: String,

    @Column(nullable = false)
    val email: String,

    @Column
    var picture: String? = "",

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val role: Role
) {}