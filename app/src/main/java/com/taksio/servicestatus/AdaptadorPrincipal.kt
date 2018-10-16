package com.taksio.servicestatus

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.estatusviajes.view.*

class AdaptadorPrincipal(val viajesContados: MutableList<ViajesContados>) : RecyclerView.Adapter<CustomViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        return CustomViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.estatusviajes, parent, false))
    }

    override fun getItemCount(): Int {
        return viajesContados.size
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {

        val viajesContados = viajesContados.get(position)

        holder.view.Fecha.text = viajesContados.request_time
        holder.view.Cantidad.text = viajesContados.completados
        holder.view.CantidadC.text = viajesContados.noatendidos
        holder.view.CantidadCCA.text = viajesContados.cancelados

        holder.viajesContados = viajesContados

    }

}


class CustomViewHolder(val view: View, var viajesContados: ViajesContados? = null) : RecyclerView.ViewHolder(view) {
    init {
        view.setOnClickListener {
            val args = Bundle()
            args.putString("FECHA", viajesContados!!.request_time)
            val fragmento = ViajesDetalles()
            fragmento.arguments = args

            (view.context as AppCompatActivity)
                    .supportFragmentManager
                    .beginTransaction()
                    .add(R.id.Frame, fragmento, "LISTA_DETALLES")
                    .addToBackStack(null)
                    .commit()
        }
    }
}