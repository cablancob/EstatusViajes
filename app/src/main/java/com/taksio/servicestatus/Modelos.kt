package com.taksio.servicestatus

import java.io.Serializable


class Viajes(val desc: String, val request_time: String, val demand: String, val supply: String, val hora: String, val features: features, val billing : billing)

class features(val origin: String?=null, val destination: String?=null)

class billing(val fare : fare)

class fare(val amount : String?="0")


class ViajesContados(val request_time: String, val completados: String, val noatendidos: String)


class ViajeDetalle(val rider: String, val estatus: String, val count: Int, val fecha: String) : Serializable

class ViajeRiderDetalle(val supply: String, val hora: String, val origen: String, val destino: String, val tks: String)

class DatosUsuario(val name: String, val email: String, val phone: String)

class DatosRider(val name: String, val email: String, val phone: String)

class DatosDriver(val name1: String, val lastname1: String, val phone: String)

class Datos(val uid: String)

class GraficoTotalO(val desc: String, val y: String)

class GraficoTotalD(val fecha: String, val desc: String, val y: String)

class GraficoTotalR(val rider: String, val completados: String, val noatendidos: String)

class GraficoTD(val driver: String, val y: String)

class GraficoTDR(val rider: String, val desc: String, val y: String)



