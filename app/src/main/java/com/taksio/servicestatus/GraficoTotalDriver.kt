package com.taksio.servicestatus


import Controlador.ControladorBD
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.IValueFormatter
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import kotlinx.android.synthetic.main.fragment_grafico_total_driver.*


class GraficoTotalDriver : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_grafico_total_driver, container, false)
    }

    override fun onStart() {
        super.onStart()

        var letra = 14f

        var entries: MutableList<BarEntry> = mutableListOf()
        var entries2: MutableList<BarEntry> = mutableListOf()
        var Etiquetas = ArrayList<String>()

        var bd = ControladorBD(context!!)

        var contador = 0

        var datos_bd = bd.GraficoTotalDriverDatos()
        datos_bd.forEach {
            if (it.driver.split(":").get(1) != "-") {
                entries.add(BarEntry(contador.toFloat(), it.completados.toFloat()))

                entries2.add(BarEntry(contador.toFloat(), it.cancelados.toFloat()))

                Etiquetas.add(bd.UsuarioDatos(it.driver.split(":").get(1)).name)

                contador += 1
            }
        }

        var DataSet = BarDataSet(entries, getString(R.string.Completado))
        DataSet.valueTextSize = letra
        DataSet.color = ContextCompat.getColor(context!!, R.color.CompletadoColor)
        DataSet.valueTextColor = ContextCompat.getColor(view!!.context, R.color.GraficoTexto)
        DataSet.valueFormatter = IValueFormatter { value, _, _, _ -> value.toInt().toString() }

        var DataSet2 = BarDataSet(entries2, getString(R.string.Cancelados))
        DataSet2.color = ContextCompat.getColor(context!!, R.color.CanceladosColor)
        DataSet2.valueFormatter = IValueFormatter { value, _, _, _ -> value.toInt().toString() }
        DataSet2.valueTextColor = ContextCompat.getColor(view!!.context, R.color.GraficoTexto)
        DataSet2.valueTextSize = letra

        var x = BarChartTotalDriver.xAxis
        x.granularity = 1f
        x.labelCount = 2
        x.textSize = letra
        x.textColor = ContextCompat.getColor(context!!, R.color.GraficoTexto)
        x.axisMinimum = 0f
        x.axisMaximum = Etiquetas.size.toFloat()
        x.valueFormatter = IndexAxisValueFormatter(Etiquetas)
        x.setCenterAxisLabels(true)

        var data = BarData(DataSet, DataSet2)

        data.barWidth = 0.46f
        BarChartTotalDriver.data = data
        BarChartTotalDriver.description.text = "DESPLAZAR A LA DERECHA PARA VER MAS DRIVER"
        BarChartTotalDriver.description.textColor = ContextCompat.getColor(context!!, R.color.GraficoTexto)
        BarChartTotalDriver.description.textSize = letra - 2
        BarChartTotalDriver.legend.textSize = letra
        BarChartTotalDriver.legend.textColor = ContextCompat.getColor(context!!, R.color.GraficoTexto)
        BarChartTotalDriver.setPinchZoom(false)
        BarChartTotalDriver.groupBars(0f, 0.04f, 0.02f)
        BarChartTotalDriver.setVisibleXRangeMaximum(2f)
        BarChartTotalDriver.animateXY(2000, 2000)

        BarChartTotalDriver.axisLeft.textColor = ContextCompat.getColor(context!!, R.color.GraficoTexto)
        BarChartTotalDriver.axisRight.textColor = ContextCompat.getColor(context!!, R.color.GraficoTexto)
        BarChartTotalDriver.setBackgroundColor(ContextCompat.getColor(context!!, R.color.FondoGrafico))

        BarChartTotalDriver.extraTopOffset = 10f

        BarChartTotalDriver.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onNothingSelected() {

            }

            override fun onValueSelected(e: Entry?, h: Highlight?) {

                Funciones().CargarDatosDriver(datos_bd.get(h!!.x.toString().split(".").get(0).toInt()).driver.split(":").get(1), context!!, Etiquetas.get(h.x.toString().split(".").get(0).toInt()), view!!)
            }

        })

        BarChartTotalDriver.invalidate()

    }

}
