package com.taksio.servicestatus

import Controlador.ControladorBD
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.formatter.IValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import kotlinx.android.synthetic.main.fragment_grafico_detalle.*


class GraficoDetalle : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_grafico_detalle,container,false)
    }


    override fun onStart() {
        super.onStart()

        var bd = ControladorBD(context!!)
        var letra = 14f
        var linea = 2f
        var radio = linea * 2.5f

        var ArrayString = ArrayList<String>()

        var datosCompletados = bd.GraficoTotalDetalleCompletado()
        var datosNoAtendido = bd.GraficoTotalDetalleCNoAtendido()
        var datosCancelados = bd.GraficoTotalDetalleCCancelado()

        var entradaCompletados: MutableList<Entry> = mutableListOf()
        var entradaNoAtendido: MutableList<Entry> = mutableListOf()
        var entradaCancelado: MutableList<Entry> = mutableListOf()


        val colores = arrayOf(ContextCompat.getColor(view!!.context, R.color.NoAtendidoColor), ContextCompat.getColor(view!!.context, R.color.CompletadoColor), ContextCompat.getColor(view!!.context, R.color.colorAccent), ContextCompat.getColor(view!!.context, R.color.GraficoTexto), ContextCompat.getColor(view!!.context, R.color.CanceladosColor))


        datosCompletados.forEach {
               ArrayString.add(it.fecha)
        }




        for (i in 0 until datosCompletados.count()) {
            var entry_datos = Entry(i.toFloat(),datosCompletados.get(i).y.toFloat())
            entradaCompletados.add(entry_datos)

            entry_datos = Entry(i.toFloat(),datosNoAtendido.get(i).y.toFloat())
            entradaNoAtendido.add(entry_datos)

            entry_datos = Entry(i.toFloat(), datosCancelados.get(i).y.toFloat())
            entradaCancelado.add(entry_datos)
        }

        val setNoAtendidos = LineDataSet(entradaNoAtendido, getString(R.string.NoAtendido))
        setNoAtendidos.axisDependency = YAxis.AxisDependency.LEFT
        setNoAtendidos.valueTextSize = letra + 1f
        setNoAtendidos.valueTextColor = colores[3]
        setNoAtendidos.color = colores[0]
        setNoAtendidos.setCircleColor(colores[0])
        setNoAtendidos.circleRadius = radio
        setNoAtendidos.lineWidth = radio
        setNoAtendidos.valueFormatter = IValueFormatter { value, _, _, _ -> value.toInt().toString() }

        val setCompletados = LineDataSet(entradaCompletados, getString(R.string.Completado))
        setCompletados.axisDependency = YAxis.AxisDependency.LEFT
        setCompletados.valueTextSize = letra + 1f
        setCompletados.valueTextColor = colores[3]
        setCompletados.color = colores[1]
        setCompletados.setCircleColor(colores[1])
        setCompletados.circleRadius = radio
        setCompletados.lineWidth = radio
        setCompletados.valueFormatter = IValueFormatter { value, _, _, _ -> value.toInt().toString() }

        val setCancelados = LineDataSet(entradaCancelado, getString(R.string.Cancelados))
        setCancelados.axisDependency = YAxis.AxisDependency.LEFT
        setCancelados.valueTextSize = letra + 1f
        setCancelados.valueTextColor = colores[3]
        setCancelados.color = colores[4]
        setCancelados.setCircleColor(colores[4])
        setCancelados.circleRadius = radio
        setCancelados.lineWidth = radio
        setCancelados.valueFormatter = IValueFormatter { value, _, _, _ -> value.toInt().toString() }

        val dataSets = ArrayList<ILineDataSet>()

        dataSets.add(setNoAtendidos)
        dataSets.add(setCompletados)
        dataSets.add(setCancelados)

        var x = GraficoD.xAxis
        x.valueFormatter = IAxisValueFormatter { value, _ -> ArrayString.get(value.toInt()) }
        x.labelCount = 3
        x.textSize = letra - 2
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
        GraficoD.extraTopOffset = 10f
        GraficoD.extraLeftOffset = 10f
        GraficoD.extraRightOffset = 10f
        GraficoD.invalidate()




    }
}
