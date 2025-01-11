package com.emreberkgoger.scoutbook.recyclerViewAdapter

import android.content.Intent
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.emreberkgoger.scoutbook.activities.DetailsActivity
import com.emreberkgoger.scoutbook.databinding.RecyclerRowBinding
import com.emreberkgoger.scoutbook.models.Player

// RecyclerView için kullanılan adapter sınıfı
class PlayerAdapter(private val playerList: ArrayList<Player>) :
    RecyclerView.Adapter<PlayerAdapter.PlayerHolder>() {

    class PlayerHolder(val binding: RecyclerRowBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerHolder {
        val binding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlayerHolder(binding)
    }

    override fun getItemCount(): Int {
        return playerList.size
    }

    override fun onBindViewHolder(holder: PlayerHolder, position: Int) {
        val player = playerList[position]

        // Oyuncunun ismini göster
        holder.binding.recyclerViewTextView.text = player.name

        // Oyuncunun profil görselini göster
        val bitmap = player.image?.let { BitmapFactory.decodeByteArray(player.image, 0, it.size) }
        holder.binding.recyclerViewImageView.setImageBitmap(bitmap)

        // Oyuncu tıklanınca detaylar ekranına geç
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, DetailsActivity::class.java)
            intent.putExtra("info", "old")
            intent.putExtra("id", player.id)
            holder.itemView.context.startActivity(intent)
        }
    }
}