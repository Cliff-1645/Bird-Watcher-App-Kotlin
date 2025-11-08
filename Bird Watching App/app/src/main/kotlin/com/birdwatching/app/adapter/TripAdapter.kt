package com.birdwatching.app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.birdwatching.app.data.entity.Trip
import com.birdwatching.app.databinding.ItemTripBinding
import java.text.SimpleDateFormat
import java.util.Locale

class TripAdapter(
    private val onTripClick: (Trip) -> Unit,
    private val onEditClick: (Trip) -> Unit,
    private val onDeleteClick: (Trip) -> Unit
) : ListAdapter<Trip, TripAdapter.TripViewHolder>(TripDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripViewHolder {
        val binding = ItemTripBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TripViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TripViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TripViewHolder(private val binding: ItemTripBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        fun bind(trip: Trip) {
            binding.textViewTripName.text = trip.tripName
            binding.textViewDate.text = dateFormat.format(trip.date)
            binding.textViewTime.text = trip.time
            binding.textViewLocation.text = trip.location
            binding.textViewDuration.text = trip.duration

            binding.root.setOnClickListener {
                onTripClick(trip)
            }

            binding.buttonEdit.setOnClickListener {
                onEditClick(trip)
            }

            binding.buttonDelete.setOnClickListener {
                onDeleteClick(trip)
            }
        }
    }

    class TripDiffCallback : DiffUtil.ItemCallback<Trip>() {
        override fun areItemsTheSame(oldItem: Trip, newItem: Trip): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Trip, newItem: Trip): Boolean {
            return oldItem == newItem
        }
    }
}

