package com.emreberkgoger.scoutbook.recyclerViewAdapter

import android.content.Intent
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.emreberkgoger.scoutbook.activities.PlayersActivity
import com.emreberkgoger.scoutbook.databinding.RecyclerRowTeamBinding
import com.emreberkgoger.scoutbook.models.Team

class TeamAdapter(private val teamList: ArrayList<Team>) : RecyclerView.Adapter<TeamAdapter.TeamHolder>() {
    class TeamHolder(val binding: RecyclerRowTeamBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeamHolder {
        val binding = RecyclerRowTeamBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TeamHolder(binding)
    }

    override fun getItemCount(): Int {
        return teamList.size
    }

    override fun onBindViewHolder(holder: TeamHolder, position: Int) {
        val team = teamList[position]
        holder.binding.recyclerViewTextView.text = team.name
        val bitmap = team.image?.let { BitmapFactory.decodeByteArray(it, 0, it.size) }
        holder.binding.recyclerViewImageView.setImageBitmap(bitmap)

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, PlayersActivity::class.java)
            intent.putExtra("teamId", team.id)
            holder.itemView.context.startActivity(intent)
        }
    }
}