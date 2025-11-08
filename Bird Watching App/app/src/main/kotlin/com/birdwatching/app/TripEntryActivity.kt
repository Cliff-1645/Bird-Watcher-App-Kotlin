package com.birdwatching.app

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.birdwatching.app.data.entity.Trip
import com.birdwatching.app.databinding.ActivityTripEntryBinding
import com.birdwatching.app.util.DatePickerHelper
import com.birdwatching.app.util.LocationHelper
import com.birdwatching.app.util.TimePickerHelper
import com.birdwatching.app.viewmodel.TripViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TripEntryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTripEntryBinding
    private lateinit var tripViewModel: TripViewModel
    private var tripId: Long? = null
    private var imagePath: String? = null
    private var latitude: Double? = null
    private var longitude: Double? = null

    private val datePickerHelper = DatePickerHelper(this)
    private val timePickerHelper = TimePickerHelper(this)
    private val locationHelper = LocationHelper(this)

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        ) {
            getCurrentLocation()
        } else {
            Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    private val takePictureLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && imagePath != null) {
            binding.imageViewPhoto.setImageURI(Uri.fromFile(File(imagePath)))
            binding.imageViewPhoto.visibility = android.view.View.VISIBLE
        }
    }

    private val selectPictureLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            imagePath = it.toString()
            binding.imageViewPhoto.setImageURI(it)
            binding.imageViewPhoto.visibility = android.view.View.VISIBLE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTripEntryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tripViewModel = ViewModelProvider(this)[TripViewModel::class.java]

        tripId = intent.getLongExtra("TRIP_ID", -1)
        if (tripId != null && tripId!! > 0) {
            loadTripData(tripId!!)
        }

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.editTextDate.setOnClickListener {
            datePickerHelper.showDatePicker { date ->
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                binding.editTextDate.setText(dateFormat.format(date))
            }
        }

        binding.editTextTime.setOnClickListener {
            timePickerHelper.showTimePicker { hour, minute ->
                binding.editTextTime.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute))
            }
        }

        binding.buttonUseCurrentLocation.setOnClickListener {
            requestLocationPermission()
        }

        binding.buttonTakePhoto.setOnClickListener {
            takePicture()
        }

        binding.buttonSelectPhoto.setOnClickListener {
            selectPicture()
        }

        binding.buttonSave.setOnClickListener {
            if (validateInput()) {
                saveTrip()
            }
        }
    }

    private fun validateInput(): Boolean {
        var isValid = true

        if (binding.editTextTripName.text.toString().trim().isEmpty()) {
            binding.editTextTripName.error = getString(R.string.required_field)
            isValid = false
        }

        if (binding.editTextDate.text.toString().trim().isEmpty()) {
            binding.editTextDate.error = getString(R.string.required_field)
            isValid = false
        }

        if (binding.editTextTime.text.toString().trim().isEmpty()) {
            binding.editTextTime.error = getString(R.string.required_field)
            isValid = false
        }

        if (binding.editTextLocation.text.toString().trim().isEmpty()) {
            binding.editTextLocation.error = getString(R.string.required_field)
            isValid = false
        }

        if (binding.editTextDuration.text.toString().trim().isEmpty()) {
            binding.editTextDuration.error = getString(R.string.required_field)
            isValid = false
        }

        return isValid
    }

    private fun saveTrip() {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = dateFormat.parse(binding.editTextDate.text.toString()) ?: Date()

        val trip = Trip(
            id = tripId ?: 0,
            tripName = binding.editTextTripName.text.toString().trim(),
            date = date,
            time = binding.editTextTime.text.toString().trim(),
            location = binding.editTextLocation.text.toString().trim(),
            duration = binding.editTextDuration.text.toString().trim(),
            description = binding.editTextDescription.text.toString().trim().takeIf { it.isNotEmpty() },
            weather = binding.editTextWeather.text.toString().trim().takeIf { it.isNotEmpty() },
            temperature = binding.editTextTemperature.text.toString().trim().takeIf { it.isNotEmpty() },
            companionCount = binding.editTextCompanionCount.text.toString().toIntOrNull() ?: 0,
            imagePath = imagePath,
            latitude = latitude,
            longitude = longitude
        )

        CoroutineScope(Dispatchers.Main).launch {
            if (tripId != null && tripId!! > 0) {
                tripViewModel.updateTrip(trip)
            } else {
                tripViewModel.insertTrip(trip)
            }
            Toast.makeText(this@TripEntryActivity, "Trip saved successfully", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun loadTripData(tripId: Long) {
        CoroutineScope(Dispatchers.Main).launch {
            val trip = tripViewModel.getTripById(tripId)
            trip?.let {
                binding.editTextTripName.setText(it.tripName)
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                binding.editTextDate.setText(dateFormat.format(it.date))
                binding.editTextTime.setText(it.time)
                binding.editTextLocation.setText(it.location)
                binding.editTextDuration.setText(it.duration)
                it.description?.let { desc -> binding.editTextDescription.setText(desc) }
                it.weather?.let { weather -> binding.editTextWeather.setText(weather) }
                it.temperature?.let { temp -> binding.editTextTemperature.setText(temp) }
                binding.editTextCompanionCount.setText(it.companionCount.toString())
                imagePath = it.imagePath
                latitude = it.latitude
                longitude = it.longitude

                if (!it.imagePath.isNullOrEmpty()) {
                    binding.imageViewPhoto.setImageURI(Uri.parse(it.imagePath))
                    binding.imageViewPhoto.visibility = android.view.View.VISIBLE
                }
            }
        }
    }

    private fun requestLocationPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                getCurrentLocation()
            }
            else -> {
                requestPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }
    }

    private fun getCurrentLocation() {
        locationHelper.getCurrentLocation { location ->
            location?.let {
                latitude = it.latitude
                longitude = it.longitude
                binding.editTextLocation.setText("${it.latitude}, ${it.longitude}")
                Toast.makeText(this, "Location updated", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun takePicture() {
        val photoFile = File(getExternalFilesDir(null), "trip_photo_${System.currentTimeMillis()}.jpg")
        imagePath = photoFile.absolutePath
        val photoURI = FileProvider.getUriForFile(
            this,
            "${packageName}.fileprovider",
            photoFile
        )
        takePictureLauncher.launch(photoURI)
    }

    private fun selectPicture() {
        selectPictureLauncher.launch("image/*")
    }
}

