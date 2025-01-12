package com.emreberkgoger.scoutbook.recyclerViewAdapter

import android.content.Intent
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.emreberkgoger.scoutbook.R
import com.emreberkgoger.scoutbook.activities.TeamDetailsActivity
import com.emreberkgoger.scoutbook.databinding.RecyclerRowTeamBinding
import com.emreberkgoger.scoutbook.models.Team

class TeamAdapter(private val teamList: ArrayList<Team>) :
    RecyclerView.Adapter<TeamAdapter.TeamHolder>() {

    class TeamHolder(val binding: RecyclerRowTeamBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeamHolder {
        val binding = RecyclerRowTeamBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TeamHolder(binding)
    }

    override fun getItemCount(): Int {
        return teamList.size
    }

    override fun onBindViewHolder(holder: TeamHolder, position: Int){
        val team = teamList[position]

        // Takımın ismini göster
        holder.binding.recyclerViewTextView3.text = team.name

        // Takımın logosunu göster
        val bitmap = team.image?.let{ BitmapFactory.decodeByteArray(team.image, 0, it.size) }
        holder.binding.recyclerViewImageView3.setImageBitmap(bitmap)

        // Takım tıklanınca bilgiler kısmına geç
        holder.itemView.setOnClickListener {
            val intentToTeamDetail = Intent(holder.itemView.context, TeamDetailsActivity::class.java)
            intentToTeamDetail.putExtra("teamName", team.name)
            holder.itemView.context.startActivity(intentToTeamDetail)
        }

    }

}