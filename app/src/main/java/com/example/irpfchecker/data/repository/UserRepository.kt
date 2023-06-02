package com.example.irpfchecker.data.repository

import com.example.irpfchecker.domain.model.User

interface UserRepository {

    suspend fun createUser(user: User)

    suspend fun updateUser(user: User)

    suspend fun deleteUser(user: User)

    suspend fun findAll(): List<User>

    suspend fun getFilteredUsers(query: String): List<User>

}