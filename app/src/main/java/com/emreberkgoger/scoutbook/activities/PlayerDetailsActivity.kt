package com.emreberkgoger.scoutbook.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.emreberkgoger.scoutbook.databinding.ActivityPlayerDetails2Binding

class PlayerDetailsActivity : AppCompatActivity() {
    // ViewBinding ile Activity'nin bileşenlerini bağlamak için değişken
    private lateinit var binding: ActivityPlayerDetails2Binding

    // Oyuncunun ID'sini saklamak için bir değişken (varsayılan olarak -1)
    private var playerId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // ViewBinding kullanarak XML layout'unu bağla
        binding = ActivityPlayerDetails2Binding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Intent ile gelen oyuncu ID'sini al
        playerId = intent.getIntExtra("playerId", -1)

        // Eğer geçerli bir oyuncu ID'si varsa, oyuncu detaylarını yükle
        if (playerId != -1) {
            loadPlayerDetails(playerId)
        }

        // EditText alanını temizle
        binding.editText.text.clear()
    }

    /**
     * Veritabanından oyuncunun detaylarını yükler.
     * @param playerId Oyuncunun ID'si
     */
    private fun loadPlayerDetails(playerId: Int) {
        try {
            // Players adlı SQLite veritabanını aç
            val playerDatabase = this.openOrCreateDatabase("Players", MODE_PRIVATE, null)

            // Oyuncunun detaylarını ID'sine göre sorgula
            val cursor = playerDatabase.rawQuery(
                "SELECT * FROM players WHERE id = ?",
                arrayOf(playerId.toString())
            )

            // Eğer sonuç varsa oyuncu detaylarını al
            if (cursor.moveToFirst()) {
                val detailsIndex = cursor.getColumnIndex("details")
                val details = cursor.getString(detailsIndex)

                // EditText ve TextView bileşenlerini doldur
                binding.editText.setText(details)
                binding.textView.text = details
            }

            // Cursor'u kapat
            cursor.close()
        } catch (e: Exception) {
            // Hata oluşursa hata mesajını yazdır
            e.printStackTrace()
        }
    }

    /**
     * Kullanıcının yaptığı değişiklikleri kaydeder.
     * Yeni bir oyuncu ekler veya mevcut bir oyuncunun detaylarını günceller.
     * @param view Kaydetme butonu
     */
    fun save(view: View) {
        try {
            // Players adlı SQLite veritabanını aç
            val playerDatabase = this.openOrCreateDatabase("Players", MODE_PRIVATE, null)

            // Eğer tablo yoksa oluştur
            playerDatabase.execSQL("CREATE TABLE IF NOT EXISTS players (id INTEGER PRIMARY KEY, name VARCHAR, country VARCHAR, position VARCHAR, age VARCHAR, value VARCHAR, team VARCHAR, image BLOB, details VARCHAR)")

            // Kullanıcının EditText'e girdiği detayları al
            val details = binding.editText.text.toString()

            if (playerId == -1) {
                // Yeni bir oyuncu ekle
                val sqlString = "INSERT INTO players (details) VALUES (?)"
                val statement = playerDatabase.compileStatement(sqlString)

                // Kullanıcı detaylarını veritabanına ekle
                statement.bindString(1, details)
                statement.execute()

                // Yeni eklenen oyuncunun ID'sini al
                val cursor = playerDatabase.rawQuery("SELECT last_insert_rowid()", null)
                if (cursor.moveToFirst()) {
                    playerId = cursor.getInt(0) // Yeni oyuncunun ID'si
                }
                cursor.close()
            } else {
                // Mevcut oyuncunun detaylarını güncelle
                val sqlString = "UPDATE players SET details = ? WHERE id = ?"
                val statement = playerDatabase.compileStatement(sqlString)

                // Detayları ve ID'yi bağla
                statement.bindString(1, details)
                statement.bindLong(2, playerId.toLong())
                statement.execute()
            }

            // TextView'a güncellenen detayları yaz
            binding.textView.text = details

            // Oyuncunun profil ekranına geri dön
            val intentToPlayerProfile = Intent()
            intentToPlayerProfile.putExtra("playerId", playerId) // Güncellenen ID'yi geri gönder
            setResult(RESULT_OK, intentToPlayerProfile)

            // Bu Activity'yi kapat
            finish()
        } catch (e: Exception) {
            // Hata oluşursa hata mesajını yazdır
            e.printStackTrace()
        }
    }
}