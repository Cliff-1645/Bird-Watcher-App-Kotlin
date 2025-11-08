package com.birdwatching.app.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.birdwatching.app.data.converter.DateConverter
import com.birdwatching.app.data.dao.BirdDao
import com.birdwatching.app.data.dao.TripDao
import com.birdwatching.app.data.entity.Bird
import com.birdwatching.app.data.entity.Trip

@Database(
    entities = [Trip::class, Bird::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateConverter::class)
abstract class BirdWatchingDatabase : RoomDatabase() {
    abstract fun tripDao(): TripDao
    abstract fun birdDao(): BirdDao

    companion object {
        @Volatile
        private var INSTANCE: BirdWatchingDatabase? = null

        fun getDatabase(context: Context): BirdWatchingDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BirdWatchingDatabase::class.java,
                    "bird_watching_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

