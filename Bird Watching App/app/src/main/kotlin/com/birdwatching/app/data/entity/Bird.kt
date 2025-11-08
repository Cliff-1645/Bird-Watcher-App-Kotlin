package com.birdwatching.app.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "birds",
    foreignKeys = [ForeignKey(
        entity = Trip::class,
        parentColumns = ["id"],
        childColumns = ["tripId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["tripId"])]
)
data class Bird(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val tripId: Long,
    val species: String,
    val location: String,
    val quantity: Int,
    val comments: String? = null,
    val imagePath: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val spottedTime: String? = null,
    val uploaded: Boolean = false
)

