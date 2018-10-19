package com.taksio.servicestatus

import android.app.AlertDialog
import android.content.Context
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.layout_driver_datos.*
import okhttp3.*
import java.io.IOException

class Funciones {

    fun CargarDatosDriver(uuid: String, context: Context, Nombre: String, view: View) {

        var url = "http://driver.taksio.net/taksio/public/ws.php?uuid=${uuid}"

        val client = OkHttpClient()

        client.newCall(Request.Builder()
                .url(url)
                .build()).enqueue(object : Callback {

            override fun onFailure(call: Call?, e: IOException?) {
                println("ERROR: ${e!!.message}")
            }

            override fun onResponse(call: Call?, response: Response?) {
                (context as MainActivity).runOnUiThread {
                    var list = GsonBuilder().create().fromJson(response?.body()?.string(), FotoDriver::class.java)

                    val dialogo_datos = AlertDialog.Builder(context).setView(context.layoutInflater.inflate(R.layout.layout_driver_datos, null))

                    val dialogo_datos_show = dialogo_datos.show()

                    Glide.with(view)
                            .load(list.photo)
                            .thumbnail(Glide.with(view).load(R.raw.loading))
                            .apply(RequestOptions().circleCrop())
                            .apply(RequestOptions().override(200, 200))
                            .into(dialogo_datos_show.datos_driver_foto)

                    dialogo_datos_show.datos_driver.text = Nombre
                    dialogo_datos_show.datos_driver_cerrar.setOnClickListener {
                        dialogo_datos_show.cancel()
                    }


                }
            }
        })


    }
}