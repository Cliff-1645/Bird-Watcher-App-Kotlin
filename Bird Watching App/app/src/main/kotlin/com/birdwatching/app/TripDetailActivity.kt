package com.birdwatching.app

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.birdwatching.app.adapter.BirdAdapter
import com.birdwatching.app.data.entity.Bird
import com.birdwatching.app.data.entity.Trip
import com.birdwatching.app.databinding.ActivityTripDetailBinding
import com.birdwatching.app.service.AzureCloudService
import com.birdwatching.app.viewmodel.BirdViewModel
import com.birdwatching.app.viewmodel.TripViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class TripDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTripDetailBinding
    private lateinit var tripViewModel: TripViewModel
    private lateinit var birdViewModel: BirdViewModel
    private lateinit var birdAdapter: BirdAdapter
    private var tripId: Long = -1
    private var currentTrip: Trip? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTripDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        tripId = intent.getLongExtra("TRIP_ID", -1)
        if (tripId == -1L) {
            Toast.makeText(this, "Invalid trip", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        tripViewModel = ViewModelProvider(this)[TripViewModel::class.java]
        birdViewModel = ViewModelProvider(this)[BirdViewModel::class.java]

        birdAdapter = BirdAdapter { bird -> deleteBird(bird) }

        binding.recyclerViewBirds.apply {
            layoutManager = LinearLayoutManager(this@TripDetailActivity)
            adapter = birdAdapter
        }

        loadTripData()
        loadBirds()

        binding.buttonEditTrip.setOnClickListener {
            val intent = Intent(this, TripEntryActivity::class.java)
            intent.putExtra("TRIP_ID", tripId)
            startActivity(intent)
        }

        binding.buttonUpload.setOnClickListener {
            uploadTripToCloud()
        }

        binding.fabAddBird.setOnClickListener {
            val intent = Intent(this, BirdEntryActivity::class.java)
            intent.putExtra("TRIP_ID", tripId)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        loadTripData()
        loadBirds()
    }

    private fun loadTripData() {
        tripViewModel.getTripByIdLiveData(tripId).observe(this) { trip ->
            currentTrip = trip
            trip?.let {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                binding.textViewTripName.text = it.tripName
                binding.textViewDate.text = dateFormat.format(it.date)
                binding.textViewTime.text = it.time
                binding.textViewLocation.text = it.location
                binding.textViewDuration.text = it.duration
                if (!it.description.isNullOrEmpty()) {
                    binding.textViewDescription.text = it.description
                    binding.textViewDescription.visibility = android.view.View.VISIBLE
                } else {
                    binding.textViewDescription.visibility = android.view.View.GONE
                }
            }
        }
    }

    private fun loadBirds() {
        birdViewModel.getBirdsByTripId(tripId).observe(this) { birds ->
            birdAdapter.submitList(birds)
            if (birds.isEmpty()) {
                binding.textViewNoBirds.visibility = android.view.View.VISIBLE
                binding.recyclerViewBirds.visibility = android.view.View.GONE
            } else {
                binding.textViewNoBirds.visibility = android.view.View.GONE
                binding.recyclerViewBirds.visibility = android.view.View.VISIBLE
            }
        }
    }

    private fun deleteBird(bird: Bird) {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Delete Bird")
            .setMessage("Are you sure you want to delete this bird sighting?")
            .setPositiveButton("Delete") { _, _ ->
                birdViewModel.deleteBird(bird)
                Toast.makeText(this, "Bird deleted", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun uploadTripToCloud() {
        currentTrip?.let { trip ->
            binding.buttonUpload.isEnabled = false
            binding.buttonUpload.text = getString(R.string.uploading)

            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val azureService = AzureCloudService(this@TripDetailActivity)
                    val birds = birdViewModel.getBirdsByTripIdSync(tripId)
                    val birdsMap = mapOf(tripId to birds)

                    val result = azureService.uploadAllData(listOf(trip), birdsMap)

                    if (result.success) {
                        tripViewModel.markTripAsUploaded(tripId, java.util.Date())
                        birds.forEach { bird ->
                            birdViewModel.markBirdAsUploaded(bird.id)
                        }
                        Toast.makeText(
                            this@TripDetailActivity,
                            "Upload successful! ${result.successCount} items uploaded.",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(
                            this@TripDetailActivity,
                            "Upload completed with errors. ${result.successCount} succeeded, ${result.failureCount} failed.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(
                        this@TripDetailActivity,
                        "Upload failed: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                } finally {
                    binding.buttonUpload.isEnabled = true
                    binding.buttonUpload.text = getString(R.string.upload_to_cloud)
                }
            }
        }
    }
}

