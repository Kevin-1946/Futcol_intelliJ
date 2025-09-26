package com.example.appinterface.Activities

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.appinterface.Api.RetrofitInstance
import com.example.appinterface.Models.Login
import com.example.appinterface.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private val ioScope = CoroutineScope(Dispatchers.IO)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Ajuste de insets (si tu layout raíz tiene id @+id/main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val sysBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(sysBars.left, sysBars.top, sysBars.right, sysBars.bottom)
            insets
        }

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPass  = findViewById<EditText>(R.id.etPass)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val tvResult = findViewById<TextView>(R.id.tvResult)

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val pass  = etPass.text.toString()

            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Completa email y password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            tvResult.text = "Autenticando…"
            btnLogin.isEnabled = false

            ioScope.launch {
                try {
                    // Usamos el modelo unificado Login
                    val res = RetrofitInstance.api2kotlin.login(Login(email = email, password = pass))
                    withContext(Dispatchers.Main) {
                        btnLogin.isEnabled = true
                        if (res.isSuccessful && res.body() != null) {
                            val body = res.body()!!

                            // Guarda sesión simple
                            getSharedPreferences("app_prefs", MODE_PRIVATE).edit()
                                .putInt("user_id", body.userId ?: -1)
                                .putString("role", body.role)   // "ADMIN" o "CAPITAN"
                                .apply()

                            // Navegar a HOME (ahí eliges Torneos o Jugadores)
                            val isAdmin = body.role == "ADMIN"
                            startActivity(
                                Intent(this@MainActivity, HomeActivity::class.java)
                                    .putExtra("isAdmin", isAdmin)
                            )
                            finish()
                        } else if (res.code() == 401) {
                            tvResult.text = "Credenciales incorrectas"
                        } else {
                            tvResult.text = "Error ${res.code()}"
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        btnLogin.isEnabled = true
                        tvResult.text = "Error de red: ${e.localizedMessage}"
                    }
                }
            }
        }
    }
}