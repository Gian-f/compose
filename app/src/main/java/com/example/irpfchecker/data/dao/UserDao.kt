package com.example.irpfchecker.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.irpfchecker.domain.model.User

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User)

    @Update
    fun updateUser(user: User)

    @Delete
    fun deleteUser(user: User)

    @Query("SELECT * FROM users ORDER BY ulid DESC")
    suspend fun findAll(): List<User>

    @Query("SELECT * FROM users WHERE name LIKE '%' || :query || '%'")
    fun getFilteredUsers(query: String): List<User>
}