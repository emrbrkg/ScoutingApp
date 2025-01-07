package com.emreberkgoger.scoutbook.activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.emreberkgoger.scoutbook.models.Player
import com.emreberkgoger.scoutbook.recyclerViewAdapter.PlayerAdapter
import com.emreberkgoger.scoutbook.databinding.ActivityMainBinding

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
        }
    }

    // add butonunun onClick() metodu:
    fun add(view : View) {
        val intent = Intent(this@MainActivity, DetailsActivity::class.java)
        intent.putExtra("info", "new")
        startActivity(intent)
    }
}