package com.emreberkgoger.scoutbook.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.emreberkgoger.scoutbook.databinding.ActivityPlayersBinding
import com.emreberkgoger.scoutbook.models.Player
import com.emreberkgoger.scoutbook.recyclerViewAdapter.PlayerAdapter

class PlayersActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlayersBinding
    private lateinit var playerList: ArrayList<Player>
    private lateinit var playerAdapter: PlayerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // RecyclerView setup
        playerList = ArrayList()
        playerAdapter = PlayerAdapter(playerList)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = playerAdapter

        // Get team ID
        val teamId = intent.getIntExtra("teamId", -1)

        // Load players of the selected team from database
        try {
            val playerDatabase = this.openOrCreateDatabase("ScoutBook", MODE_PRIVATE, null)
            val cursor = playerDatabase.rawQuery("SELECT * FROM players WHERE teamId = ?", arrayOf(teamId.toString()))
            val nameIndex = cursor.getColumnIndex("name")
            val idIndex = cursor.getColumnIndex("id")
            val imageIndex = cursor.getColumnIndex("image")

            while (cursor.moveToNext()) {
                val name = cursor.getString(nameIndex)
                val id = cursor.getInt(idIndex)
                val image = cursor.getBlob(imageIndex)
                val player = Player(name, id, image)
                playerList.add(player)
            }
            playerAdapter.notifyDataSetChanged()
            cursor.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}