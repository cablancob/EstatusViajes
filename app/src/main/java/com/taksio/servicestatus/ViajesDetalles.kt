package com.taksio.servicestatus

import Controlador.ControladorBD
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.detalleviaje.view.*
import kotlinx.android.synthetic.main.fragment_viajes_detalles.*


class ViajesDetalles : Fragment() {

    private var bd: ControladorBD? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_viajes_detalles, container, false)
    }

    override fun onStart() {
        super.onStart()
        val fecha = arguments?.getString("FECHA")
        fechaDetalle.text = fecha
        bd = ControladorBD(activity!!.applicationContext)

        RecyclerDetalles.layoutManager = LinearLayoutManager(activity!!.applicationContext)
        RecyclerDetalles.adapter = DetalleAdaptador(bd!!.ConsultaViaje(fecha!!))
    }

}


class DetalleAdaptador(val viajes: MutableList<ViajeDetalle>) : RecyclerView.Adapter<DetalleViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetalleViewHolder {
        return DetalleViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.detalleviaje, parent, false))
    }

    override fun getItemCount(): Int {
        return viajes.size
    }

    override fun onBindViewHolder(holder: DetalleViewHolder, position: Int) {
        val viaje = viajes.get(position)

        var bd = ControladorBD(holder.view.context)

        var datos = bd.UsuarioDatos(viaje.rider)
        holder.view.contenidoRider.text = datos.name
        holder.view.contenidoEmail.text = datos.email
        holder.view.contenidoCel.text = datos.phone
        holder.view.contenidoCantidad.text = viaje.count.toString()

        if (viaje.estatus.trim() == "TRIP_ENDED") {
            holder.view.contenidoStatus.setTextColor(ContextCompat.getColor(holder.view.context, R.color.CompletadoColor))
            holder.view.contenidoStatus.text = (holder.view.context as AppCompatActivity).resources.getString(R.string.Completado)
        } else {
            holder.view.contenidoStatus.setTextColor(ContextCompat.getColor(holder.view.context, R.color.NoAtendidoColor))
            holder.view.contenidoStatus.text = (holder.view.context as AppCompatActivity).resources.getString(R.string.NoAtendido)
        }

        holder.viajes = viaje

    }




}

class DetalleViewHolder(val view: View, var viajes: ViajeDetalle? = null) : RecyclerView.ViewHolder(view) {
    init {
        view.setOnClickListener {
            val args = Bundle()
            args.putSerializable("viaje", viajes)
            val fragmento = DetalleViaje()
            fragmento.arguments = args

            (view.context as AppCompatActivity)
                    .supportFragmentManager
                    .beginTransaction()
                    .add(R.id.Frame, fragmento, "DETALLE")
                    .addToBackStack(null)
                    .commit()
        }
    }
}
