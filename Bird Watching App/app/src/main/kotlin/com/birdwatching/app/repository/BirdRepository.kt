package com.birdwatching.app.repository

import com.birdwatching.app.data.dao.BirdDao
import com.birdwatching.app.data.entity.Bird
import kotlinx.coroutines.flow.Flow

class BirdRepository(private val birdDao: BirdDao) {
    fun getBirdsByTripId(tripId: Long): Flow<List<Bird>> = birdDao.getBirdsByTripId(tripId)

    suspend fun getBirdsByTripIdSync(tripId: Long): List<Bird> = birdDao.getBirdsByTripIdSync(tripId)

    suspend fun getBirdById(birdId: Long): Bird? = birdDao.getBirdById(birdId)

    suspend fun insertBird(bird: Bird): Long = birdDao.insertBird(bird)

    suspend fun updateBird(bird: Bird) = birdDao.updateBird(bird)

    suspend fun deleteBird(bird: Bird) = birdDao.deleteBird(bird)

    suspend fun deleteBirdById(birdId: Long) = birdDao.deleteBirdById(birdId)

    suspend fun getUnuploadedBirds(tripId: Long): List<Bird> = birdDao.getUnuploadedBirds(tripId)

    suspend fun markBirdAsUploaded(birdId: Long) = birdDao.markBirdAsUploaded(birdId)
}

