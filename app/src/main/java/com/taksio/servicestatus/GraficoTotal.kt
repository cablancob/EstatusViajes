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
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import kotlinx.android.synthetic.main.fragment_grafico_total.*


class GraficoTotal : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_grafico_total, container, false)
    }

    override fun onStart() {
        super.onStart()

        val bd = ControladorBD(context!!)
        val letra = 14f

        var entries: MutableList<BarEntry>
        val colores = arrayOf(ContextCompat.getColor(view!!.context, R.color.NoAtendidoColor), ContextCompat.getColor(view!!.context, R.color.CompletadoColor))
        val data_final: MutableList<IBarDataSet> = mutableListOf()


        var contador = 0
        bd.GraficoTotalF().forEach {
            entries = mutableListOf()
            entries.add(BarEntry(contador.toFloat(), it.y.toFloat()))
            if (it.desc == "COMPLETADO") {
                val DataSet = BarDataSet(entries, it.desc)
                DataSet.valueTextSize = letra
                DataSet.valueTextColor = ContextCompat.getColor(view!!.context, R.color.GraficoTexto)
                DataSet.color = colores[1]
                DataSet.valueFormatter = IValueFormatter { value, _, _, _ -> value.toInt().toString() }
                data_final.add(DataSet)
            } else {
                val DataSet = BarDataSet(entries, it.desc)
                DataSet.valueTextSize = letra
                DataSet.valueTextColor = ContextCompat.getColor(view!!.context, R.color.GraficoTexto)
                DataSet.color = colores[0]
                DataSet.valueFormatter = IValueFormatter { value, _, _, _ -> value.toInt().toString() }
                data_final.add(DataSet)
            }
            contador += 1
        }

        val x = GraficoT.xAxis
        x.labelCount = data_final.count()
        x.textColor = ContextCompat.getColor(view!!.context, R.color.GraficoTexto)
        x.textSize = letra
        x.isEnabled = false


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

        GraficoT.axisLeft.axisMinimum = 0f
        GraficoT.axisRight.axisMinimum = 0f

        GraficoT.invalidate()
    }
}
