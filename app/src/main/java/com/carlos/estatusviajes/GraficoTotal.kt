package com.carlos.estatusviajes


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
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import kotlinx.android.synthetic.main.fragment_grafico_total.*
import kotlinx.android.synthetic.main.fragment_grafico_total.view.*


class GraficoTotal : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_grafico_total, container, false)
    }

    override fun onStart() {
        super.onStart()

        var bd = ControladorBD(context!!)
        var letra = 14f

        var entries: MutableList<BarEntry>
        val colores = arrayOf(ContextCompat.getColor(view!!.context, R.color.NoAtendidoColor), ContextCompat.getColor(view!!.context, R.color.CompletadoColor))
        var data_final: MutableList<IBarDataSet> = mutableListOf()


        var contador = 0
        bd!!.GraficoTotalF().forEach {
            entries = mutableListOf()
            entries.add(BarEntry(contador.toFloat(), it.y.toFloat()))
            if (it.desc == "COMPLETADO") {
                var DataSet = BarDataSet(entries, it.desc)
                DataSet.valueTextSize = letra
                DataSet.valueTextColor = ContextCompat.getColor(view!!.context, R.color.GraficoTexto)
                DataSet.color = colores[1]
                DataSet.valueFormatter = IValueFormatter { value, entry, dataSetIndex, viewPortHandler -> value.toInt().toString() }
                data_final.add(DataSet)
            } else {
                var DataSet = BarDataSet(entries, it.desc)
                DataSet.valueTextSize = letra
                DataSet.valueTextColor = ContextCompat.getColor(view!!.context, R.color.GraficoTexto)
                DataSet.color = colores[0]
                DataSet.valueFormatter = IValueFormatter { value, entry, dataSetIndex, viewPortHandler -> value.toInt().toString() }
                data_final.add(DataSet)
            }
            contador += 1
        }

        var x = GraficoT.xAxis
        x.labelCount = data_final.count()
        x.textColor = ContextCompat.getColor(view!!.context, R.color.GraficoTexto)
        x.textSize = letra
        x.isEnabled = false

        GraficoT.axisRight.setEnabled(false)

        var y = GraficoT.axisLeft
        y.granularity = 20f

        GraficoT.data = BarData(data_final)
        GraficoT.description.isEnabled = false
        GraficoT.legend.textSize = letra
        GraficoT.legend.textColor = ContextCompat.getColor(view!!.context, R.color.GraficoTexto)
        GraficoT.animateY(2000)
        GraficoT.setPinchZoom(false)
        GraficoT.setTouchEnabled(false)

        GraficoT.axisRight.textColor = ContextCompat.getColor(view!!.context, R.color.GraficoTexto)
        GraficoT.axisLeft.textColor = ContextCompat.getColor(view!!.context, R.color.GraficoTexto)

        GraficoT.setBackgroundColor(ContextCompat.getColor(view!!.context, R.color.FondoGrafico))
        GraficoT.invalidate()
    }
}
