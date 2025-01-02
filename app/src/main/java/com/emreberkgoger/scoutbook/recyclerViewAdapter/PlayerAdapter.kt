package com.emreberkgoger.scoutbook.recyclerViewAdapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.emreberkgoger.scoutbook.activities.DetailsActivity
import com.emreberkgoger.scoutbook.databinding.RecyclerRowBinding
import com.emreberkgoger.scoutbook.models.Player

class PlayerAdapter(val playerList : ArrayList<Player>) : RecyclerView.Adapter<PlayerAdapter.PlayerHolder>() {

    class PlayerHolder(val binding: RecyclerRowBinding) : RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerHolder {
        val binding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlayerHolder(binding)
    }

    override fun getItemCount(): Int {
        return playerList.size
    }

    override fun onBindViewHolder(holder: PlayerHolder, position: Int) {
        holder.binding.recyclerViewTextView.text = playerList.get(position).name
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, DetailsActivity::class.java)
            intent.putExtra("info", "old")
            intent.putExtra("id", playerList[position].id)
            holder.itemView.context.startActivity(intent)
        }


    }
}