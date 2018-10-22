package com.taksio.servicestatus

import java.io.Serializable


class Viajes(val desc: String, val request_time: String, val demand: String, val supply: String, val hora: String, val features: features, val billing: billing, val supply_accept_time: String?, val cancel_reason: String?, val user_cancel: String?, val supply_arrive_time: String?, val supply_arrive_location: String?, val supply_accept_location: String?, val supply_cancel_location: String?, val cancel_time: String?)

class features(val origin: String?=null, val destination: String?=null)

class billing(val fare : fare)

class fare(val amount : String?="0")


class ViajesContados(val request_time: String, val completados: String, val noatendidos: String, val cancelados: String)

class ViajeDetalle(val rider: String, val estatus: String, val count: Int, val fecha: String) : Serializable

class ViajeRiderDetalle(val supply: String, val hora: String, val origen: String?, val destino: String?, val tks: String?, val supply_accept_time: String?, val cancel_reason: String?, val user_cancel: String?, val supply_arrive_time: String?, val supply_arrive_location: String?, val supply_accept_location: String?, val supply_cancel_location: String?, val cancel_time: String?, val estatus: String)

class DatosUsuario(val name: String, val email: String, val phone: String)

class DatosRider(val name: String, val email: String, val phone: String)

class DatosDriver(val name1: String, val lastname1: String, val phone: String)

class Datos(val uid: String)

class GraficoTotalO(val desc: String, val y: String)

class GraficoTotalDriverDatos(val driver: String, val completados: String, val cancelados: String)

class GraficoTotalD(val fecha: String, val desc: String, val y: String)

class GraficoTotalR(val rider: String, val completados: String, val noatendidos: String, val cancelados: String)

class FotoDriver(val photo: String)



