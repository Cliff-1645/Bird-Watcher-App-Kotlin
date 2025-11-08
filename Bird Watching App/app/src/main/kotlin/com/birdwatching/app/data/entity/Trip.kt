package com.birdwatching.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.birdwatching.app.data.converter.DateConverter
import java.util.Date

@Entity(tableName = "trips")
@TypeConverters(DateConverter::class)
data class Trip(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val tripName: String,
    val date: Date,
    val time: String,
    val location: String,
    val duration: String,
    val description: String? = null,
    val weather: String? = null,
    val temperature: String? = null,
    val companionCount: Int = 0,
    val imagePath: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val uploaded: Boolean = false,
    val uploadDate: Date? = null
)

