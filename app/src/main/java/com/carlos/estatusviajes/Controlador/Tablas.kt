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
            val COLUMNA_DEMAND_TEL = "RiderTel"
            val COLUMNA_DEMAND_EMAIL = "RiderEmail"
            val COLUMNA_SUPPLY = "Driver"
            val COLUMNA_SUPPLY_TEL = "DriverTel"
            val COLUMNA_ORIGIN = "Origen"
            val COLUMNA_DESTINO = "Destino"
            val COLUMNA_TKS = "Tks"
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