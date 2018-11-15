package com.taksio.servicestatus

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import java.util.*

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        validar_permisos()


    }

    override fun onBackPressed() {
        if (this.supportFragmentManager.fragments.count() == 1) {
            finish()
        } else {
            supportFragmentManager.popBackStack()
        }
    }

    fun validar_permisos() {

        if (android.os.Build.VERSION.SDK_INT >= 23) {
            var permisos: ArrayList<String> = arrayListOf()


            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
                permisos.add(Manifest.permission.ACCESS_NETWORK_STATE)
            }

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permisos.add(Manifest.permission.ACCESS_FINE_LOCATION)
            }

            if (permisos.size > 0) {
                val arreglo = arrayOfNulls<String>(permisos.size)
                permisos.toArray(arreglo)
                this.requestPermissions(arreglo, 1)
            } else {
                programa()
            }


        } else {
            programa()
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        var boolean = true

        for (result in grantResults) {
            if (result != 0) {
                boolean = false
            }
        }

        if (boolean) {
            programa()
        } else {
            validar_permisos()
        }
    }

    fun programa() {
        supportFragmentManager
                .beginTransaction()
                .add(R.id.Frame, ConsultaViajes(), "VIAJES")
                .addToBackStack(null)
                .commit()
    }

}
