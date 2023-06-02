package com.example.irpfchecker.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "users")
data class User(
    @PrimaryKey val ulid: String,
    val name: String,
    val email: String,
    val createdAt: Date
)
