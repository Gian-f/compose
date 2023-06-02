package com.example.irpfchecker.data.repository

import com.example.irpfchecker.data.dao.UserDao
import com.example.irpfchecker.domain.model.User
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao
    ) : UserRepository {

     override suspend fun createUser(user: User) {
        userDao.insert(user)
    }

    override suspend fun updateUser(user: User) {
        userDao.updateUser(user)
    }

    override suspend fun deleteUser(user: User) {
        userDao.deleteUser(user)
    }

    override suspend fun findAll(): List<User> {
        return userDao.findAll()
    }

    override suspend fun getFilteredUsers(query: String): List<User> {
        return userDao.getFilteredUsers(query)
    }
}