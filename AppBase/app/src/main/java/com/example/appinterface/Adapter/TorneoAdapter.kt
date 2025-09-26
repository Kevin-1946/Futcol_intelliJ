package com.example.appinterface.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appinterface.Models.Torneo
import com.example.appinterface.R

class TorneoAdapter(
    private val data: MutableList<Torneo>,
    private val onClick: (Torneo) -> Unit
) : RecyclerView.Adapter<TorneoAdapter.VH>() {

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val tvTitle: TextView = v.findViewById(R.id.tvItemTitle)
        val tvSub: TextView = v.findViewById(R.id.tvItemSub)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_torneo, parent, false)
        return VH(v)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val t = data[position]
        holder.tvTitle.text = "#${t.id} · ${t.nombre}"
        holder.tvSub.text = "${t.categoria} · ${t.modalidad} · ${t.fecha_inicio}→${t.fecha_fin}"
        holder.itemView.setOnClickListener { onClick(t) }
    }

    fun setItems(newItems: List<Torneo>) {
        data.clear()
        data.addAll(newItems)
        notifyDataSetChanged()
    }
}
