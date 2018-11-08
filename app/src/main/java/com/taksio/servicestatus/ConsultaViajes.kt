package com.taksio.servicestatus

import Controlador.ControladorBD
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.Toast
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_consulta_viajes.*
import kotlinx.android.synthetic.main.graficos_menu.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.*
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.CountDownLatch


class ConsultaViajes : Fragment() {

    private var cal = Calendar.getInstance()
    private var bd: ControladorBD? = null
    val formatoFecha = "yyyy-MM-dd"
    val formatoHora = "HH:mm:ss"
    var fechaConsultaI = 0L
    var fechaConsultaF = 0L
    lateinit var semaforo: CountDownLatch

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_consulta_viajes, container, false)
    }

    override fun onStart() {
        super.onStart()


        bd = ControladorBD(activity!!.applicationContext)


        val dateListener = object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
                cal = Calendar.getInstance()
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, month)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                DesdeContenido.text = SimpleDateFormat(formatoFecha).format(cal.time).toString()

                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, month)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                //cal.add(Calendar.HOUR, -4)

                fechaConsultaI = cal.timeInMillis / 1000

            }

        }

        val dateListener2 = object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
                cal = Calendar.getInstance()
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, month)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)


                HastaContenido.text = SimpleDateFormat(formatoFecha).format(cal.time).toString()

                cal = Calendar.getInstance()
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, month)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                cal.set(Calendar.HOUR_OF_DAY, 23)
                cal.set(Calendar.MINUTE, 59)
                cal.set(Calendar.SECOND, 59)
                // cal.add(Calendar.HOUR, -12)

                fechaConsultaF = cal.timeInMillis / 1000

            }

        }

        Graficos.setOnClickListener {
            MenuGraficos(it)
        }

        Consultar.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                try {
                    if (ValidarCampos()) {
                        bd!!.Drop()
                        bd!!.Create()
                        if (verifyAvailableNetwork((activity as MainActivity))) {
                            semaforo = CountDownLatch(2)
                            var dialogo = AlertDialog.Builder(context)
                            dialogo.setView(layoutInflater.inflate(R.layout.layout_loading_dialog, null))
                            dialogo.setCancelable(false)
                            val dialogo_show = dialogo.show()

                            GlobalScope.launch {
                                AppEndPointData(fechaConsultaI, fechaConsultaF)
                            }
                            GlobalScope.launch {
                                CallCenterEndPointData(DesdeContenido.text.toString(), HastaContenido.text.toString())
                            }
                            GlobalScope.launch {
                                semaforo.await()
                                activity!!.runOnUiThread {
                                    bd!!.ActualizarDatosUsuarios(context!!)
                                    ListaViajes.adapter = AdaptadorPrincipal(bd!!.Select())
                                    Graficos.visibility = View.VISIBLE
                                    dialogo_show.cancel()
                                    Log.d("LOG:", "FIN DE CICLO PRINCIPAL")
                                }
                            }


                        } else {
                            Toast.makeText(context, "No hay conexion a internet, por favor validar", Toast.LENGTH_SHORT).show()
                        }

                    }
                    /*   ListaViajes.adapter = AdaptadorPrincipal(bd!!.Select())
                   Graficos.visibility = View.VISIBLE*/
                } catch (e: Exception) {
                    activity!!.runOnUiThread {
                        Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show()
                    }
                }
            }

        })


        Calendario1!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                cal = Calendar.getInstance()
                val Calendario = DatePickerDialog(v!!.context, dateListener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
                Calendario.datePicker.maxDate = System.currentTimeMillis()
                Calendario.show()
            }

        })

        Calendario2!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                cal = Calendar.getInstance()
                val Calendario = DatePickerDialog(v!!.context, dateListener2, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
                Calendario.datePicker.maxDate = System.currentTimeMillis()
                Calendario.show()


            }

        })

        ListaViajes.setItemViewCacheSize(200)
        ListaViajes.isDrawingCacheEnabled = true
        ListaViajes.layoutManager = LinearLayoutManager(activity!!.applicationContext)
    }

    fun AppEndPointData(fechaInicial: Long, fechaFinal: Long) {


        Log.d("LOG:", "COMIENZO DE CICLO")

        var url = "https://services.taksio.net/services"


        if ((fechaInicial != 0L) and (fechaFinal == 0L)) {
            url = url + "?start=${fechaInicial}"

        }
        if ((fechaInicial == 0L) and (fechaFinal != 0L)) {
            url = url + "?end=${fechaFinal}"

        }
        if ((fechaInicial != 0L) and (fechaFinal != 0L)) {
            url = url + "?start=${fechaInicial}&end=${fechaFinal}"

        }

        //DEBUG
        //url = url + "?start=1533096000&end=1533268799"
        val client = OkHttpClient()


        client.newCall(Request.Builder()
                .url(url)
                .addHeader("authorization", "Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjU0NTM0MmEwLWExOTktNGJhOC1iZjU5LWY4ZWRiYTE4Mjk3MiIsImlwIjoiMTI3LjAuMC4xIiwiaWF0IjoxNTA5NzU0ODQzfQ.lrJSPmkRs3ka8JckL9WZSPTbvl018ZB8EPiRP9AvPS2w9D0La-NvoL7tTDhswAFY6_Od3Ot-jDlcsN9zVuiSmI02ZZ5paUklLDYV9WjMQO0MLbGU8GiB6IwsXYTZqJ8-dn-B46JVhMrZbFLLTfETss6e4r-hzJsK4hXmQObWAfBBbW3QRQXMlAIrbFURhwZdafyd7o7BUdficlb4Sxtl473IypDu9N5RS0gngvmQFKm5nRDgCIQWDHjy20Mr2U8Ola84x8BzS5GswBU44p2z1vjFCZ_UBFauC2Z_8HA-U9UbGq2Q6EoFX3GV91SyjZwFRw40bLcjf-DkdAdE2g22vg")
                .build()).enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                println("ERROR: ${e!!.message}")

                (activity as MainActivity).runOnUiThread {
                    Toast.makeText(context, "Hubo un problema en la consulta, por favor verifique su conexión a Internet e intente de nuevo", Toast.LENGTH_SHORT).show()
                }
                semaforo.countDown()


            }

            override fun onResponse(call: Call?, response: Response?) {
                val list: MutableList<Viajes>
                val listType = object : TypeToken<List<Viajes>>() {

                }.type

                if (DesdeContenido.text != "AAAA-MM-DD") {

                    cal = Calendar.getInstance()
                    cal.set(Calendar.YEAR, DesdeContenido.text.split("-")[0].toInt())
                    cal.set(Calendar.MONTH, DesdeContenido.text.split("-")[1].toInt() - 1)
                    cal.set(Calendar.DATE, DesdeContenido.text.split("-")[2].toInt())

                } else {
                    cal = Calendar.getInstance()
                    cal.set(Calendar.YEAR, 2018)
                    cal.set(Calendar.MONTH, 2)
                    cal.set(Calendar.DATE, 9)
                }



                list = GsonBuilder().create().fromJson(response?.body()?.string(), listType)

                val jdf = SimpleDateFormat(formatoFecha)
                jdf.timeZone = TimeZone.getTimeZone("GMT-4")
                val hora = SimpleDateFormat(formatoHora)
                hora.timeZone = TimeZone.getTimeZone("GMT-4")

                list.forEach {
                    if (it.desc.trim() != "TRIP_CANCELLED") {
                        val date = Date(it.request_time.split(".").get(0).toInt() * 1000L)
                        // format of the date


                        bd!!.Insert(
                                Viajes(it.desc.trim(),
                                        jdf.format(date).trim(),
                                        it.demand.split(":")[1].trim(),
                                        when {
                                            it.supply == null -> {
                                                "-"
                                            }
                                            else -> {
                                                it.supply.split(":")[1].trim()
                                            }

                                        },
                                        hora.format(date).toString().trim(),
                                        features(it.features.origin, it.features.destination),
                                        billing(fare(it.billing.fare.amount)),
                                        when {
                                            it.supply_accept_time == null -> {
                                                "-"
                                            }
                                            else -> {
                                                hora.format(Date(it.supply_accept_time.split(".").get(0).toInt() * 1000L)).toString().trim()
                                            }
                                        },
                                        it.cancel_reason,
                                        it.user_cancel,
                                        when {
                                            it.supply_arrive_time == null -> {
                                                "-"
                                            }
                                            else -> {
                                                hora.format(Date(it.supply_arrive_time.split(".").get(0).toInt() * 1000L)).toString().trim()
                                            }
                                        },
                                        it.supply_arrive_location,
                                        it.supply_accept_location,
                                        it.supply_cancel_location,
                                        when {
                                            it.cancel_time == null -> {
                                                "-"
                                            }
                                            else -> {
                                                hora.format(Date(it.cancel_time.split(".").get(0).toInt() * 1000L)).toString().trim()
                                            }
                                        }, "A"
                                )
                        )

                    } else {
                        var cancelado = it
                        if (cancelado.supply_accept_location != null) {
                            val date = Date(it.request_time.split(".").get(0).toInt() * 1000L)
                            bd!!.Insert(
                                    Viajes(it.desc.trim(),
                                            jdf.format(date).trim(),
                                            it.demand.split(":")[1].trim(),
                                            when {
                                                it.supply == null -> {
                                                    "-"
                                                }
                                                else -> {
                                                    it.supply.split(":")[1].trim()
                                                }

                                            },
                                            hora.format(date).toString().trim(),
                                            features(it.features.origin, it.features.destination),
                                            billing(fare(it.billing.fare.amount)),
                                            when {
                                                it.supply_accept_time == null -> {
                                                    "-"
                                                }
                                                else -> {
                                                    hora.format(Date(it.supply_accept_time.split(".").get(0).toInt() * 1000L)).toString().trim()
                                                }
                                            },
                                            it.cancel_reason,
                                            it.user_cancel,
                                            when {
                                                it.supply_arrive_time == null -> {
                                                    "-"
                                                }
                                                else -> {
                                                    hora.format(Date(it.supply_arrive_time.split(".").get(0).toInt() * 1000L)).toString().trim()
                                                }
                                            },
                                            it.supply_arrive_location,
                                            it.supply_accept_location,
                                            it.supply_cancel_location,
                                            when {
                                                it.cancel_time == null -> {
                                                    "-"
                                                }
                                                else -> {
                                                    hora.format(Date(it.cancel_time.split(".").get(0).toInt() * 1000L)).toString().trim()
                                                }
                                            }, "A"
                                    )
                            )
                        }

                    }
                }
                semaforo.countDown()
                println("FIN DEL CICLO APP")
            }
        }
        )
    }

    fun CallCenterEndPointData(date_from: String, date_to: String) {
        var desde = ""
        var hasta = ""

        if (date_from != "AAAA-MM-DD") {
            desde = date_from
        }

        if (date_to != "AAAA-MM-DD") {
            hasta = date_to
        }

        var Json = "{\"date_from\" : \"${desde}\",\"date_to\" : \"${hasta}\"}"

        val client = OkHttpClient()


        client.newCall(Request.Builder()
                .url("http://experimental.taksio.net:8111/callcenter")
                .post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), Json))
                .build()).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {

                (activity as MainActivity).runOnUiThread {
                    Toast.makeText(context, "Hubo un problema en la consulta, por favor verifique su conexión a Internet e intente de nuevo", Toast.LENGTH_SHORT).show()
                }
                semaforo.countDown()


            }

            override fun onResponse(call: Call, response: Response) {
                val list: MutableList<CallCenterData>
                val listType = object : TypeToken<List<CallCenterData>>() {

                }.type
                var data = response.body()?.string()?.trim()
                if (data != "") {
                    list = GsonBuilder().create().fromJson(data, listType)


                    list.forEach {
                        bd!!.Insert(Viajes(
                                "TRIP_ENDED",
                                it.date_ride,
                                it.rider,
                                it.driver,
                                "-",
                                features(it.origin, it.destination),
                                billing(fare(it.tks)),
                                "-",
                                "-",
                                "-",
                                "-",
                                "-",
                                "-",
                                "-",
                                "-",
                                "C"
                        ))
                    }
                    println("FIN DEL CICLO CALL")
                    semaforo.countDown()
                } else {
                    semaforo.countDown()
                }
            }

        })


    }

    fun ValidarCampos(): Boolean {
        var boolean = true
        if (!DesdeContenido.text.equals("AAAA-MM-DD") and !HastaContenido.text.equals("AAAA-MM-DD")) {
            if (fechaConsultaF < fechaConsultaI) {
                Toast.makeText(activity!!.applicationContext, "El campo fecha HASTA no puede ser menor que el campo fecha DESDE", Toast.LENGTH_LONG).show()
                boolean = false
            }
        }
        return boolean
    }

    fun verifyAvailableNetwork(activity: AppCompatActivity): Boolean {
        val connectivityManager = activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        //return  networkInfo!=null && networkInfo.isConnected
        return true
    }

    fun MenuGraficos(view: View) {
        val dialogo_layout = layoutInflater.inflate(R.layout.graficos_menu, null)
        val dialogo = AlertDialog.Builder(view.context).setView(dialogo_layout)

        val dialogo_show = dialogo.show()

        dialogo_show.graficos_total.setOnClickListener {
            dialogo_show.cancel()
            (context as AppCompatActivity).supportFragmentManager
                    .beginTransaction()
                    .add(R.id.Frame, GraficoTotal(), "GRAFICOS_TOTALES")
                    .addToBackStack(null)
                    .commit()
        }

        dialogo_show.graficos_detalles.setOnClickListener {
            dialogo_show.cancel()
            (context as AppCompatActivity).supportFragmentManager
                    .beginTransaction()
                    .add(R.id.Frame, GraficoDetalle(), "GRAFICOS DETALLES")
                    .addToBackStack(null)
                    .commit()
        }


        dialogo_show.graficos_detalle_rider.setOnClickListener {
            dialogo_show.cancel()
            (context as AppCompatActivity).supportFragmentManager
                    .beginTransaction()
                    .add(R.id.Frame, GraficoDetalleRider(), "GRAFICO DETALLE RIDER")
                    .addToBackStack(null)
                    .commit()
        }

        dialogo_show.graficos_total_driver.setOnClickListener {
            dialogo_show.cancel()
            (context as AppCompatActivity).supportFragmentManager
                    .beginTransaction()
                    .add(R.id.Frame, GraficoTotalDriver(), "GRAFICO TOTAL DRIVER")
                    .addToBackStack(null)
                    .commit()
        }


    }


}
