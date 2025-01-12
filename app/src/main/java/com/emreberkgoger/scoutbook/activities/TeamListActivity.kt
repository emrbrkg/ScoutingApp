package com.emreberkgoger.scoutbook.activities

import android.annotation.SuppressLint
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.emreberkgoger.scoutbook.databinding.ActivityTeamListBinding
import com.emreberkgoger.scoutbook.models.Team
import com.emreberkgoger.scoutbook.recyclerViewAdapter.TeamAdapter

class TeamListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTeamListBinding // Activity'nin XML tasarım dosyası ile bağlanmasını sağlayan binding nesnesi
    private lateinit var teamList: ArrayList<Team> // Takım bilgilerini tutan liste
    private lateinit var teamAdapter: TeamAdapter // RecyclerView için takım verilerini bağlayan adapter
    private lateinit var teamDatabase: SQLiteDatabase // SQLite veritabanı bağlantısı

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Activity'nin tasarım dosyasını bağlama
        binding = ActivityTeamListBinding.inflate(layoutInflater)
        val teamView = binding.root
        setContentView(teamView)

        // Takım listesini ve adapterini başlatma
        teamList = ArrayList<Team>()
        teamAdapter = TeamAdapter(teamList)
        // RecyclerView için düzenleme ve adapter ayarları
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = teamAdapter

        try {
            // Veritabanını açma veya oluşturma
            teamDatabase = this.openOrCreateDatabase("teamDB", MODE_PRIVATE, null)

            // Veritabanından tüm kayıtları sorgulama ve veritabanı sütunlarının indekslerini alma
            val cursor = teamDatabase.rawQuery("SELECT * FROM teamDB", null)
            val nameIndex = cursor.getColumnIndex("name")
            val idIndex = cursor.getColumnIndex("id")
            val countryIndex = cursor.getColumnIndex("country")
            val leagueIndex = cursor.getColumnIndex("league")
            val foundedIndex = cursor.getColumnIndex("founded")
            val groundIndex = cursor.getColumnIndex("ground")
            val capacityIndex = cursor.getColumnIndex("capacity")
            val chairmanIndex = cursor.getColumnIndex("chairman")
            val headCoachIndex = cursor.getColumnIndex("headCoach")
            val imageIndex = cursor.getColumnIndex("image")

            while (cursor.moveToNext()) {
                // Cursor'daki her bir kaydı okuma ve listeye ekleme
                val name = cursor.getString(nameIndex)
                val id = cursor.getInt(idIndex)
                val image = cursor.getBlob(imageIndex)
                val country = cursor.getString(countryIndex)
                val league = cursor.getString(leagueIndex)
                val founded = cursor.getString(foundedIndex)
                val ground = cursor.getString(groundIndex)
                val capacity = cursor.getString(capacityIndex)
                val chairman = cursor.getString(chairmanIndex)
                val headCoach = cursor.getString(headCoachIndex)
                // Yeni bir Team nesnesi oluşturma ve listeye ekleme
                val team = Team(name, id, country, league, founded, ground, capacity, chairman, headCoach, image)
                teamList.add(team)
            }
            // Adaptere veri değişikliği olduğunu bildirerek RecyclerView'ı güncelleme
            teamAdapter.notifyDataSetChanged()
            cursor.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}