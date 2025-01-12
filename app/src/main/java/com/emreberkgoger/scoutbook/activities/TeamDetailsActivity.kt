package com.emreberkgoger.scoutbook.activities

import android.database.sqlite.SQLiteDatabase
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.emreberkgoger.scoutbook.databinding.ActivityTeamDetailsBinding

class TeamDetailsActivity : AppCompatActivity()  {
    private lateinit var binding: ActivityTeamDetailsBinding // Activity'nin XML tasarım dosyası ile bağlanmasını sağlayan binding nesnesi
    private lateinit var teamDatabase: SQLiteDatabase // SQLite veritabanı bağlantısı

    override fun onCreate(savedInstanceState: Bundle?) {
        // Activity oluşturulurken yapılan view binding işlemleri
        super.onCreate(savedInstanceState)
        binding = ActivityTeamDetailsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Veritabanını açma veya oluşturma
        teamDatabase = this.openOrCreateDatabase("teamDB", MODE_PRIVATE, null)

        // Başka bir activity'den gelen intent'i alma
        val intent = intent
        val teamName = intent.getStringExtra("teamName") // Gönderilen takım adını alma

        // Veritabanında belirli bir takımın bilgilerini sorgulama
        val cursor = teamDatabase.rawQuery("SELECT * FROM teamDB WHERE name = ?", arrayOf(teamName.toString()))

        // Sorgu sonucundaki sütun indekslerini alma
        val teamNameIndex = cursor.getColumnIndex("name")
        val teamCountryIndex = cursor.getColumnIndex("country")
        val teamLeagueIndex = cursor.getColumnIndex("league")
        val foundedIndex = cursor.getColumnIndex("founded")
        val groundIndex = cursor.getColumnIndex("ground")
        val capacityIndex = cursor.getColumnIndex("capacity")
        val chairmanIndex = cursor.getColumnIndex("chairman")
        val headCoachIndex = cursor.getColumnIndex("headCoach")
        val teamImageIndex = cursor.getColumnIndex("image")

        while (cursor.moveToNext()) {
            // Cursor'dan gelen verileri okuma ve ilgili görsellere/görünüm bileşenlerine aktarma
            binding.teamNameText.setText(cursor.getString(teamNameIndex))
            binding.teamCountryText.setText(cursor.getString(teamCountryIndex))
            binding.teamLeagueText.setText(cursor.getString(teamLeagueIndex))
            binding.teamFoundedText.setText(cursor.getString(foundedIndex))
            binding.teamGroundText.setText(cursor.getString(groundIndex))
            binding.teamCapacityText.setText(cursor.getString(capacityIndex))
            binding.teamChairmanText.setText(cursor.getString(chairmanIndex))
            binding.teamHeadCoachText.setText(cursor.getString(headCoachIndex))

            // Takım logosunu byte dizisinden bitmap'e dönüştürüp ImageView'a atama
            val byteArray1 = cursor.getBlob(teamImageIndex)
            val bitmap = BitmapFactory.decodeByteArray(byteArray1, 0, byteArray1.size)
            binding.teamImageView.setImageBitmap(bitmap)
        }
        cursor.close()
    }
}