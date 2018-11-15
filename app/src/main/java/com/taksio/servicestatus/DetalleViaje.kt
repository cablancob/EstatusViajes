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
        } else if (viaje.estatus.trim() == "REQUEST_TIMEOUT") {
            detalleContenidoStatus.text = (view!!.context as AppCompatActivity).getString(R.string.NoAtendido)
            detalleContenidoStatus.setTextColor(ContextCompat.getColor(view!!.context, R.color.NoAtendidoColor))
        } else {
            detalleContenidoStatus.text = (view!!.context as AppCompatActivity).getString(R.string.Cancelados)
            detalleContenidoStatus.setTextColor(ContextCompat.getColor(view!!.context, R.color.CanceladosColor))
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


        if (dato.estatus == "TRIP_ENDED") {
            ocultar_cancelados(holder)
            var datos = bd.UsuarioDatos(dato.supply)
            holder.view.driverC.text = datos.name
            holder.view.telefonoC.text = datos.phone
            holder.view.riderHora.text = dato.hora
            holder.view.ridercontenidoOrigen.text = dato.origen
            holder.view.ridercontenidoDestino.text = dato.destino
            holder.view.tksC.text = "${dato.tks} TKS"

        }

        if (dato.estatus == "REQUEST_TIMEOUT") {
            ocultar_cancelados(holder)
            holder.view.driverC.visibility = View.GONE
            holder.view.driverT.visibility = View.GONE
            holder.view.telefonoC.visibility = View.GONE
            holder.view.telefonoT.visibility = View.GONE
            holder.view.tksC.visibility = View.GONE
            holder.view.tksT.visibility = View.GONE

            holder.view.riderHora.text = dato.hora
            holder.view.ridercontenidoOrigen.text = dato.origen
            holder.view.ridercontenidoDestino.text = dato.destino
        }

        if (dato.estatus == "TRIP_CANCELLED") {
            var datos = bd.UsuarioDatos(dato.supply)
            holder.view.driverC.text = datos.name
            holder.view.telefonoC.text = datos.phone
            holder.view.riderHora.text = dato.hora
            holder.view.ridercontenidoOrigen.text = dato.origen
            holder.view.ridercontenidoDestino.text = dato.destino
            holder.view.tksC.text = "${dato.tks} TKS"

            holder.view.cancelH.text = dato.cancel_time

            holder.view.cancelU.text = bd.UsuarioDatos(dato.user_cancel!!.split(":").get(1)).name

            if (dato.cancel_reason == "CANCEL_TIMEOUT") {
                holder.view.cancelR.text = (holder.view.context as AppCompatActivity).resources.getString(R.string.CANCEL_TIMEOUT)
            }
            if (dato.cancel_reason == "CANCEL_MISMATCH_PAYLOAD") {
                holder.view.cancelR.text = (holder.view.context as AppCompatActivity).resources.getString(R.string.CANCEL_MISMATCH_PAYLOAD)
            }
            if (dato.cancel_reason == "CANCEL_MISMATCH_ID") {
                holder.view.cancelR.text = (holder.view.context as AppCompatActivity).resources.getString(R.string.CANCEL_MISMATCH_ID)
            }
            if (dato.cancel_reason == "CANCEL_OTHER") {
                holder.view.cancelR.text = (holder.view.context as AppCompatActivity).resources.getString(R.string.CANCEL_OTHER)
            }


            holder.view.aceptH.text = dato.supply_accept_time

            if (dato.supply_cancel_location != null) {
                holder.view.canceloImg.setOnClickListener {
                    val args = Bundle()
                    args.putString("cordenadas", dato.supply_cancel_location)
                    val fragmento = UbicacionMapa()
                    fragmento.arguments = args
                    (holder.view.context as AppCompatActivity).supportFragmentManager
                            .beginTransaction()
                            .add(R.id.Frame, fragmento, "MAPA")
                            .addToBackStack(null)
                            .commit()
                }
            } else {
                holder.view.cancelLT.visibility = View.GONE
                holder.view.canceloImg.visibility = View.GONE
            }

            if (dato.supply_accept_location != null) {
                holder.view.aceptoImg.setOnClickListener {
                    val args = Bundle()
                    args.putString("cordenadas", dato.supply_accept_location)
                    val fragmento = UbicacionMapa()
                    fragmento.arguments = args
                    (holder.view.context as AppCompatActivity).supportFragmentManager
                            .beginTransaction()
                            .add(R.id.Frame, fragmento, "MAPA")
                            .addToBackStack(null)
                            .commit()
                }
            } else {
                holder.view.aceptLT.visibility = View.GONE
                holder.view.aceptoImg.visibility = View.GONE
            }


            if (dato.supply_arrive_location != null) {
                holder.view.aceptH.text = dato.supply_arrive_time
                holder.view.llegoImg.setOnClickListener {
                    val args = Bundle()
                    args.putString("cordenadas", dato.supply_arrive_location)
                    val fragmento = UbicacionMapa()
                    fragmento.arguments = args
                    (holder.view.context as AppCompatActivity).supportFragmentManager
                            .beginTransaction()
                            .add(R.id.Frame, fragmento, "MAPA")
                            .addToBackStack(null)
                            .commit()
                }
            } else {
                holder.view.llegoH.visibility = View.GONE
                holder.view.llegoHT.visibility = View.GONE
                holder.view.llegoLT.visibility = View.GONE
                holder.view.llegoImg.visibility = View.GONE
            }

        }

        if (dato.date_from == "C") {
            holder.view.riderTitulo.text = "Call Center"
            holder.view.riderHora.visibility = View.GONE
        }
    }

    fun ocultar_cancelados(holder: RiderViewHolder) {
        holder.view.cancelUT.visibility = View.GONE
        holder.view.cancelU.visibility = View.GONE
        holder.view.cancelHT.visibility = View.GONE
        holder.view.cancelH.visibility = View.GONE
        holder.view.cancelRT.visibility = View.GONE
        holder.view.cancelR.visibility = View.GONE
        holder.view.cancelLT.visibility = View.GONE
        holder.view.aceptT.visibility = View.GONE
        holder.view.aceptHT.visibility = View.GONE
        holder.view.aceptH.visibility = View.GONE
        holder.view.aceptLT.visibility = View.GONE
        holder.view.llegoHT.visibility = View.GONE
        holder.view.llegoH.visibility = View.GONE
        holder.view.llegoLT.visibility = View.GONE

        holder.view.canceloImg.visibility = View.GONE
        holder.view.aceptoImg.visibility = View.GONE
        holder.view.llegoImg.visibility = View.GONE
    }
}


class RiderViewHolder(val view: View, var viajes: ViajeDetalle? = null) : RecyclerView.ViewHolder(view) {
    init {

    }
}
