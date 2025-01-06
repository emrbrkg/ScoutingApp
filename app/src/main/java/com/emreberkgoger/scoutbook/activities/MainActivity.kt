package com.emreberkgoger.scoutbook.activities

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import com.emreberkgoger.scoutbook.R
import com.emreberkgoger.scoutbook.models.Player
import com.emreberkgoger.scoutbook.recyclerViewAdapter.PlayerAdapter
import com.emreberkgoger.scoutbook.databinding.ActivityMainBinding
import com.emreberkgoger.scoutbook.models.Team

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private lateinit var playerList : ArrayList<Player>
    private lateinit var playerAdapter : PlayerAdapter

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        // view binding işlemleri
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // RecyclerView adapter yapısının tanımlanması
        playerList = ArrayList<Player>()
        playerAdapter = PlayerAdapter(playerList)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = playerAdapter

        val teamsButton: Button = findViewById(R.id.teamsButton)
        teamsButton.setOnClickListener {
            // TeamList activity ekranını başlat
            val intent = Intent(this@MainActivity, TeamListActivity::class.java)
            startActivity(intent)
        }

        try {
            // Player database'inden oyuncuların RecyclerView içinde gösterilmesi işlemi
            val PlayerDatabase = this.openOrCreateDatabase("Players", MODE_PRIVATE, null)
            val cursor = PlayerDatabase.rawQuery("SELECT * FROM players", null)
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
        } catch (e : Exception){
            e.printStackTrace()
            Log.e("DatabaseError", "Error opening database", e)
        }

        setupTeamDatabase()


    }

    private fun setupTeamDatabase() {
        val database = this.openOrCreateDatabase("Teams", MODE_PRIVATE, null)

        // Takımlar tablosunu oluştur
        database.execSQL("CREATE TABLE IF NOT EXISTS teams (id INTEGER PRIMARY KEY, name VARCHAR, image BLOB)")

        // Tabloda veri olup olmadığını kontrol et
        val cursor = database.rawQuery("SELECT COUNT(*) FROM teams", null)
        cursor.moveToFirst()
        val count = cursor.getInt(0)
        cursor.close()

        // Eğer tablo boşsa örnek takımları ekle
        if (count == 0) {
            val teams = generateSampleTeams()
            for (team in teams) {
                val values = ContentValues()
                values.put("name", team.name)
                values.put("image", team.image) // Görseller için örnek veriyi base64 ya da byte[] olarak koyabilirsiniz
                database.insert("teams", null, values)
            }
        }
    }

    private fun generateSampleTeams(): List<Team> {
        val teams = ArrayList<Team>()
        for (i in 1..20) {
            val name = "Team $i"
            val image: ByteArray? = null // Örnek olarak resim koyabilirsiniz
            teams.add(Team(name, i, image))
        }
        return teams
    }

    // add butonunun onClick() metodu:
    fun add(view : View) {
        val intent = Intent(this@MainActivity, DetailsActivity::class.java)
        intent.putExtra("info", "new")
        startActivity(intent)
    }
}