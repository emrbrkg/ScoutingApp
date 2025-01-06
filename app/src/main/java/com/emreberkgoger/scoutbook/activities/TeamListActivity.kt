package com.emreberkgoger.scoutbook.activities

import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.emreberkgoger.scoutbook.databinding.ActivityTeamListBinding
import com.emreberkgoger.scoutbook.models.Team
import com.emreberkgoger.scoutbook.recyclerViewAdapter.TeamAdapter

class TeamListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTeamListBinding
    private lateinit var teamList: ArrayList<Team>
    private lateinit var teamAdapter: TeamAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTeamListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // RecyclerView yapılandırma
        teamList = ArrayList()
        teamAdapter = TeamAdapter(teamList)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = teamAdapter

        // Takımları veritabanından yükle
        loadTeamsFromDatabase()
    }

    private fun loadTeamsFromDatabase() {
        try {
            val database = this.openOrCreateDatabase("Teams", MODE_PRIVATE, null)
            val cursor = database.rawQuery("SELECT * FROM teams", null)

            val nameIndex = cursor.getColumnIndex("name")
            val idIndex = cursor.getColumnIndex("id")
            val imageIndex = cursor.getColumnIndex("image")

            while (cursor.moveToNext()) {
                val name = cursor.getString(nameIndex)
                val id = cursor.getInt(idIndex)
                val image = cursor.getBlob(imageIndex)
                val team = Team(name, id, image)
                teamList.add(team)
            }

            teamAdapter.notifyDataSetChanged()
            cursor.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}