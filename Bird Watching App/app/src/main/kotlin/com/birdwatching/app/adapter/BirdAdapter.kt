package com.birdwatching.app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.birdwatching.app.data.entity.Bird
import com.birdwatching.app.databinding.ItemBirdBinding

class BirdAdapter(
    private val onDeleteClick: (Bird) -> Unit
) : ListAdapter<Bird, BirdAdapter.BirdViewHolder>(BirdDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BirdViewHolder {
        val binding = ItemBirdBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BirdViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BirdViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class BirdViewHolder(private val binding: ItemBirdBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(bird: Bird) {
            binding.textViewSpecies.text = bird.species
            binding.textViewLocation.text = bird.location
            binding.textViewQuantity.text = bird.quantity.toString()

            if (!bird.comments.isNullOrEmpty()) {
                binding.textViewComments.text = bird.comments
                binding.textViewComments.visibility = android.view.View.VISIBLE
            } else {
                binding.textViewComments.visibility = android.view.View.GONE
            }

            binding.buttonDelete.setOnClickListener {
                onDeleteClick(bird)
            }
        }
    }

    class BirdDiffCallback : DiffUtil.ItemCallback<Bird>() {
        override fun areItemsTheSame(oldItem: Bird, newItem: Bird): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Bird, newItem: Bird): Boolean {
            return oldItem == newItem
        }
    }
}

