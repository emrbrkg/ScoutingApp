package com.emreberkgoger.scoutbook.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.emreberkgoger.scoutbook.databinding.ActivityMainBinding
import com.emreberkgoger.scoutbook.models.Player
import com.emreberkgoger.scoutbook.recyclerViewAdapter.PlayerAdapter

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
}