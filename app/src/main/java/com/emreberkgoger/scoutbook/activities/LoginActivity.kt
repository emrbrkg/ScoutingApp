package com.emreberkgoger.scoutbook.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.emreberkgoger.scoutbook.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

// log-in işlemlerinin yapıldığı activity sınıfı
class LoginActivity : AppCompatActivity() {

    private var mBinding: ActivityLoginBinding? = null
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Data binding işlemleri. layout dosyası ile aktivite sınıfını bağlar.
        mBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(mBinding!!.root)

        // Firebase authentication nesnesi oluşturulur.
        mAuth = FirebaseAuth.getInstance()

        // Kullanıcı zaten giriş yaptıysa MainActivity'ye yönlendir
        checkIfUserIsLoggedIn()

        // Giriş butonuna tıklanması durumu
        mBinding!!.loginButton.setOnClickListener {
            val email = mBinding!!.emailET.text.toString().trim()
            val password = mBinding!!.passwordET.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                showToast("Lütfen e-posta adresinizi ve şifrenizi girin.")
            } else {
                performLogin(email, password)
            }
        }

        // Kayıt olma linkine tıklanması durumunda register activitye yönlendirilir.
        mBinding!!.registerText.setOnClickListener {
            navigateToRegisterActivity()
        }
    }

    // Kullanıcı zaten giriş yaptıysa MainActivity'ye yönlendir
    private fun checkIfUserIsLoggedIn() {
        if (mAuth.currentUser != null) {
            navigateToMainActivity()
        }
    }

    // Giriş işlemini gerçekleştiren fonksiyon
    private fun performLogin(email: String, password: String) {
        // Firebase ile giriş işlemi
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                navigateToMainActivity()
            } else {
                showToast("E-posta adresi veya şifre hatalı. Lütfen tekrar deneyin.")
            }
        }
    }

    // MainActivity'ye yönlendiren fonksiyon
    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish() // LoginActivity'yi kapat
    }

    // RegisterActivity'ye yönlendiren fonksiyon
    private fun navigateToRegisterActivity() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }

    // Kullanıcıya anlamlı mesajlar veren Toast fonksiyonu
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    // Uygulama kapandığında kullanıcıyı çıkart
    override fun onDestroy() {
        super.onDestroy()
        signOutUser()
        mBinding = null // Binding bellek sızıntısını önlemek için temizlenir
    }

    // Firebase'den çıkış yapan fonksiyon
    private fun signOutUser() {
        FirebaseAuth.getInstance().signOut()
    }
}
