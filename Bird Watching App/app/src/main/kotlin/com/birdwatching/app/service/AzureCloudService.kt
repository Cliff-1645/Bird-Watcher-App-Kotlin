package com.birdwatching.app.service

import android.content.Context
import android.util.Log
import com.azure.storage.blob.BlobClient
import com.azure.storage.blob.BlobContainerClient
import com.azure.storage.blob.BlobServiceClient
import com.azure.storage.blob.BlobServiceClientBuilder
import com.birdwatching.app.data.entity.Bird
import com.birdwatching.app.data.entity.Trip
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayInputStream

class AzureCloudService(context: Context) {
    private val gson = Gson()
    
    // TODO: Replace with your Azure Storage Account connection string
    // You can get this from Azure Portal -> Storage Account -> Access Keys
    private val connectionString = "DefaultEndpointsProtocol=https;AccountName=YOUR_ACCOUNT_NAME;AccountKey=YOUR_ACCOUNT_KEY;EndpointSuffix=core.windows.net"
    private val containerName = "bird-watching-data"
    
    private val blobServiceClient: BlobServiceClient = try {
        BlobServiceClientBuilder()
            .connectionString(connectionString)
            .buildClient()
    } catch (e: Exception) {
        Log.e("AzureCloudService", "Failed to initialize Azure client", e)
        throw e
    }

    private val containerClient: BlobContainerClient = try {
        val container = blobServiceClient.getBlobContainerClient(containerName)
        if (!container.exists()) {
            container.create()
        }
        container
    } catch (e: Exception) {
        Log.e("AzureCloudService", "Failed to get/create container", e)
        throw e
    }

    suspend fun uploadTrip(trip: Trip): Boolean = withContext(Dispatchers.IO) {
        try {
            val tripJson = gson.toJson(trip)
            val blobName = "trips/trip_${trip.id}.json"
            val blobClient: BlobClient = containerClient.getBlobClient(blobName)
            
            blobClient.upload(
                ByteArrayInputStream(tripJson.toByteArray()),
                tripJson.toByteArray().size.toLong()),
                true
            )
            
            Log.d("AzureCloudService", "Trip ${trip.id} uploaded successfully")
            true
        } catch (e: Exception) {
            Log.e("AzureCloudService", "Failed to upload trip ${trip.id}", e)
            false
        }
    }

    suspend fun uploadBird(bird: Bird): Boolean = withContext(Dispatchers.IO) {
        try {
            val birdJson = gson.toJson(bird)
            val blobName = "birds/trip_${bird.tripId}/bird_${bird.id}.json"
            val blobClient: BlobClient = containerClient.getBlobClient(blobName)
            
            blobClient.upload(
                ByteArrayInputStream(birdJson.toByteArray()),
                birdJson.toByteArray().size.toLong()),
                true
            )
            
            Log.d("AzureCloudService", "Bird ${bird.id} uploaded successfully")
            true
        } catch (e: Exception) {
            Log.e("AzureCloudService", "Failed to upload bird ${bird.id}", e)
            false
        }
    }

    suspend fun uploadAllData(trips: List<Trip>, birdsMap: Map<Long, List<Bird>>): UploadResult {
        return withContext(Dispatchers.IO) {
            var successCount = 0
            var failureCount = 0
            val errors = mutableListOf<String>()

            try {
                // Upload trips
                trips.forEach { trip ->
                    if (uploadTrip(trip)) {
                        successCount++
                    } else {
                        failureCount++
                        errors.add("Failed to upload trip: ${trip.tripName}")
                    }
                }

                // Upload birds
                birdsMap.forEach { (tripId, birds) ->
                    birds.forEach { bird ->
                        if (uploadBird(bird)) {
                            successCount++
                        } else {
                            failureCount++
                            errors.add("Failed to upload bird: ${bird.species}")
                        }
                    }
                }

                UploadResult(
                    success = failureCount == 0,
                    successCount = successCount,
                    failureCount = failureCount,
                    errors = errors
                )
            } catch (e: Exception) {
                Log.e("AzureCloudService", "Failed to upload all data", e)
                UploadResult(
                    success = false,
                    successCount = successCount,
                    failureCount = failureCount,
                    errors = errors + "Upload failed: ${e.message}"
                )
            }
        }
    }

    data class UploadResult(
        val success: Boolean,
        val successCount: Int,
        val failureCount: Int,
        val errors: List<String>
    )
}

