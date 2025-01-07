package com.emreberkgoger.scoutbook.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.emreberkgoger.scoutbook.databinding.ActivityPlayerDetails2Binding

class PlayerDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlayerDetails2Binding
    private var playerId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerDetails2Binding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Oyuncunun ID'sini al
        playerId = intent.getIntExtra("playerId", -1)
        if (playerId != -1) {
            loadPlayerDetails(playerId) // Oyuncu detaylarını yükle
        }
        binding.editText.text.clear()
    }

    private fun loadPlayerDetails(playerId: Int) {
        try {
            val playerDatabase = this.openOrCreateDatabase("Players", MODE_PRIVATE, null)
            val cursor = playerDatabase.rawQuery(
                "SELECT * FROM players WHERE id = ?",
                arrayOf(playerId.toString())
            )

            if (cursor.moveToFirst()) {
                val detailsIndex = cursor.getColumnIndex("details")
                val details = cursor.getString(detailsIndex)
                binding.editText.setText(details) // Oyuncu detaylarını EditText'e yerleştir
                binding.textView.text = details // TextView'a da yaz
            }

            cursor.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun save(view: View) {
        try {
            val playerDatabase = this.openOrCreateDatabase("Players", MODE_PRIVATE, null)
            playerDatabase.execSQL("CREATE TABLE IF NOT EXISTS players (id INTEGER PRIMARY KEY, name VARCHAR, country VARCHAR, position VARCHAR, age VARCHAR, value VARCHAR, team VARCHAR, image BLOB, details VARCHAR)")
            val details = binding.editText.text.toString()

            if (playerId == -1) {
                // Yeni oyuncu ekle
                val sqlString = "INSERT INTO players (details) VALUES (?)"
                val statement = playerDatabase.compileStatement(sqlString)
                statement.bindString(1, details)
                statement.execute()

                // Yeni eklenen oyuncunun ID'sini al
                val cursor = playerDatabase.rawQuery("SELECT last_insert_rowid()", null)
                if (cursor.moveToFirst()) {
                    playerId = cursor.getInt(0) // Yeni oyuncunun ID'sini al
                }
                cursor.close()
            } else {
                // Mevcut oyuncuyu güncelle
                val sqlString = "UPDATE players SET details = ? WHERE id = ?"
                val statement = playerDatabase.compileStatement(sqlString)
                statement.bindString(1, details)
                statement.bindLong(2, playerId.toLong())
                statement.execute()
            }

            // TextView'a güncel detayları yaz
            binding.textView.text = details

            // Oyuncunun profil ekranına dön
            val intentToPlayerProfile = Intent()
            intentToPlayerProfile.putExtra("playerId", playerId) // Güncellenen ID'yi geri gönder
            setResult(RESULT_OK, intentToPlayerProfile)
            finish()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}
