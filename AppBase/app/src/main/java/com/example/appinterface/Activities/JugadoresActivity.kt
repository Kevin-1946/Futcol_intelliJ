package com.example.appinterface.Activities

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appinterface.Api.RetrofitInstance
import com.example.appinterface.Adapter.JugadorAdapter
import com.example.appinterface.Models.Jugador
import com.example.appinterface.R
import kotlinx.coroutines.*

class JugadoresActivity : AppCompatActivity() {

    private val io = CoroutineScope(Dispatchers.IO)

    private lateinit var tvRole: TextView
    private lateinit var adminPanel: View
    private lateinit var adapter: JugadorAdapter

    // Inputs admin
    private lateinit var etNombre: EditText
    private lateinit var etDocumento: EditText
    private lateinit var etNacimiento: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etGenero: EditText
    private lateinit var etEdad: EditText
    private lateinit var etUserId: EditText
    private lateinit var etEquipoId: EditText
    private lateinit var etIdActualizar: EditText
    private lateinit var etIdEliminar: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_jugadores)

        val isAdmin = intent.getBooleanExtra("isAdmin", false)

        tvRole = findViewById(R.id.tvRole)
        adminPanel = findViewById(R.id.adminPanel)
        tvRole.text = if (isAdmin) "Rol: ADMIN" else "Rol: CAPITAN"
        adminPanel.visibility = if (isAdmin) View.VISIBLE else View.GONE

        // Recycler
        val rv = findViewById<RecyclerView>(R.id.rvJugadores)
        rv.layoutManager = LinearLayoutManager(this)
        adapter = JugadorAdapter(mutableListOf()) { seleccionado ->
            if (isAdmin) {
                etIdActualizar.setText(seleccionado.id.toString())
                etNombre.setText(seleccionado.nombre)
                etDocumento.setText(seleccionado.n_documento)
                etNacimiento.setText(seleccionado.fecha_nacimiento)
                etEmail.setText(seleccionado.email)
                etPassword.setText(seleccionado.password)
                etGenero.setText(seleccionado.genero)
                etEdad.setText(seleccionado.edad.toString())
                etUserId.setText(seleccionado.user_id.toString())
                etEquipoId.setText(seleccionado.equipo_id.toString())
            }
        }
        rv.adapter = adapter

        // Botón listar
        findViewById<Button>(R.id.btnRefrescar).setOnClickListener { listar() }

        if (isAdmin) {
            // Referencias panel admin
            etNombre     = findViewById(R.id.etNombre)
            etDocumento  = findViewById(R.id.etDocumento)
            etNacimiento = findViewById(R.id.etNacimiento)
            etEmail      = findViewById(R.id.etEmail)
            etPassword   = findViewById(R.id.etPassword)
            etGenero     = findViewById(R.id.etGenero)
            etEdad       = findViewById(R.id.etEdad)
            etUserId     = findViewById(R.id.etUserId)
            etEquipoId   = findViewById(R.id.etEquipoId)
            etIdActualizar = findViewById(R.id.etIdActualizar)
            etIdEliminar   = findViewById(R.id.etIdEliminar)

            // Crear
            findViewById<Button>(R.id.btnCrear).setOnClickListener {
                val edad = etEdad.text.toString().toIntOrNull()
                val userId = etUserId.text.toString().toIntOrNull()
                val equipoId = etEquipoId.text.toString().toIntOrNull()
                if (etNombre.text.isBlank() || edad == null || userId == null || equipoId == null) {
                    toast("Completa nombre, edad, user_id y equipo_id válidos")
                    return@setOnClickListener
                }
                val body = buildJugador(edad, userId, equipoId, id = 0) // id=0 para crear
                crear(body) {
                    limpiarInputs()
                    listar()
                }
            }

            // Actualizar
            findViewById<Button>(R.id.btnActualizar).setOnClickListener {
                val id = etIdActualizar.text.toString().toIntOrNull()
                val edad = etEdad.text.toString().toIntOrNull()
                val userId = etUserId.text.toString().toIntOrNull()
                val equipoId = etEquipoId.text.toString().toIntOrNull()
                if (id == null) { toast("ID inválido"); return@setOnClickListener }
                if (etNombre.text.isBlank() || edad == null || userId == null || equipoId == null) {
                    toast("Completa nombre, edad, user_id y equipo_id válidos")
                    return@setOnClickListener
                }
                val body = buildJugador(edad, userId, equipoId, id = id)
                actualizar(id, body) {
                    listar()
                    toast("Actualizado")
                }
            }

            // Eliminar
            findViewById<Button>(R.id.btnEliminar).setOnClickListener {
                val id = etIdEliminar.text.toString().toIntOrNull()
                if (id == null) { toast("ID inválido"); return@setOnClickListener }
                eliminar(id) { listar() }
            }
        }

        // listar de entrada
        listar()
    }

    private fun buildJugador(edad: Int, userId: Int, equipoId: Int, id: Int) = Jugador(
        id = id,
        nombre = etNombre.text.toString().trim(),
        n_documento = etDocumento.text.toString().trim(),
        fecha_nacimiento = etNacimiento.text.toString().trim(), // "YYYY-MM-DD"
        email = etEmail.text.toString().trim(),
        password = etPassword.text.toString(),
        genero = etGenero.text.toString().trim(),
        edad = edad,
        user_id = userId,
        equipo_id = equipoId
    )

    private fun listar() {
        io.launch {
            try {
                val res = RetrofitInstance.api2kotlin.getJugadores()
                withContext(Dispatchers.Main) {
                    if (res.isSuccessful && res.body() != null) {
                        adapter.setItems(res.body()!!)
                    } else toast("Error ${res.code()}")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { toast("Red: ${e.localizedMessage}") }
            }
        }
    }

    private fun crear(body: Jugador, onOk: () -> Unit) {
        io.launch {
            try {
                val res = RetrofitInstance.api2kotlin.crearJugador(body)
                withContext(Dispatchers.Main) {
                    if (res.isSuccessful) onOk() else toast("Error ${res.code()}")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { toast("Red: ${e.localizedMessage}") }
            }
        }
    }

    private fun actualizar(id: Int, body: Jugador, onOk: () -> Unit) {
        io.launch {
            try {
                val res = RetrofitInstance.api2kotlin.actualizarJugador(id, body)
                withContext(Dispatchers.Main) {
                    if (res.isSuccessful) {
                        val msg = res.body() ?: ""
                        if (msg.contains("Error", ignoreCase = true)) toast(msg) else onOk()
                    } else toast("Error ${res.code()}")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { toast("Red: ${e.localizedMessage}") }
            }
        }
    }

    private fun eliminar(id: Int, onOk: () -> Unit) {
        io.launch {
            try {
                val res = RetrofitInstance.api2kotlin.eliminarJugador(id)
                withContext(Dispatchers.Main) {
                    if (res.isSuccessful) onOk() else toast("Error ${res.code()}")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { toast("Red: ${e.localizedMessage}") }
            }
        }
    }

    private fun limpiarInputs() {
        etNombre.text.clear(); etDocumento.text.clear(); etNacimiento.text.clear()
        etEmail.text.clear(); etPassword.text.clear(); etGenero.text.clear()
        etEdad.text.clear(); etUserId.text.clear(); etEquipoId.text.clear()
        etIdActualizar.text.clear(); etIdEliminar.text.clear()
    }

    private fun toast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}