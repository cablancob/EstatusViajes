package Controlador

class Tablas {
    abstract class Personas {
        companion object {
            val Id = "Id"
            val NOMBRE_TABLA = "EstatusViajes"
            val COLUMNA_FECHA = "Fecha"
            val COLUMNA_DESC = "Descripcion"
            val COLUMNA_HORA = "Hora"
            val COLUMNA_DEMAND = "Rider"
            val COLUMNA_SUPPLY = "Driver"
            val COLUMNA_ORIGIN = "Origen"
            val COLUMNA_DESTINO = "Destino"
            val COLUMNA_TKS = "Tks"
            val COLUMNA_SUPPLY_ACCEPT_TIME = "HoraDriverAcepto"
            val COLUMNA_CANCEL_REASON = "RazonCancelacion"
            val COLUMNA_USER_CANCEL = "UsuarioQueCancelo"
            val COLUMNA_SUPPLY_ARRIVE_TIME = "HoraDriverLlego"
            val COLUMNA_SUPPLY_ARRIVE_LOCATION = "UbicacionDriverLlego"
            val COLUMNA_SUPPLY_ACCEPT_LOCATION = "UbicacionDriverAcepto"
            val COLUMNA_SUPPLY_CANCEL_LOCATION = "UbicacionDriverCancelo"
            val COLUMNA_CANCEL_TIME = "HoraCancelacion"


        }
    }

    abstract class Usuarios {
        companion object {
            val NOMBRE_TABLA = "Usuarios"
            val COLUMNA_UID = "uid"
            val COLUMNA_NOMBRE = "nombre"
            val COLUMNA_EMAIL = "email"
            val COLUMNA_TELEFONO = "telefono"
        }
    }
}