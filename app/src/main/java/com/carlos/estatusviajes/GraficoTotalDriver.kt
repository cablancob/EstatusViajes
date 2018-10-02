package com.carlos.estatusviajes


import Controlador.ControladorBD
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.formatter.IValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.renderer.XAxisRenderer
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.Transformer
import com.github.mikephil.charting.utils.ViewPortHandler
import kotlinx.android.synthetic.main.fragment_grafico_total_driver.*
import kotlinx.android.synthetic.main.fragment_grafico_total_driver.view.*
import kotlin.math.max


class GraficoTotalDriver : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_grafico_total_driver, container, false)
    }

    override fun onStart() {
        super.onStart()

        val letra = 14f

        var bd = ControladorBD(context!!)
        var entries: MutableList<BarEntry>
        var data_final: MutableList<IBarDataSet> = mutableListOf()
        var Etiquetas = ArrayList<String>()

        var maximo = 0F


        var contador = 0
        ControladorBD(context!!).GraficoTotalTD().forEach {
            entries = mutableListOf()
            entries.add(BarEntry(contador.toFloat(), it.y.toFloat()))

            if (it.y.toFloat() > maximo) {
                maximo = it.y.toFloat()
            }

            var DataSet = BarDataSet(entries, it.driver)
            DataSet.valueTextSize = letra
            DataSet.valueTextColor = ContextCompat.getColor(view!!.context, R.color.GraficoTexto)
            DataSet.color = ContextCompat.getColor(view!!.context, R.color.CompletadoColor)
            DataSet.valueFormatter = IValueFormatter { value, entry, dataSetIndex, viewPortHandler ->  value.toInt().toString()}
            DataSet.setDrawValues(false)
            data_final.add(DataSet)
            Etiquetas.add(bd.UsuarioDatos(it.driver).name)
            contador += 1
        }

        var x = GraficoTD.xAxis
        x.labelRotationAngle = -90f
        x.position = XAxis.XAxisPosition.BOTTOM_INSIDE
        x.labelCount = 3
        x.textSize = letra
        x.valueFormatter = IAxisValueFormatter { value, axis -> Etiquetas.get(value.toInt()) }
        x.setDrawGridLines(false)



        GraficoTD.data = BarData(data_final)
        GraficoTD.setXAxisRenderer(CustomXAxisRenderer(GraficoTD.viewPortHandler, GraficoTD.xAxis, GraficoTD.getTransformer(YAxis.AxisDependency.RIGHT), view!!.context))
        GraficoTD.legend.isEnabled = false
        GraficoTD.description.text = ""
        GraficoTD.setVisibleXRangeMaximum(3.5f)
        GraficoTD.setPinchZoom(false)
        GraficoTD.animateY(2000)

        GraficoTD.axisLeft.axisMinimum = 0f
        GraficoTD.axisLeft.gridLineWidth = 0.7f
        GraficoTD.axisLeft.labelCount = maximo.toInt()
        GraficoTD.axisLeft.granularity = 1f
        GraficoTD.axisLeft.textColor = ContextCompat.getColor(view!!.context, R.color.GraficoTexto)
        GraficoTD.axisRight.gridLineWidth = 0.7f
        GraficoTD.axisRight.labelCount = maximo.toInt()
        GraficoTD.axisRight.granularity = 1f
        GraficoTD.axisRight.textColor = ContextCompat.getColor(view!!.context, R.color.GraficoTexto)
        GraficoTD.axisRight.axisMinimum = 0f

        GraficoTD.setBackgroundColor(ContextCompat.getColor(view!!.context, R.color.FondoGrafico))

        GraficoTD.invalidate()

    }

}

private class CustomXAxisRenderer(viewPortHandler: ViewPortHandler, xAxis: XAxis, trans: Transformer, context: Context) : XAxisRenderer(viewPortHandler, xAxis, trans) {

    var contexto = context

    override fun renderAxisLabels(c: Canvas?) {

        val pointF = MPPointF.getInstance(0f, 0f)

        mAxisLabelPaint.typeface = mXAxis.typeface
        mAxisLabelPaint.textSize = 52f
        mAxisLabelPaint.color = ContextCompat.getColor(contexto, R.color.GraficoTexto)

        pointF.x = 0.5f
        pointF.y = 1.0f
        drawLabels(c, mViewPortHandler.contentBottom() - 24f, pointF)

        MPPointF.recycleInstance(pointF)
    }
}
