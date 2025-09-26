package com.example.appinterface.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appinterface.Models.Jugador
import com.example.appinterface.R

class JugadorAdapter(
    private val data: MutableList<Jugador>,
    private val onClick: (Jugador) -> Unit
) : RecyclerView.Adapter<JugadorAdapter.VH>() {

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val tvTitle: TextView = v.findViewById(R.id.tvItemTitle)
        val tvSub: TextView   = v.findViewById(R.id.tvItemSub)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_jugador, parent, false)
        return VH(v)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val j = data[position]
        holder.tvTitle.text = "#${j.id} · ${j.nombre}"
        holder.tvSub.text   = "Doc:${j.n_documento} · ${j.genero} · Edad:${j.edad} · Equipo:${j.equipo_id}"
        holder.itemView.setOnClickListener { onClick(j) }
    }

    fun setItems(newItems: List<Jugador>) {
        data.clear()
        data.addAll(newItems)
        notifyDataSetChanged()
    }
}