package com.birdwatching.app

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.birdwatching.app.data.entity.Bird
import com.birdwatching.app.databinding.ActivityBirdEntryBinding
import com.birdwatching.app.util.LocationHelper
import com.birdwatching.app.viewmodel.BirdViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class BirdEntryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBirdEntryBinding
    private lateinit var birdViewModel: BirdViewModel
    private var tripId: Long = -1
    private var imagePath: String? = null
    private var latitude: Double? = null
    private var longitude: Double? = null

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
        binding = ActivityBirdEntryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        tripId = intent.getLongExtra("TRIP_ID", -1)
        if (tripId == -1L) {
            Toast.makeText(this, "Invalid trip", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        birdViewModel = ViewModelProvider(this)[BirdViewModel::class.java]

        setupClickListeners()
    }

    private fun setupClickListeners() {
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
                saveBird()
            }
        }
    }

    private fun validateInput(): Boolean {
        var isValid = true

        if (binding.editTextSpecies.text.toString().trim().isEmpty()) {
            binding.editTextSpecies.error = getString(R.string.required_field)
            isValid = false
        }

        if (binding.editTextLocation.text.toString().trim().isEmpty()) {
            binding.editTextLocation.error = getString(R.string.required_field)
            isValid = false
        }

        if (binding.editTextQuantity.text.toString().trim().isEmpty()) {
            binding.editTextQuantity.error = getString(R.string.required_field)
            isValid = false
        }

        val quantity = binding.editTextQuantity.text.toString().toIntOrNull()
        if (quantity == null || quantity <= 0) {
            binding.editTextQuantity.error = "Please enter a valid quantity"
            isValid = false
        }

        return isValid
    }

    private fun saveBird() {
        val quantity = binding.editTextQuantity.text.toString().toIntOrNull() ?: 1

        val bird = Bird(
            tripId = tripId,
            species = binding.editTextSpecies.text.toString().trim(),
            location = binding.editTextLocation.text.toString().trim(),
            quantity = quantity,
            comments = binding.editTextComments.text.toString().trim().takeIf { it.isNotEmpty() },
            imagePath = imagePath,
            latitude = latitude,
            longitude = longitude
        )

        CoroutineScope(Dispatchers.Main).launch {
            birdViewModel.insertBird(bird)
            Toast.makeText(this@BirdEntryActivity, "Bird saved successfully", Toast.LENGTH_SHORT).show()
            finish()
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
        val photoFile = File(getExternalFilesDir(null), "bird_photo_${System.currentTimeMillis()}.jpg")
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

