package com.emreberkgoger.scoutbook.activities

import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.emreberkgoger.scoutbook.databinding.ActivityPlayerDetails2Binding
import java.io.ByteArrayOutputStream

class PlayerDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlayerDetails2Binding
    var selectedBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerDetails2Binding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val playerId = intent.getIntExtra("playerId", -1) // Gönderilen id'yi al
        if (playerId != -1) {
            loadPlayerDetails(playerId) // Oyuncu detaylarını yükle
        }
    }

    private fun loadPlayerDetails(playerId: Int) {
        try {
            val playerDatabase = this.openOrCreateDatabase("Players", MODE_PRIVATE, null)
            val cursor = playerDatabase.rawQuery("SELECT * FROM players WHERE id = ?", arrayOf(playerId.toString()))

            if (cursor.moveToFirst()) {
                val detailsIndex = cursor.getColumnIndex("details")
                val details = cursor.getString(detailsIndex)
                binding.editText.setText(details) // Oyuncu detaylarını editText'e yerleştir
            }

            cursor.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun save(view: View) {
        val playerId = intent.getIntExtra("playerId", -1) // Gönderilen id'yi kontrol et

        try {
            val playerDatabase = this.openOrCreateDatabase("Players", MODE_PRIVATE, null)
            playerDatabase.execSQL("CREATE TABLE IF NOT EXISTS players (id INTEGER PRIMARY KEY, details VARCHAR)")

            if (playerId == -1) {
                // Yeni oyuncu ekle
                val sqlString = "INSERT INTO players (details) VALUES (?)"
                val statement = playerDatabase.compileStatement(sqlString)
                statement.bindString(1, binding.editText.text.toString())
                statement.execute()
            } else {
                // Mevcut oyuncuyu güncelle
                val sqlString = "UPDATE players SET details = ? WHERE id = ?"
                val statement = playerDatabase.compileStatement(sqlString)
                statement.bindString(1, binding.editText.text.toString())
                statement.bindLong(2, playerId.toLong())
                statement.execute()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Oyuncunun profil ekranına dön
        val intentToPlayerProfile = Intent(this@PlayerDetailsActivity, DetailsActivity::class.java)
        intentToPlayerProfile.putExtra("playerId", playerId) // Oyuncu id'sini ekle
        startActivity(intentToPlayerProfile)
    }

    fun makeSmallerBitMap(image: Bitmap, maximumSize: Int): Bitmap {
        var width = image.width
        var height = image.height

        val bitmapRatio: Double = width.toDouble() / height.toDouble()

        if (bitmapRatio > 1) {
            width = maximumSize
            val scaledHeight = width / bitmapRatio
            height = scaledHeight.toInt()
        } else {
            height = maximumSize
            val scaledWidth = height * bitmapRatio
            width = scaledWidth.toInt()
        }
        return Bitmap.createScaledBitmap(image, width, height, true)
    }
}