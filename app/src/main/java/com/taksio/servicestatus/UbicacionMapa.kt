package com.taksio.servicestatus

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class UbicacionMapa : Fragment(), OnMapReadyCallback {
    override fun onMapReady(p0: GoogleMap?) {

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        var datos = arguments?.getString("cordenadas")
        val rootView = inflater.inflate(R.layout.fragment_ubicacion_mapa, container, false)

        val mMapView = rootView.findViewById(R.id.mapView) as MapView
        mMapView.onCreate(savedInstanceState)

        mMapView.onResume() // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(activity!!.applicationContext)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        mMapView.getMapAsync(object : OnMapReadyCallback {
            override fun onMapReady(p0: GoogleMap?) {
                val googleMap = p0

                googleMap!!.isMyLocationEnabled
                val latitud = datos!!.split("/")[0].toDouble()
                val longitud = datos.split("/")[1].toDouble()
                val cordenadas = LatLng(latitud, longitud)
                googleMap.addMarker(MarkerOptions().position(cordenadas))
                val cameraPosition = CameraPosition.Builder().target(cordenadas).zoom(16f).build()
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

            }

        })
        return rootView
    }

}
