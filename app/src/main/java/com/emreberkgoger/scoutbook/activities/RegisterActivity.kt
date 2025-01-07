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

class RegisterActivity : AppCompatActivity(), View.OnClickListener,
    OnFocusChangeListener, View.OnKeyListener, TextWatcher {
    private var mBinding: ActivityRegisterBinding? = null
    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(mBinding!!.root)

        mAuth = FirebaseAuth.getInstance()

        mBinding!!.registerButton.setOnClickListener(this)

        mBinding!!.fullNameET.onFocusChangeListener = this
        mBinding!!.emailET.onFocusChangeListener = this
        mBinding!!.passwordTil.onFocusChangeListener = this
        mBinding!!.confirmPasswordTil.onFocusChangeListener = this
        mBinding!!.passwordET.setOnKeyListener(this)
        mBinding!!.confirmPasswordET.setOnKeyListener(this)
        mBinding!!.confirmPasswordET.addTextChangedListener(this)
    }

    private fun validateFullName(): Boolean {
        val fullName = mBinding!!.fullNameET.text.toString()
        if (fullName.isEmpty()) {
            mBinding!!.fullNameTil.error = "Bu alan boş bırakılamaz"
            return false
        }
        mBinding!!.fullNameTil.error = null
        return true
    }

    private fun validateEmail(): Boolean {
        val email = mBinding!!.emailET.text.toString()
        if (email.isEmpty()) {
            mBinding!!.emailTil.error = "Bu alan boş bırakılamaz"
            return false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mBinding!!.emailTil.error = "Geçersiz email adresi"
            return false
        }
        mBinding!!.emailTil.error = null
        return true
    }

    private fun validatePassword(): Boolean {
        val password = mBinding!!.passwordET.text.toString()
        if (password.isEmpty()) {
            mBinding!!.passwordTil.error = "Bu alan boş bırakılamaz"
            return false
        } else if (password.length < 6) {
            mBinding!!.passwordTil.error = "Şifre en az 6 karakter olmalıdır"
            return false
        }
        mBinding!!.passwordTil.error = null
        return true
    }

    private fun validateConfirmPassword(): Boolean {
        val confirmPassword = mBinding!!.confirmPasswordET.text.toString()
        val password = mBinding!!.passwordET.text.toString()
        if (confirmPassword != password) {
            mBinding!!.confirmPasswordTil.error = "Şifreler eşleşmiyor"
            return false
        }
        mBinding!!.confirmPasswordTil.error = null
        return true
    }

    override fun onClick(view: View) {
        if (view.id == R.id.registerButton) {
            if (validate()) {
                registerUser()
            }
        }
    }

    private fun validate(): Boolean {
        var isValid = true
        if (!validateFullName()) isValid = false
        if (!validateEmail()) isValid = false
        if (!validatePassword()) isValid = false
        if (!validateConfirmPassword()) isValid = false
        return isValid
    }

    private fun registerUser() {
        val email = mBinding!!.emailET.text.toString()
        val password = mBinding!!.passwordET.text.toString()

        // Progress bar kaldırıldı.

        mAuth!!.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(
                    this@RegisterActivity,
                    "Kayıt başarılı!",
                    Toast.LENGTH_SHORT
                ).show()
                // MainActivity'yi başlat ve bu aktiviteyi bitir
                val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                // Nullable exception'ı güvenli bir şekilde ele alıyoruz.
                handleRegistrationError(task.exception)
            }
        }
    }

    private fun handleRegistrationError(exception: Exception?) {
        val errorMessage = when (exception) {
            is FirebaseAuthWeakPasswordException -> "Şifre çok zayıf, lütfen daha güçlü bir şifre girin."
            is FirebaseAuthInvalidCredentialsException -> "Geçersiz e-posta adresi, lütfen kontrol edin."
            is FirebaseAuthUserCollisionException -> "Bu e-posta adresi zaten kullanılıyor."
            else -> exception?.message ?: "Bilinmeyen bir hata oluştu."
        }

        AlertDialog.Builder(this)
            .setTitle("Kayıt Hatası")
            .setMessage(errorMessage)
            .setPositiveButton("Tamam") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    override fun onFocusChange(view: View, hasFocus: Boolean) {
        if (!hasFocus) {
            when (view.id) {
                R.id.fullNameET -> validateFullName()
                R.id.emailET -> validateEmail()
                R.id.passwordET -> validatePassword()
                R.id.confirmPasswordET -> validateConfirmPassword()
            }
        }
    }

    override fun onKey(view: View, keyCode: Int, keyEvent: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_ENTER && keyEvent.action == KeyEvent.ACTION_UP) {
            if (validate()) {
                registerUser()
            }
            return true
        }
        return false
    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

    override fun afterTextChanged(s: Editable) {
        validateConfirmPassword()
    }
}
