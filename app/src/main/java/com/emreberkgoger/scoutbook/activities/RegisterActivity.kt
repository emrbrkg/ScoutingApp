package com.emreberkgoger.scoutbook.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.KeyEvent
import android.view.View
import android.view.View.OnFocusChangeListener
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.emreberkgoger.scoutbook.R
import com.emreberkgoger.scoutbook.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException

// Kullanıcının kayıt olmasını sağlayan RegisterActivity sınıfı
class RegisterActivity : AppCompatActivity(), View.OnClickListener,
    OnFocusChangeListener, View.OnKeyListener, TextWatcher {

    // View binding nesnesi (XML elemanlarına kolay erişim için kullanılıyor)
    private var mBinding: ActivityRegisterBinding? = null

    // Firebase Authentication nesnesi (kullanıcı işlemleri için kullanılıyor)
    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // View binding işlemi yapılıyor
        mBinding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(mBinding!!.root)

        // Firebase Authentication nesnesi başlatılıyor
        mAuth = FirebaseAuth.getInstance()

        // Buton ve diğer bileşenlere tıklama/odaklanma olayları atanıyor
        mBinding!!.registerButton.setOnClickListener(this)
        mBinding!!.fullNameET.onFocusChangeListener = this
        mBinding!!.emailET.onFocusChangeListener = this
        mBinding!!.passwordTil.onFocusChangeListener = this
        mBinding!!.confirmPasswordTil.onFocusChangeListener = this
        mBinding!!.passwordET.setOnKeyListener(this)
        mBinding!!.confirmPasswordET.setOnKeyListener(this)
        mBinding!!.confirmPasswordET.addTextChangedListener(this)
    }

    // Kullanıcının tam adını doğrulayan fonksiyon
    private fun validateFullName(): Boolean {
        val fullName = mBinding!!.fullNameET.text.toString()
        if (fullName.isEmpty()) {
            // Eğer alan boşsa, hata mesajı gösteriliyor
            mBinding!!.fullNameTil.error = "Bu alan boş bırakılamaz"
            return false
        }
        // Hata mesajı temizleniyor (geçerli bir giriş yapılmışsa)
        mBinding!!.fullNameTil.error = null
        return true
    }

    // Kullanıcının e-posta adresini doğrulayan fonksiyon
    private fun validateEmail(): Boolean {
        val email = mBinding!!.emailET.text.toString()
        if (email.isEmpty()) {
            mBinding!!.emailTil.error = "Bu alan boş bırakılamaz"
            return false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            // E-posta adresinin geçerli bir formata sahip olup olmadığını kontrol ediyor
            mBinding!!.emailTil.error = "Geçersiz email adresi"
            return false
        }
        mBinding!!.emailTil.error = null
        return true
    }

    // Kullanıcının şifresini doğrulayan fonksiyon
    private fun validatePassword(): Boolean {
        val password = mBinding!!.passwordET.text.toString()
        if (password.isEmpty()) {
            mBinding!!.passwordTil.error = "Bu alan boş bırakılamaz"
            return false
        } else if (password.length < 6) {
            // Şifre uzunluğunu kontrol ediyor (en az 6 karakter olmalı)
            mBinding!!.passwordTil.error = "Şifre en az 6 karakter olmalıdır"
            return false
        }
        mBinding!!.passwordTil.error = null
        return true
    }

    // Şifre tekrarı doğrulama işlemi yapan fonksiyon
    private fun validateConfirmPassword(): Boolean {
        val confirmPassword = mBinding!!.confirmPasswordET.text.toString()
        val password = mBinding!!.passwordET.text.toString()
        if (confirmPassword != password) {
            // Şifreler eşleşmiyorsa hata mesajı gösteriliyor
            mBinding!!.confirmPasswordTil.error = "Şifreler eşleşmiyor"
            return false
        }
        mBinding!!.confirmPasswordTil.error = null
        return true
    }

    // "Kayıt Ol" butonuna tıklama işlemi
    override fun onClick(view: View) {
        if (view.id == R.id.registerButton) {
            if (validate()) {
                // Tüm doğrulamalar başarılıysa kullanıcı kaydediliyor
                registerUser()
            }
        }
    }

    // Tüm doğrulama işlemlerini kontrol eden fonksiyon
    private fun validate(): Boolean {
        var isValid = true
        if (!validateFullName()) isValid = false
        if (!validateEmail()) isValid = false
        if (!validatePassword()) isValid = false
        if (!validateConfirmPassword()) isValid = false
        return isValid
    }

    // Kullanıcı kaydı yapan fonksiyon (Firebase Authentication kullanılıyor)
    private fun registerUser() {
        val email = mBinding!!.emailET.text.toString()
        val password = mBinding!!.passwordET.text.toString()

        // Firebase'e kullanıcı kaydı için istek gönderiliyor
        mAuth!!.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Kayıt başarılı ise mesaj göster ve MainActivity'ye yönlendir
                Toast.makeText(
                    this@RegisterActivity,
                    "Kayıt başarılı!",
                    Toast.LENGTH_SHORT
                ).show()
                val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                startActivity(intent)
                finish() // Bu aktiviteyi sonlandır
            } else {
                // Kayıt sırasında hata oluşursa hatayı işle
                handleRegistrationError(task.exception)
            }
        }
    }

    // Kayıt sırasında oluşan hataları ele alan fonksiyon
    private fun handleRegistrationError(exception: Exception?) {
        val errorMessage = when (exception) {
            is FirebaseAuthWeakPasswordException -> "Şifre çok zayıf, lütfen daha güçlü bir şifre girin."
            is FirebaseAuthInvalidCredentialsException -> "Geçersiz e-posta adresi, lütfen kontrol edin."
            is FirebaseAuthUserCollisionException -> "Bu e-posta adresi zaten kullanılıyor."
            else -> exception?.message ?: "Bilinmeyen bir hata oluştu."
        }

        // Kullanıcıya hata mesajını gösteren bir uyarı penceresi oluştur
        AlertDialog.Builder(this)
            .setTitle("Kayıt Hatası")
            .setMessage(errorMessage)
            .setPositiveButton("Tamam") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    // Odaklanma değiştiğinde doğrulamayı tetikleyen fonksiyon
    override fun onFocusChange(view: View, hasFocus: Boolean) {
        if (!hasFocus) { // Odak kaybı olduğunda doğrulama yapılır
            when (view.id) {
                R.id.fullNameET -> validateFullName()
                R.id.emailET -> validateEmail()
                R.id.passwordET -> validatePassword()
                R.id.confirmPasswordET -> validateConfirmPassword()
            }
        }
    }

    // Kullanıcı "Enter" tuşuna bastığında kaydı başlatır
    override fun onKey(view: View, keyCode: Int, keyEvent: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_ENTER && keyEvent.action == KeyEvent.ACTION_UP) {
            if (validate()) {
                registerUser()
            }
            return true
        }
        return false
    }

    // Kullanıcının şifre tekrarını gerçek zamanlı kontrol eden TextWatcher metodları
    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

    override fun afterTextChanged(s: Editable) {
        validateConfirmPassword() // Kullanıcı şifre tekrarını değiştirdiğinde doğrulama yapılır
    }
}