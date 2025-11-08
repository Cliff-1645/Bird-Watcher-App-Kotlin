package com.birdwatching.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.birdwatching.app.BirdWatchingApplication
import com.birdwatching.app.data.entity.Bird
import com.birdwatching.app.repository.BirdRepository
import kotlinx.coroutines.launch

class BirdViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: BirdRepository = (application as BirdWatchingApplication).birdRepository

    fun getBirdsByTripId(tripId: Long): LiveData<List<Bird>> = repository.getBirdsByTripId(tripId)

    fun insertBird(bird: Bird) = viewModelScope.launch {
        repository.insertBird(bird)
    }

    fun updateBird(bird: Bird) = viewModelScope.launch {
        repository.updateBird(bird)
    }

    fun deleteBird(bird: Bird) = viewModelScope.launch {
        repository.deleteBird(bird)
    }

    fun deleteBirdById(birdId: Long) = viewModelScope.launch {
        repository.deleteBirdById(birdId)
    }

    suspend fun getBirdsByTripIdSync(tripId: Long): List<Bird> =
        repository.getBirdsByTripIdSync(tripId)

    suspend fun getUnuploadedBirds(tripId: Long): List<Bird> =
        repository.getUnuploadedBirds(tripId)

    fun markBirdAsUploaded(birdId: Long) = viewModelScope.launch {
        repository.markBirdAsUploaded(birdId)
    }
}

