package com.taksio.servicestatus


import Controlador.ControladorBD
import android.app.AlertDialog
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.IValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.fragment_grafico_total_driver.*
import kotlinx.android.synthetic.main.layout_driver_datos.*
import okhttp3.*
import java.io.IOException


class GraficoTotalDriver : Fragment() {

    var datos_bd: MutableList<GraficoTD> = mutableListOf()
    val Etiquetas = ArrayList<String>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_grafico_total_driver, container, false)
    }

    override fun onStart() {
        super.onStart()

        val letra = 14f

        val bd = ControladorBD(context!!)
        var entries: MutableList<BarEntry>
        val data_final: MutableList<IBarDataSet> = mutableListOf()


        var maximo = 0F


        var contador = 0
        datos_bd = ControladorBD(context!!).GraficoTotalTD()

        datos_bd.forEach {
            entries = mutableListOf()
            entries.add(BarEntry(contador.toFloat(), it.y.toFloat()))

            if (it.y.toFloat() > maximo) {
                maximo = it.y.toFloat()
            }

            val DataSet = BarDataSet(entries, it.driver)
            DataSet.valueTextSize = letra
            DataSet.valueTextColor = ContextCompat.getColor(view!!.context, R.color.GraficoTexto)
            DataSet.color = ContextCompat.getColor(view!!.context, R.color.CompletadoColor)
            DataSet.valueFormatter = IValueFormatter { value, _, _, _ -> value.toInt().toString() }
            DataSet.setDrawValues(true)
            data_final.add(DataSet)
            Etiquetas.add(bd.UsuarioDatos(it.driver).name)
            contador += 1
        }

        val x = GraficoTD.xAxis

        x.position = XAxis.XAxisPosition.BOTTOM
        x.labelCount = 3
        x.textSize = letra
        x.textColor = ContextCompat.getColor(view!!.context, R.color.GraficoTexto)
        x.isEnabled = false
        x.setDrawGridLines(false)


        GraficoTD.data = BarData(data_final)
        GraficoTD.legend.isEnabled = false
        GraficoTD.description.text = "PRESIONE UNA BARRA PARA VER MAS DETALLES"
        GraficoTD.description.textSize = letra - 2
        GraficoTD.setVisibleXRangeMaximum(3.5f)
        GraficoTD.animateY(2000)

        GraficoTD.axisLeft.axisMinimum = -1f
        GraficoTD.axisLeft.textColor = ContextCompat.getColor(view!!.context, R.color.GraficoTexto)
        GraficoTD.axisRight.textColor = ContextCompat.getColor(view!!.context, R.color.GraficoTexto)
        GraficoTD.axisRight.axisMinimum = -1f

        GraficoTD.extraBottomOffset = 24F

        GraficoTD.setBackgroundColor(ContextCompat.getColor(view!!.context, R.color.FondoGrafico))

        GraficoTD.setScaleEnabled(false)

        GraficoTD.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onNothingSelected() {

            }

            override fun onValueSelected(e: Entry?, h: Highlight?) {
                CargarDatosDriver(h!!.dataSetIndex)
            }

        })

        GraficoTD.invalidate()

    }

    fun CargarDatosDriver(position: Int) {

        var url = "http://driver.taksio.net/taksio/public/ws.php?uuid=${datos_bd.get(position).driver}"

        val client = OkHttpClient()

        client.newCall(Request.Builder()
                .url(url)
                .build()).enqueue(object : Callback {

            override fun onFailure(call: Call?, e: IOException?) {
                println("ERROR: ${e!!.message}")
            }

            override fun onResponse(call: Call?, response: Response?) {
                (activity as MainActivity).runOnUiThread {
                    var list = GsonBuilder().create().fromJson(response?.body()?.string(), FotoDriver::class.java)

                    val dialogo_datos = AlertDialog.Builder(context).setView(layoutInflater.inflate(R.layout.layout_driver_datos, null))

                    val dialogo_datos_show = dialogo_datos.show()

                    Glide.with(view!!)
                            .load(list.photo)
                            .thumbnail(Glide.with(view!!).load(R.raw.loading))
                            .apply(RequestOptions().circleCrop())
                            .apply(RequestOptions().override(200, 200))
                            .into(dialogo_datos_show.datos_driver_foto)

                    dialogo_datos_show.datos_driver.text = Etiquetas.get(position)
                    dialogo_datos_show.datos_driver_cerrar.setOnClickListener {
                        dialogo_datos_show.cancel()
                    }


                }
            }
        })


    }

}