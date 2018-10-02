package com.carlos.estatusviajes

import Controlador.ControladorBD
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.mikephil.charting.components.AxisBase
import kotlinx.android.synthetic.main.fragment_grafico_detalle.*
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.formatter.IValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import kotlinx.android.synthetic.main.fragment_grafico_detalle.view.*
import kotlinx.android.synthetic.main.fragment_grafico_total.*


class GraficoDetalle : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_grafico_detalle,container,false)
    }


    override fun onStart() {
        super.onStart()

        var bd = ControladorBD(context!!)
        var letra = 14f
        var linea = 2f
        var radio = linea * 1.5f

        var ArrayString = ArrayList<String>()

        var datosCompletados = bd!!.GraficoTotalDetalleCompletado()
        var datosNoAtendido = bd!!.GraficoTotalDetalleCNoAtendido()

        var entradaCompletados: MutableList<Entry> = mutableListOf()
        var entradaNoAtendido: MutableList<Entry> = mutableListOf()


        val colores = arrayOf(ContextCompat.getColor(view!!.context, R.color.NoAtendidoColor), ContextCompat.getColor(view!!.context, R.color.CompletadoColor), ContextCompat.getColor(view!!.context, R.color.colorAccent), ContextCompat.getColor(view!!.context, R.color.GraficoTexto))


        datosCompletados.forEach {
               ArrayString.add(it.fecha)
        }




        for (i in 0 until datosCompletados.count()) {
            var entry_datos = Entry(i.toFloat(),datosCompletados.get(i).y.toFloat())
            entradaCompletados.add(entry_datos)

            entry_datos = Entry(i.toFloat(),datosNoAtendido.get(i).y.toFloat())
            entradaNoAtendido.add(entry_datos)
        }

        val setNoAtendidos = LineDataSet(entradaNoAtendido, getString(R.string.NoAtendido))
        setNoAtendidos.axisDependency = YAxis.AxisDependency.LEFT
        setNoAtendidos.valueTextSize = letra + 1f
        setNoAtendidos.valueTextColor = colores[3]
        setNoAtendidos.color = colores[0]
        setNoAtendidos.setCircleColor(colores[2])
        setNoAtendidos.circleRadius = radio
        setNoAtendidos.lineWidth = radio
        setNoAtendidos.valueFormatter = IValueFormatter { value, entry, dataSetIndex, viewPortHandler -> value.toInt().toString() }

        val setCompletados = LineDataSet(entradaCompletados, getString(R.string.Completado))
        setCompletados.axisDependency = YAxis.AxisDependency.LEFT
        setCompletados.valueTextSize = letra + 1f
        setCompletados.valueTextColor = colores[3]
        setCompletados.color = colores[1]
        setCompletados.setCircleColor(colores[2])
        setCompletados.circleRadius = radio
        setCompletados.lineWidth = radio
        setCompletados.valueFormatter = IValueFormatter { value, entry, dataSetIndex, viewPortHandler -> value.toInt().toString() }

        val dataSets = ArrayList<ILineDataSet>()

        dataSets.add(setNoAtendidos)
        dataSets.add(setCompletados)

        var x = GraficoD.xAxis
        x.valueFormatter = IAxisValueFormatter { value, axis -> ArrayString.get(value.toInt()) }
        x.labelRotationAngle = -45f
        x.labelCount = 3
        x.textColor = ContextCompat.getColor(view!!.context, R.color.GraficoTexto)





        GraficoD.data = LineData(dataSets)
        GraficoD.legend.textSize = letra
        GraficoD.legend.textColor = ContextCompat.getColor(view!!.context, R.color.GraficoTexto)
        GraficoD.description.text = ""
        GraficoD.setVisibleXRangeMaximum(3.5f)
        GraficoD.animateY(1500)
        GraficoD.axisLeft.textColor = ContextCompat.getColor(view!!.context, R.color.GraficoTexto)
        GraficoD.axisRight.textColor = ContextCompat.getColor(view!!.context, R.color.GraficoTexto)
        GraficoD.setPinchZoom(false)

        GraficoD.setBackgroundColor(ContextCompat.getColor(view!!.context, R.color.FondoGrafico))
        GraficoD.invalidate()




    }
}
