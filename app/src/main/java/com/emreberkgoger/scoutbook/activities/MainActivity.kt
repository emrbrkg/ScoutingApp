package com.emreberkgoger.scoutbook.activities

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.emreberkgoger.scoutbook.R
import com.emreberkgoger.scoutbook.databinding.ActivityMainBinding
import com.emreberkgoger.scoutbook.models.Player
import com.emreberkgoger.scoutbook.models.Team
import com.emreberkgoger.scoutbook.recyclerViewAdapter.PlayerAdapter
import java.io.ByteArrayOutputStream

// MainActivity sınıfı, uygulamanın ana ekranını temsil eder.
// RecyclerView ile oyuncu listesini gösterir ve yeni oyuncu eklemek için bir buton barındırır.
class MainActivity : AppCompatActivity() {
    // View binding için tanımlanan değişken
    private lateinit var binding: ActivityMainBinding

    // Oyuncu listesini tutmak için bir ArrayList
    private lateinit var playerList: ArrayList<Player>

    // RecyclerView'da kullanılacak olan adapter
    private lateinit var playerAdapter: PlayerAdapter

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        // Activity oluşturulurken yapılan işlemler
        super.onCreate(savedInstanceState)

        // View binding işlemi
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Oyuncu listesinin oluşturulması ve RecyclerView'a bağlanması
        playerList = ArrayList<Player>()
        playerAdapter = PlayerAdapter(playerList)

        // RecyclerView için bir layout manager ayarlanıyor (dikey listeleme)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        // RecyclerView'a adapter atanıyor
        binding.recyclerView.adapter = playerAdapter

        setupTeamDatabase() // yeni bir SQLite veritabanı aç veya oluşturacak metodu çağır.

