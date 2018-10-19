package com.taksio.servicestatus

import Controlador.ControladorBD
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.IValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import kotlinx.android.synthetic.main.fragment_grafico_total_driver_cancelados.*

class GraficoTotalDriverCancelados : Fragment() {

    var datos_bd: MutableList<GraficoTD> = mutableListOf()
    val Etiquetas = ArrayList<String>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_grafico_total_driver_cancelados, null)
    }

    override fun onStart() {
        super.onStart()

        val letra = 14f

        val bd = ControladorBD(context!!)
        var entries: MutableList<BarEntry>
        val data_final: MutableList<IBarDataSet> = mutableListOf()


        var maximo = 0F


        var contador = 0
        datos_bd = ControladorBD(context!!).GraficoTotalTDC()

        datos_bd.forEach {
            entries = mutableListOf()
            entries.add(BarEntry(contador.toFloat(), it.y.toFloat()))

            if (it.y.toFloat() > maximo) {
                maximo = it.y.toFloat()
            }

            val DataSet = BarDataSet(entries, it.driver.split(":").get(1))
            DataSet.valueTextSize = letra
            DataSet.valueTextColor = ContextCompat.getColor(view!!.context, R.color.GraficoTexto)
            DataSet.color = ContextCompat.getColor(view!!.context, R.color.CanceladosColor)
            DataSet.valueFormatter = IValueFormatter { value, _, _, _ -> value.toInt().toString() }
            DataSet.setDrawValues(true)
            data_final.add(DataSet)
            Etiquetas.add(bd.UsuarioDatos(it.driver.split(":").get(1)).name)
            contador += 1
        }

        val x = GraficoTDC.xAxis

        x.position = XAxis.XAxisPosition.BOTTOM
        x.labelCount = 3
        x.textSize = letra
        x.textColor = ContextCompat.getColor(view!!.context, R.color.GraficoTexto)
        x.isEnabled = false
        x.setDrawGridLines(false)


        GraficoTDC.data = BarData(data_final)
        GraficoTDC.legend.isEnabled = false
        GraficoTDC.description.text = getString(R.string.PRESIONAR_BARRA)
        GraficoTDC.description.textSize = letra - 2
        GraficoTDC.setVisibleXRangeMaximum(3.5f)
        GraficoTDC.animateY(2000)

        GraficoTDC.axisLeft.axisMinimum = -0.15f
        GraficoTDC.axisLeft.granularity = 1f
        GraficoTDC.axisLeft.textColor = ContextCompat.getColor(view!!.context, R.color.GraficoTexto)
        GraficoTDC.axisRight.textColor = ContextCompat.getColor(view!!.context, R.color.GraficoTexto)
        GraficoTDC.axisRight.axisMinimum = -0.15f
        GraficoTDC.axisRight.granularity = 1f


        GraficoTDC.extraBottomOffset = 24F

        GraficoTDC.setBackgroundColor(ContextCompat.getColor(view!!.context, R.color.FondoGrafico))

        GraficoTDC.setScaleEnabled(false)

        GraficoTDC.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onNothingSelected() {

            }

            override fun onValueSelected(e: Entry?, h: Highlight?) {
                Funciones().CargarDatosDriver(datos_bd.get(h!!.dataSetIndex).driver.split(":").get(1), context!!, Etiquetas.get(h.dataSetIndex), view!!)
            }

        })

        GraficoTDC.invalidate()

    }


}
