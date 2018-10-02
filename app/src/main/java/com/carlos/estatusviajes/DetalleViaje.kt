package com.carlos.estatusviajes


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
import kotlinx.android.synthetic.main.detallerider.view.*
import kotlinx.android.synthetic.main.fragment_detalle_viaje.*


class DetalleViaje : Fragment() {

    private lateinit var viaje: ViajeDetalle
    private var bd: ControladorBD? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_detalle_viaje, container, false)
    }

    override fun onStart() {
        super.onStart()
        viaje = arguments?.getSerializable("viaje") as ViajeDetalle
        bd = ControladorBD(activity!!.applicationContext)
        detalleContenidoRider.text = bd!!.UsuarioDatos(viaje.rider).name



        if (viaje.estatus == "TRIP_ENDED") {
            detalleContenidoStatus.text = (view!!.context as AppCompatActivity).getString(R.string.Completado)
            detalleContenidoStatus.setTextColor(ContextCompat.getColor(view!!.context, R.color.CompletadoColor))
        } else {
            detalleContenidoStatus.text = (view!!.context as AppCompatActivity).getString(R.string.NoAtendido)
            detalleContenidoStatus.setTextColor(ContextCompat.getColor(view!!.context, R.color.NoAtendidoColor))
        }

        detalleRecycler.layoutManager = LinearLayoutManager(activity!!.applicationContext)


        detalleRecycler.adapter = RiderAdaptador(bd!!.ConsultaRider(viaje.fecha.trim(), viaje.estatus.trim(), viaje.rider.trim()))

    }

}

class RiderAdaptador(val viaje: MutableList<ViajeRiderDetalle>) : RecyclerView.Adapter<RiderViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RiderViewHolder {
        return RiderViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.detallerider, parent, false))
    }

    override fun getItemCount(): Int {
        return viaje.size
    }

    override fun onBindViewHolder(holder: RiderViewHolder, position: Int) {
        val dato = viaje.get(position)
        var bd = ControladorBD(holder.view.context)



        if (dato.supply == "-") {
            holder.view.driverC.visibility = View.GONE
            holder.view.driverT.visibility = View.GONE
            holder.view.telefonoC.visibility = View.GONE
            holder.view.telefonoT.visibility = View.GONE
            holder.view.tksC.visibility = View.GONE
            holder.view.tksT.visibility = View.GONE
        }
        if (dato.supply != "-") {
            var datos = bd!!.UsuarioDatos(dato.supply)
            holder.view.driverC.text = datos.name
            holder.view.telefonoC.text = datos.phone
            holder.view.riderHora.text = dato.hora
            holder.view.ridercontenidoOrigen.text = dato.origen
            holder.view.ridercontenidoDestino.text = dato.destino
            holder.view.tksC.text = "${dato.tks} TKS"
        } else {
            holder.view.riderHora.text = dato.hora
            holder.view.ridercontenidoOrigen.text = dato.origen
            holder.view.ridercontenidoDestino.text = dato.destino
        }
    }
}


class RiderViewHolder(val view: View, var viajes: ViajeDetalle? = null) : RecyclerView.ViewHolder(view) {
    init {

    }
}