        try {
            // Oyuncuların SQLite veritabanından alınarak RecyclerView'da gösterilmesi
            val PlayerDatabase = this.openOrCreateDatabase("Players", MODE_PRIVATE, null)

            // Veritabanından tüm oyuncuları almak için SQL sorgusu
            val cursor = PlayerDatabase.rawQuery("SELECT * FROM players", null)

            // Veritabanı sütunlarının index değerlerini alıyoruz
            val nameIndex = cursor.getColumnIndex("name") // Oyuncu adı sütunu
            val idIndex = cursor.getColumnIndex("id") // Oyuncu ID'si sütunu
            val imageIndex = cursor.getColumnIndex("image") // Oyuncu resmi sütunu

            // Veritabanından tüm kayıtları okuyarak listeye ekliyoruz
            while (cursor.moveToNext()) {
                // Sırasıyla oyuncu adı, ID ve resmi alınıyor
                val name = cursor.getString(nameIndex)
                val id = cursor.getInt(idIndex)
                val image = cursor.getBlob(imageIndex)

                // Player modeline uygun bir nesne oluşturuluyor
                val player = Player(name, id, image)

                // Listeye ekleme yapılıyor
                playerList.add(player)
            }

            // Adapter'e yeni verilerin eklenmesi gerektiğini bildiriyoruz
            playerAdapter.notifyDataSetChanged()

            // Cursor kapatılıyor (veritabanı bağlantısı temizleniyor)
            cursor.close()
        } catch (e: Exception) {
            // Hata durumunda hatayı konsola yazdırıyoruz
            e.printStackTrace()
        }
    }

    // 'Ekle' butonuna tıklandığında çalışan metot
    // Kullanıcıyı DetailsActivity'e yönlendirir ve "new" bilgisi ekler.
    fun add(view: View) {
        val intent = Intent(this@MainActivity, DetailsActivity::class.java)
        intent.putExtra("info", "new") // Yeni oyuncu ekleme modunu belirtir
        startActivity(intent) // DetailsActivity'i başlatır
    }

    fun viewTeamList(view: View) {
        // TeamListActivity ekranını başlatmak için bir Intent oluştur ve yeni ekranı başlat.
        val intent = Intent(this@MainActivity, TeamListActivity::class.java)
        startActivity(intent)
    }

    private fun setupTeamDatabase() {
        // yeni bir SQLite veritabanı aç veya oluştur.
        val teamDatabase = this.openOrCreateDatabase("teamDB", MODE_PRIVATE, null)

        // Takımlar için bir tablo oluştur (eğer yoksa).
        teamDatabase.execSQL("CREATE TABLE IF NOT EXISTS teamDB (id INTEGER PRIMARY KEY, name VARCHAR, country VARCHAR, league VARCHAR, founded VARCHAR, ground VARCHAR, capacity VARCHAR, chairman VARCHAR, headCoach VARCHAR, image BLOB)")

        // Tablo içindeki kayıt sayısını kontrol etmek için sorgu çalıştır.
        val cursor = teamDatabase.rawQuery("SELECT COUNT(*) FROM teamDB", null)
        cursor.moveToFirst()
        val count = cursor.getInt(0)
        cursor.close()

        // Eğer tablo boşsa örnek takımları ekle.
        if (count == 0) {
            val teams = generateSampleTeams() // Örnek takımları oluştur.
            for (teamDB in teams) {
                // Yeni takım verilerini eklemek için ContentValues kullan.
                val values = ContentValues()
                values.put("name", teamDB.name)
                values.put("country", teamDB.country)
                values.put("league", teamDB.league)
                values.put("founded", teamDB.founded)
                values.put("ground", teamDB.ground)
                values.put("capacity", teamDB.capacity)
                values.put("chairman", teamDB.chairman)
                values.put("headCoach", teamDB.headCoach)
                values.put("image", teamDB.image)
                // Takımı tabloya ekle.
                teamDatabase.insert("teamDB", null, values)
            }
        }
    }

    private fun generateSampleTeams(): ArrayList<Team> {
        val teams = ArrayList<Team>() // Örnek takımları tutacak bir liste oluştur.
        // Her bir takım için bilgileri belirle ve listeye ekle
        // Takımın görselini byte array olarak al.
        val name1 = "Fenerbahce S.K."
        val image1: ByteArray? = getImageAsByteArray(this, R.drawable.fb)
        teams.add(Team(name1, 0, "Turkey", "Süper Lig", "3 May 1907", "Şükrü Saracoğlu Stadyumu", "47.834", "Ali Koç", "José Mourinho", image1))

        val name2 = "Galatasaray S.K."
        val image2: ByteArray? = getImageAsByteArray(this, R.drawable.gs)
        teams.add(Team(name2, 1, "Turkey", "Süper Lig", "19 October 1905", "Türk Telekom Stadyumu", "52.650", "Dursun Özbek", "Okan Buruk", image2))

        val name3 = "Besiktas J.K."
        val image3: ByteArray? = getImageAsByteArray(this, R.drawable.besiktas)
        teams.add(Team(name3, 2, "Turkey", "Süper Lig", "19 March 1903", "Vodafone Park", "41.188", "Serdar Adalı", "Serdar Topraktepe", image3))

        val name4 = "Real Madrid C.F."
        val image4: ByteArray? = getImageAsByteArray(this, R.drawable.realmadrid)
        teams.add(Team(name4, 3, "Spain", "La Liga", "6 March 1902", "Santiago Bernabéu Stadyumu", "81.044", "Florentino Pérez", "Carlo Ancelotti", image4))

        val name5 = "FC Barcelona"
        val image5: ByteArray? = getImageAsByteArray(this, R.drawable.fcb)
        teams.add(Team(name5, 4, "Spain", "La Liga", "29 November 1899", "Camp Nou", "99.354", "Joan Laporta", "\tHansi Flick", image5))

        val name6 = "Chelsea F.C."
        val image6: ByteArray? = getImageAsByteArray(this, R.drawable.chelsea)
        teams.add(Team(name6, 5, "England", "Premier League", "10 March 1905", "Stamford Bridge", "40.341", "Todd Boehly", "Enzo Maresca", image6))

        val name7 = "Liverpool F.C."
        val image7: ByteArray? = getImageAsByteArray(this, R.drawable.liverpool)
        teams.add(Team(name7, 6, "England", "Premier League", "3 June 1892", "Anfield", "53.394", "John W. Henry", "Arne Slot", image7))

        val name8 = "Bursaspor"
        val image8: ByteArray? = getImageAsByteArray(this, R.drawable.bursaspor)
        teams.add(Team(name8, 7, "Turkey", "TFF 1. Lig", "1 June 1963", "Timsah Arena", "43.000", "Sinan Bür", "Pablo Batalla", image8))

        val name9 = "Borussia Dortmund"
        val image9: ByteArray? = getImageAsByteArray(this, R.drawable.dortmund)
        teams.add(Team(name9, 8, "Germany", "Bundesliga", "19 December 1909", "Signal Iduna Park", "81.365", "Hans-Joachim Watzke", "Nuri Şahin", image9))

        val name10 = "Como 1907"
        val image10: ByteArray? = getImageAsByteArray(this, R.drawable.como)
        teams.add(Team(name10, 9, "Italy", "Serie A", "10 March 1907", "Stadio Giuseppe Sinigaglia", "13.602", "Roberto Felleca", "Cesc Fàbregas", image10))

        return teams
    }

    fun getImageAsByteArray(context: Context, drawableId: Int): ByteArray? {
        // Drawable dosyasını Bitmap olarak al ve Bitmap'i ByteArray'e dönüştür.
        val drawable = context.resources.getDrawable(drawableId, null) as BitmapDrawable
        val bitmap = drawable.bitmap
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        return outputStream.toByteArray()
    }

}