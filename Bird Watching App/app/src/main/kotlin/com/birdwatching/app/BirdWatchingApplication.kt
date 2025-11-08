package com.birdwatching.app

import android.app.Application
import com.birdwatching.app.data.database.BirdWatchingDatabase
import com.birdwatching.app.repository.BirdRepository
import com.birdwatching.app.repository.TripRepository

class BirdWatchingApplication : Application() {
    val database by lazy { BirdWatchingDatabase.getDatabase(this) }
    val tripRepository by lazy { TripRepository(database.tripDao()) }
    val birdRepository by lazy { BirdRepository(database.birdDao()) }
}

