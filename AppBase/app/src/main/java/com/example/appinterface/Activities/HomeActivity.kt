package com.example.appinterface.Activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.appinterface.R

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val isAdmin = intent.getBooleanExtra("isAdmin", false)

        findViewById<TextView>(R.id.tvBienvenida).text =
            if (isAdmin) "Rol: ADMIN" else "Rol: CAPITÁN"

        findViewById<Button>(R.id.btnIrTorneos).setOnClickListener {
            startActivity(
                Intent(this, TorneosActivity::class.java)
                    .putExtra("isAdmin", isAdmin)
            )
        }

        findViewById<Button>(R.id.btnIrJugadores).setOnClickListener {
            startActivity(
                Intent(this, JugadoresActivity::class.java)
                    .putExtra("isAdmin", isAdmin)
            )
        }

        findViewById<Button>(R.id.btnLogout).setOnClickListener {
            getSharedPreferences("app_prefs", MODE_PRIVATE).edit()
                .remove("user_id")
                .remove("role")
                .apply()

            val intent = Intent(this, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            }
            startActivity(intent)
        }
    }
}