package com.birdwatching.app.repository

import com.birdwatching.app.data.dao.TripDao
import com.birdwatching.app.data.entity.Trip
import kotlinx.coroutines.flow.Flow

class TripRepository(private val tripDao: TripDao) {
    fun getAllTrips(): Flow<List<Trip>> = tripDao.getAllTrips()

    suspend fun getTripById(tripId: Long): Trip? = tripDao.getTripById(tripId)

    fun getTripByIdLiveData(tripId: Long) = tripDao.getTripByIdLiveData(tripId)

    suspend fun insertTrip(trip: Trip): Long = tripDao.insertTrip(trip)

    suspend fun updateTrip(trip: Trip) = tripDao.updateTrip(trip)

    suspend fun deleteTrip(trip: Trip) = tripDao.deleteTrip(trip)

    suspend fun deleteTripById(tripId: Long) = tripDao.deleteTripById(tripId)

    suspend fun getUnuploadedTrips(): List<Trip> = tripDao.getUnuploadedTrips()

    suspend fun markTripAsUploaded(tripId: Long, uploadDate: java.util.Date) =
        tripDao.markTripAsUploaded(tripId, uploadDate)
}

