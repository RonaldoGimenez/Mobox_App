package com.example.mobox_app.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "favorites",
    primaryKeys = ["userId", "movieId"], // Clave primaria compuesta
    indices = [Index(value = ["movieId"])],
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE // Si se borra un usuario, se borran sus favoritos
        ),
        ForeignKey(
            entity = Movie::class,
            parentColumns = ["id"],
            childColumns = ["movieId"],
            onDelete = ForeignKey.CASCADE // Si se borra una película, se borra de favoritos
        )
    ]
)
data class Favorite(
    val userId: Int,
    val movieId: Int
)