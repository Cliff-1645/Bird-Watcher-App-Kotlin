package com.birdwatching.app.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.birdwatching.app.data.entity.Bird

@Dao
interface BirdDao {
    @Query("SELECT * FROM birds WHERE tripId = :tripId ORDER BY id DESC")
    fun getBirdsByTripId(tripId: Long): LiveData<List<Bird>>

    @Query("SELECT * FROM birds WHERE tripId = :tripId")
    suspend fun getBirdsByTripIdSync(tripId: Long): List<Bird>

    @Query("SELECT * FROM birds WHERE id = :birdId")
    suspend fun getBirdById(birdId: Long): Bird?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBird(bird: Bird): Long

    @Update
    suspend fun updateBird(bird: Bird)

    @Delete
    suspend fun deleteBird(bird: Bird)

    @Query("DELETE FROM birds WHERE id = :birdId")
    suspend fun deleteBirdById(birdId: Long)

    @Query("DELETE FROM birds WHERE tripId = :tripId")
    suspend fun deleteBirdsByTripId(tripId: Long)

    @Query("SELECT * FROM birds WHERE tripId = :tripId AND uploaded = 0")
    suspend fun getUnuploadedBirds(tripId: Long): List<Bird>

    @Query("UPDATE birds SET uploaded = 1 WHERE id = :birdId")
    suspend fun markBirdAsUploaded(birdId: Long)
}

