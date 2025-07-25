package com.example.mobox_app.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [Index(value = ["email"], unique = true)] // El email debe ser único
)
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val lastName: String,
    val email: String,
    val passwordHash: String // Guardamos un hash, no la contraseña real
)