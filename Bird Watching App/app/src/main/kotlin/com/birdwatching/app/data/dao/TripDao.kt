package com.birdwatching.app.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.birdwatching.app.data.entity.Trip

@Dao
interface TripDao {
    @Query("SELECT * FROM trips ORDER BY date DESC, time DESC")
    fun getAllTrips(): LiveData<List<Trip>>

    @Query("SELECT * FROM trips WHERE id = :tripId")
    suspend fun getTripById(tripId: Long): Trip?

    @Query("SELECT * FROM trips WHERE id = :tripId")
    fun getTripByIdLiveData(tripId: Long): LiveData<Trip>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrip(trip: Trip): Long

    @Update
    suspend fun updateTrip(trip: Trip)

    @Delete
    suspend fun deleteTrip(trip: Trip)

    @Query("DELETE FROM trips WHERE id = :tripId")
    suspend fun deleteTripById(tripId: Long)

    @Query("SELECT * FROM trips WHERE uploaded = 0")
    suspend fun getUnuploadedTrips(): List<Trip>

    @Query("UPDATE trips SET uploaded = 1, uploadDate = :uploadDate WHERE id = :tripId")
    suspend fun markTripAsUploaded(tripId: Long, uploadDate: java.util.Date)
}

