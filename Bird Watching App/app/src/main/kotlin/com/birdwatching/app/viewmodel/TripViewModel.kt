package com.birdwatching.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.birdwatching.app.BirdWatchingApplication
import com.birdwatching.app.data.entity.Trip
import com.birdwatching.app.repository.TripRepository
import kotlinx.coroutines.launch

class TripViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TripRepository = (application as BirdWatchingApplication).tripRepository

    val allTrips: LiveData<List<Trip>> = repository.getAllTrips()

    fun insertTrip(trip: Trip) = viewModelScope.launch {
        repository.insertTrip(trip)
    }

    fun updateTrip(trip: Trip) = viewModelScope.launch {
        repository.updateTrip(trip)
    }

    fun deleteTrip(trip: Trip) = viewModelScope.launch {
        repository.deleteTrip(trip)
    }

    fun deleteTripById(tripId: Long) = viewModelScope.launch {
        repository.deleteTripById(tripId)
    }

    suspend fun getTripById(tripId: Long): Trip? = repository.getTripById(tripId)

    fun getTripByIdLiveData(tripId: Long) = repository.getTripByIdLiveData(tripId)

    suspend fun getUnuploadedTrips(): List<Trip> = repository.getUnuploadedTrips()

    fun markTripAsUploaded(tripId: Long, uploadDate: java.util.Date) = viewModelScope.launch {
        repository.markTripAsUploaded(tripId, uploadDate)
    }
}

