package com.birdwatching.app

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.birdwatching.app.adapter.TripAdapter
import com.birdwatching.app.data.entity.Trip
import com.birdwatching.app.databinding.ActivityMainBinding
import com.birdwatching.app.viewmodel.TripViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var tripViewModel: TripViewModel
    private lateinit var tripAdapter: TripAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        tripViewModel = ViewModelProvider(this)[TripViewModel::class.java]
        tripAdapter = TripAdapter(
            onTripClick = { trip -> openTripDetail(trip.id) },
            onEditClick = { trip -> editTrip(trip.id) },
            onDeleteClick = { trip -> deleteTrip(trip) }
        )

        binding.recyclerViewTrips.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = tripAdapter
        }

        tripViewModel.allTrips.observe(this) { trips ->
            tripAdapter.submitList(trips)
            if (trips.isEmpty()) {
                binding.textViewEmpty.visibility = android.view.View.VISIBLE
                binding.recyclerViewTrips.visibility = android.view.View.GONE
            } else {
                binding.textViewEmpty.visibility = android.view.View.GONE
                binding.recyclerViewTrips.visibility = android.view.View.VISIBLE
            }
        }

        binding.fabAddTrip.setOnClickListener {
            val intent = Intent(this, TripEntryActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_upload_all -> {
                uploadAllTrips()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun openTripDetail(tripId: Long) {
        val intent = Intent(this, TripDetailActivity::class.java)
        intent.putExtra("TRIP_ID", tripId)
        startActivity(intent)
    }

    private fun editTrip(tripId: Long) {
        val intent = Intent(this, TripEntryActivity::class.java)
        intent.putExtra("TRIP_ID", tripId)
        startActivity(intent)
    }

    private fun deleteTrip(trip: Trip) {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Delete Trip")
            .setMessage("Are you sure you want to delete this trip?")
            .setPositiveButton("Delete") { _, _ ->
                tripViewModel.deleteTrip(trip)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun uploadAllTrips() {
        // This will be implemented in TripDetailActivity
        // For now, just show a message
        android.widget.Toast.makeText(this, "Upload functionality available in trip details", android.widget.Toast.LENGTH_SHORT).show()
    }
}

