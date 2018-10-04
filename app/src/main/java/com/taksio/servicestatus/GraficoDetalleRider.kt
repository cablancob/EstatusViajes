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
import com.github.mikephil.charting.formatter.IValueFormatter
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import kotlinx.android.synthetic.main.fragment_grafico_detalle_rider.*




class GraficoDetalleRider : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_grafico_detalle_rider, container, false)
    }

    override fun onStart() {
        super.onStart()

        var letra = 14f

        var entries: MutableList<BarEntry> = mutableListOf()
        var entries2: MutableList<BarEntry> = mutableListOf()
        var Etiquetas = ArrayList<String>()

        var bd = ControladorBD(context!!)

        var contador = 0
        ControladorBD(context!!).GraficoTotalDetalleRider().forEach {
            entries.add(BarEntry(contador.toFloat(), it.completados.toFloat()))

            entries2.add(BarEntry(contador.toFloat(), it.noatendidos.toFloat()))

            Etiquetas.add(bd.UsuarioDatos(it.rider).name)

            contador += 1

        }


        var DataSet = BarDataSet(entries,getString(R.string.Completado))
        DataSet.valueTextSize = letra
        DataSet.color = ContextCompat.getColor(context!!, R.color.CompletadoColor)
        DataSet.valueTextColor = ContextCompat.getColor(view!!.context, R.color.GraficoTexto)
        DataSet.valueFormatter = IValueFormatter { value, _, _, _ -> value.toInt().toString() }
        var DataSet1 = BarDataSet(entries2, getString(R.string.NoAtendido))
        DataSet1.color = ContextCompat.getColor(context!!, R.color.NoAtendidoColor)
        DataSet1.valueFormatter = IValueFormatter { value, _, _, _ -> value.toInt().toString() }
        DataSet1.valueTextColor = ContextCompat.getColor(view!!.context, R.color.GraficoTexto)
        DataSet1.valueTextSize = letra

        var x = GraficoTDRider.xAxis
        x.granularity = 1f
        x.labelCount = 2
        x.textSize = letra
        x.textColor = ContextCompat.getColor(context!!, R.color.GraficoTexto)
        x.axisMinimum = 0f
        x.valueFormatter = IndexAxisValueFormatter(Etiquetas)
        x.setCenterAxisLabels(true)

        var data = BarData(DataSet, DataSet1)
        data.barWidth = 0.45f
        GraficoTDRider.data = data
        GraficoTDRider.description.text = "DESPLAZAR A LA DERECHA PARA VER MAS RIDERS"
        GraficoTDRider.description.textColor = ContextCompat.getColor(context!!, R.color.GraficoTexto)
        GraficoTDRider.description.textSize = letra - 2
        GraficoTDRider.legend.textSize = letra
        GraficoTDRider.legend.textColor = ContextCompat.getColor(context!!, R.color.GraficoTexto)
        GraficoTDRider.setPinchZoom(false)
        GraficoTDRider.groupBars(0f,0.06f,0.02f)
        GraficoTDRider.setVisibleXRangeMaximum(2f)
        GraficoTDRider.animateXY(2000,2000)

        GraficoTDRider.axisLeft.textColor = ContextCompat.getColor(context!!, R.color.GraficoTexto)
        GraficoTDRider.axisRight.textColor = ContextCompat.getColor(context!!, R.color.GraficoTexto)
        GraficoTDRider.setBackgroundColor(ContextCompat.getColor(context!!, R.color.FondoGrafico))

        GraficoTDRider.extraTopOffset = 10f
        GraficoTDRider.invalidate()





    }


}
