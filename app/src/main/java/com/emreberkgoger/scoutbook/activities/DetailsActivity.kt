package com.emreberkgoger.scoutbook.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.emreberkgoger.scoutbook.R
import com.emreberkgoger.scoutbook.databinding.ActivityDetailsBinding
import com.emreberkgoger.scoutbook.enums.PlayerPosition
import com.google.android.material.snackbar.Snackbar
import java.io.ByteArrayOutputStream

class DetailsActivity : AppCompatActivity()
{
    private lateinit var binding : ActivityDetailsBinding
    private  lateinit var permissionLauncher : ActivityResultLauncher<String>
    private lateinit var activityResultLauncher : ActivityResultLauncher<Intent>    // intent ile gidip sonucunda bize veri döndüren result launcher olacak.
    var selectedBitmap : Bitmap? = null
    private lateinit var PlayerDatabase : SQLiteDatabase

    @SuppressLint("Recycle")
    override fun onCreate(savedInstanceState: Bundle?) {
        // View binding işlemleri
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Database'in oluşturulması ve intentin çağırımı
        PlayerDatabase = this.openOrCreateDatabase("Players", MODE_PRIVATE, null)
        val intent = intent     //get intent
        val info = intent.getStringExtra("info")
        registerLauncher()

        val positionList = PlayerPosition.values().map { it.positionName }

        // Spinner için adapter tanımlama işlemi
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, positionList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.positionSpinner.adapter = adapter

        if (info == "new") {
            // Yeni oyuncu ekleme durumunda Spinner varsayılan olarak ilk elemanı seçer
            binding.nameText.setText("")
            binding.countryText.setText("")
            binding.ageText.setText("")
            binding.valueText.setText("")
            binding.teamText.setText("")
            binding.updateButton.visibility = View.INVISIBLE
            binding.deleteButton.visibility = View.INVISIBLE

            val selectedImageBackground = BitmapFactory.decodeResource(
                applicationContext.resources, R.drawable.select
            )
            binding.imageView.setImageBitmap(selectedImageBackground)

        } else {
            binding.saveButton.visibility = View.INVISIBLE
            val selectedId = intent.getIntExtra("id", 1)
            val cursor = PlayerDatabase.rawQuery("SELECT * FROM players WHERE id = ?", arrayOf(selectedId.toString()))
            val nameIndex = cursor.getColumnIndex("name")
            val countryIndex = cursor.getColumnIndex("country")
            val positionIndex = cursor.getColumnIndex("position")
            val ageIndex = cursor.getColumnIndex("age")
            val valueIndex = cursor.getColumnIndex("value")
            val teamIndex = cursor.getColumnIndex("team")
            val imageIndex = cursor.getColumnIndex("image")

            while (cursor.moveToNext()) {
                binding.nameText.setText(cursor.getString(nameIndex))
                binding.countryText.setText(cursor.getString(countryIndex))
                binding.ageText.setText(cursor.getString(ageIndex))
                binding.valueText.setText(cursor.getString(valueIndex))
                binding.teamText.setText(cursor.getString(teamIndex))

                // Veritabanından gelen pozisyonu enum ile eşleştir
                val position = cursor.getString(positionIndex)
                val positionIndexInEnum = PlayerPosition.values().indexOfFirst { it.positionName == position }
                if (positionIndexInEnum >= 0) {
                    binding.positionSpinner.setSelection(positionIndexInEnum)
                }
                val byteArray = cursor.getBlob(imageIndex)
                val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                binding.imageView.setImageBitmap(bitmap)
            }
            cursor.close()
        }

        // Spinner'dan seçilen pozisyonu saklamak için
        binding.positionSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedPosition = PlayerPosition.values()[position]
                // Seçilen pozisyonu kaydetmek için bir değişken
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Hiçbir şey seçilmezse yapılacak işlemler (genellikle boş bırakılır)
            }
        }
    }

    // save butonu onClick metodu
    fun saveButton(view : View) {
        val name = binding.nameText.text.toString()
        val country = binding.countryText.text.toString()
        val selectedPosition = binding.positionSpinner.selectedItem.toString()
        val age = binding.ageText.text.toString()
        val value = binding.valueText.text.toString()
        val team = binding.teamText.text.toString()

        val positionEnum = PlayerPosition.fromValue(selectedPosition)
        if (positionEnum == null) {
            Toast.makeText(this, "Invalid position selection!", Toast.LENGTH_SHORT).show()
            return
        }
        val position = positionEnum.positionName

       if (selectedBitmap != null) {
           // görseli ayarlamak için yapılan işlemler.
            val smallBitmap = makeSmallerBitMap(selectedBitmap!!, 300)
            val outputStream = ByteArrayOutputStream()
            smallBitmap.compress(Bitmap.CompressFormat.PNG, 50, outputStream)
            val byteArray = outputStream.toByteArray()

            try {
                val PlayerDatabase = this.openOrCreateDatabase("Players", MODE_PRIVATE, null)
                PlayerDatabase.execSQL("CREATE TABLE IF NOT EXISTS players (id INTEGER PRIMARY KEY, name VARCHAR, country VARCHAR, position VARCHAR, age VARCHAR, value VARCHAR, team VARCHAR, image BLOB)")

                val sqlString = "INSERT INTO players (name, country, position, age, value, team, image) VALUES (?, ?, ?, ?, ?, ?, ?)"
                val statement = PlayerDatabase.compileStatement(sqlString)

                statement.bindString(1, name)
                statement.bindString(2, country)
                statement.bindString(3, position)
                statement.bindString(4, age)
                statement.bindString(5, value)
                statement.bindString(6, team)
                statement.bindBlob(7, byteArray)
                statement.execute()

            } catch (e : Exception) {
                e.printStackTrace()
            }
            val intent = Intent(this@DetailsActivity, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
    }

    // update işlemi onClick metodu
    fun updateButton(view: View) {
        val selectedId = intent.getIntExtra("id", 1)

        val name = binding.nameText.text.toString()
        val country = binding.countryText.text.toString()
        val selectedPosition = binding.positionSpinner.selectedItem.toString()
        val age = binding.ageText.text.toString()
        val value = binding.valueText.text.toString()
        val team = binding.teamText.text.toString()

        val positionEnum = PlayerPosition.fromValue(selectedPosition)
        val position = positionEnum?.positionName
        try {
            val PlayerDatabase = this.openOrCreateDatabase("Players", MODE_PRIVATE, null)

            val sqlStringForName = "UPDATE players SET name = ? WHERE id = ?"
            val statementForName = PlayerDatabase.compileStatement(sqlStringForName)
            statementForName.bindString(1, name) // İlk parametre 'name' değeri
            statementForName.bindLong(2, selectedId.toLong()) // İkinci parametre 'id' değeri
            statementForName.execute()

            val sqlStringForTeam = "UPDATE players SET team = ? WHERE id = ?"
            val statementForTeam = PlayerDatabase.compileStatement(sqlStringForTeam)
            statementForTeam.bindString(1, team) // İlk parametre 'team' değeri
            statementForTeam.bindLong(2, selectedId.toLong()) // İkinci parametre 'id' değeri
            statementForTeam.execute()

            val sqlStringForCountry = "UPDATE players SET country = ? WHERE id = ?"
            val statementForCountry = PlayerDatabase.compileStatement(sqlStringForCountry)
            statementForCountry.bindString(1, country) // İlk parametre 'country' değeri
            statementForCountry.bindLong(2, selectedId.toLong()) // İkinci parametre 'id' değeri
            statementForCountry.execute()

            val sqlStringForPosition = "UPDATE players SET position = ? WHERE id = ?"
            val statementForPosition = PlayerDatabase.compileStatement(sqlStringForPosition)
            statementForPosition.bindString(1, position) // İlk parametre 'position' değeri
            statementForPosition.bindLong(2, selectedId.toLong()) // İkinci parametre 'id' değeri
            statementForPosition.execute()

            val sqlStringForBirthDate = "UPDATE players SET age = ? WHERE id = ?"
            val statementForBirthDate = PlayerDatabase.compileStatement(sqlStringForBirthDate)
            statementForBirthDate.bindString(1, age) // İlk parametre 'birthDate' değeri
            statementForBirthDate.bindLong(2, selectedId.toLong()) // İkinci parametre 'id' değeri
            statementForBirthDate.execute()

            val sqlStringForMarketValue = "UPDATE players SET value = ? WHERE id = ?"
            val statementForMarketValue = PlayerDatabase.compileStatement(sqlStringForMarketValue)
            statementForMarketValue.bindString(1, value) // İlk parametre 'MarketValue' değeri
            statementForMarketValue.bindLong(2, selectedId.toLong()) // İkinci parametre 'id' değeri
            statementForMarketValue.execute()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val intent = Intent(this@DetailsActivity, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
   }

    // Delete onClick metodu
    fun deleteButton(view: View) {
        showDeleteConfirmationDialog(view)
    }

    // Oyuncu silme business kodu
    fun deletePlayer(view: View) {
        val selectedId = intent.getIntExtra("id", 1)
        try {
            val PlayerDatabase = this.openOrCreateDatabase("Players", MODE_PRIVATE, null)
            val sqlStringForDelete = "DELETE FROM players WHERE id = $selectedId"
            val statementForDelete = PlayerDatabase.compileStatement(sqlStringForDelete)
            statementForDelete.execute()
            Toast.makeText(this, "Player deleted successfully", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Deletion failed!", Toast.LENGTH_LONG).show()
        }

        val intent = Intent(this@DetailsActivity, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    // delete işleminde kullanıcıya emin misiniz diye soran AlertDialogun oluşturulduğu metot.
    fun showDeleteConfirmationDialog(view: View) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete Confirmation")
        builder.setMessage("Are you sure of deleting this player permanently ?")

        builder.setPositiveButton("Yes") { dialog, _->
            deletePlayer(view)
            dialog.dismiss()
        }

        builder.setNegativeButton("No") { dialog, _->
            dialog.dismiss()
        }

        val alertDialog = builder.create()
        alertDialog.show()
    }

    // Oyuncu detayına gitme butonunun onClick metodu
    fun goToPlayerDetails(view: View) {
        val intentToPlayerDetail = Intent(this@DetailsActivity, PlayerDetailsActivity::class.java)
        val playerId = intent.getIntExtra("playerId", -1) // Bu id ilgili oyuncuya ait olmalı.
        intentToPlayerDetail.putExtra("playerId", playerId)
        startActivity(intentToPlayerDetail)
    }

    // Çektiğimiz görsellerin mümkün olduğunca az yer kaplaması lazım. Görselleri küçültmek için bu fonksiyonu kullanacağız:
    // Bitmapi küçültmek için genel geçer kullanılabilecek bir fonksiyondur.
    fun makeSmallerBitMap(image : Bitmap, maximumSize : Int) : Bitmap {
        var width = image.width
        var height = image.height

        val bitmapRatio : Double = width.toDouble() / height.toDouble()

        // Görselleri aynı oranda küçültmek için kurduğumuz algoritma
        if (bitmapRatio > 1) {
            //landscape (yatay)
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

    fun selectImage(view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_MEDIA_IMAGES)) {
                    Snackbar.make(view, "Permission needed for gallery", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Give permission", View.OnClickListener {
                            permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                        }).show()

                } else {
                    permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                }
            } else {
                val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    Snackbar.make(view, "Permission needed for gallery", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Give permission", View.OnClickListener {
                            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                        }).show()

                } else {
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            } else {
                // Kullanıcıya seçim yapmak için iki seçenek sunalım: Galeri veya drawable kaynakları
                val options = arrayOf("Gallery", "Drawable Resources")
                AlertDialog.Builder(this)
                    .setTitle("Select an option")
                    .setItems(options) { _, which ->
                        when (which) {
                            0 -> { // Galeri seçimi
                                val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                                activityResultLauncher.launch(intentToGallery)
                            }
                            1 -> { // Drawable kaynakları seçimi
                                showDrawableResourcePicker()
                            }
                        }
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
        }
    }

    // Drawable kaynakları listelemek için bir metod
    private fun showDrawableResourcePicker() {
        val drawableResources = listOf(
            R.drawable.emre_berk_goger,
            R.drawable.select,
        )
        val resourceNames = arrayOf("Emre Berk Goger", "Select")

        AlertDialog.Builder(this)
            .setTitle("Select a Drawable Resource")
            .setItems(resourceNames) { _, which ->
                val selectedResource = drawableResources[which]
                // Seçilen resmi bir ImageView'da gösterelim
                findViewById<ImageView>(R.id.imageView).setImageResource(selectedResource)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun registerLauncher() {
        // Galeriye gitmek ve galeriden fotoğraf seçme işlemleri
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val intentFromResult = result.data
                if (intentFromResult != null)
                {
                    val imageData = intentFromResult.data
                    //binding.imageView.setImageURI(imageData)
                    // Kendi bitmapimizi oluşturup oradan veriyi küçültüp SQLite'a kaydedeceğiz (?)
                    // URI'ı bitmape çevireceğiz.
                    // Birçok farklı hataya açık bir işlem olduğundan try catch içinde yazacağız.
                    if (imageData != null) {
                        try {
                            // Bu kontrol, yapmadığımızda SDK hatası veriyodu. Yani eski cihazlarda çalışmayacağına dair
                            if (Build.VERSION.SDK_INT >= 28) {
                                // Bitmap'e çevirme işlemleri:
                                val source = ImageDecoder.createSource(contentResolver, imageData)
                                selectedBitmap = ImageDecoder.decodeBitmap(source)
                                binding.imageView.setImageBitmap(selectedBitmap)
                            } else {
                                selectedBitmap = MediaStore.Images.Media.getBitmap(this@DetailsActivity.contentResolver, imageData)
                                binding.imageView.setImageBitmap(selectedBitmap)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }

        // Galeriye gitmek için kullanılan izin işlemleri
        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { success ->
            if (success) {
                val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            } else {
                Toast.makeText(this@DetailsActivity, "Permission needed!", Toast.LENGTH_LONG).show()
            }
        }
    }
}