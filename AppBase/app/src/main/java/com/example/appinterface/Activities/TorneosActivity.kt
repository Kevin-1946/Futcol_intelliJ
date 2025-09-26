package com.example.appinterface.Activities

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appinterface.Api.*
import com.example.appinterface.Adapter.TorneoAdapter
import com.example.appinterface.Models.TorneoCU
import com.example.appinterface.R
import kotlinx.coroutines.*

class TorneosActivity : AppCompatActivity() {

    private val io = CoroutineScope(Dispatchers.IO)

    private lateinit var tvRole: TextView
    private lateinit var adminPanel: View
    private lateinit var adapter: TorneoAdapter

    // Inputs admin
    private lateinit var etNombre: EditText
    private lateinit var etInicio: EditText
    private lateinit var etFin: EditText
    private lateinit var etCategoria: EditText
    private lateinit var etModalidad: EditText
    private lateinit var etOrganizador: EditText
    private lateinit var etPrecio: EditText
    private lateinit var etSedes: EditText
    private lateinit var etIdActualizar: EditText
    private lateinit var etIdEliminar: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_torneos)

        val isAdmin = intent.getBooleanExtra("isAdmin", false)

        tvRole = findViewById(R.id.tvRole)
        adminPanel = findViewById(R.id.adminPanel)
        tvRole.text = if (isAdmin) "Rol: ADMIN" else "Rol: CAPITAN"
        adminPanel.visibility = if (isAdmin) View.VISIBLE else View.GONE

        // Recycler
        val rv = findViewById<RecyclerView>(R.id.rvTorneos)
        rv.layoutManager = LinearLayoutManager(this)
        adapter = TorneoAdapter(mutableListOf()) { seleccionado ->
            // Al tocar un item, llenamos los campos para actualizar/eliminar
            if (isAdmin) {
                etIdActualizar.setText(seleccionado.id.toString())
                etNombre.setText(seleccionado.nombre)
                etInicio.setText(seleccionado.fecha_inicio)
                etFin.setText(seleccionado.fecha_fin)
                etCategoria.setText(seleccionado.categoria)
                etModalidad.setText(seleccionado.modalidad)
                etOrganizador.setText(seleccionado.organizador)
                etPrecio.setText(seleccionado.precio.toString())
                etSedes.setText(seleccionado.sedes)
            }
        }
        rv.adapter = adapter

        // Botón listar
        findViewById<Button>(R.id.btnRefrescar).setOnClickListener { listar() }

        if (isAdmin) {
            // Referencias panel admin
            etNombre = findViewById(R.id.etNombre)
            etInicio = findViewById(R.id.etInicio)
            etFin = findViewById(R.id.etFin)
            etCategoria = findViewById(R.id.etCategoria)
            etModalidad = findViewById(R.id.etModalidad)
            etOrganizador = findViewById(R.id.etOrganizador)
            etPrecio = findViewById(R.id.etPrecio)
            etSedes = findViewById(R.id.etSedes)
            etIdActualizar = findViewById(R.id.etIdActualizar)
            etIdEliminar = findViewById(R.id.etIdEliminar)

            // Crear
            findViewById<Button>(R.id.btnCrear).setOnClickListener {
                val precio = etPrecio.text.toString().toDoubleOrNull()
                if (etNombre.text.isBlank() || precio == null) {
                    toast("Completa nombre y precio")
                    return@setOnClickListener
                }
                val body = buildCU(precio)
                crear(body) {
                    limpiarInputs()
                    listar()
                }
            }

            // Actualizar
            findViewById<Button>(R.id.btnActualizar).setOnClickListener {
                val id = etIdActualizar.text.toString().toIntOrNull()
                val precio = etPrecio.text.toString().toDoubleOrNull()
                if (id == null) { toast("ID inválido"); return@setOnClickListener }
                if (etNombre.text.isBlank() || precio == null) { toast("Completa nombre y precio"); return@setOnClickListener }
                val body = buildCU(precio)
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

    private fun buildCU(precio: Double) = TorneoCU(
        nombre = etNombre.text.toString().trim(),
        fecha_inicio = etInicio.text.toString().trim(), // "YYYY-MM-DD"
        fecha_fin = etFin.text.toString().trim(),       // "YYYY-MM-DD"
        categoria = etCategoria.text.toString().trim(),
        modalidad = etModalidad.text.toString().trim(),
        organizador = etOrganizador.text.toString().trim(),
        precio = precio,
        sedes = etSedes.text.toString().trim()
    )

    private fun listar() {
        io.launch {
            try {
                val res = RetrofitInstance.api2kotlin.getTorneos()
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

    private fun crear(body: TorneoCU, onOk: () -> Unit) {
        io.launch {
            try {
                val res = RetrofitInstance.api2kotlin.crearTorneo(body)
                withContext(Dispatchers.Main) {
                    if (res.isSuccessful) onOk() else toast("Error ${res.code()}")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { toast("Red: ${e.localizedMessage}") }
            }
        }
    }

    private fun actualizar(id: Int, body: TorneoCU, onOk: () -> Unit) {
        io.launch {
            try {
                val res = RetrofitInstance.api2kotlin.actualizarTorneo(id, body)
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
                val res = RetrofitInstance.api2kotlin.eliminarTorneo(id)
                withContext(Dispatchers.Main) {
                    if (res.isSuccessful) onOk() else toast("Error ${res.code()}")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { toast("Red: ${e.localizedMessage}") }
            }
        }
    }

    private fun limpiarInputs() {
        etNombre.text.clear(); etInicio.text.clear(); etFin.text.clear()
        etCategoria.text.clear(); etModalidad.text.clear(); etOrganizador.text.clear()
        etPrecio.text.clear(); etSedes.text.clear(); etIdActualizar.text.clear(); etIdEliminar.text.clear()
    }

    private fun toast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}