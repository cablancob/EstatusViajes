package Controlador

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.google.gson.GsonBuilder
import com.taksio.servicestatus.*
import okhttp3.*
import java.io.IOException

class ControladorBD(context: Context) : SQLiteOpenHelper(context, NOMBRE_BD, null, VERSION_BD) {


    private val bd: SQLiteDatabase
    private var valores: ContentValues

    companion object {
        private val NOMBRE_BD = "CARLOS"
        private val VERSION_BD = 1
    }

    init {
        bd = this.writableDatabase
        valores = ContentValues()
    }

    override fun onCreate(db: SQLiteDatabase?) {

    }

    fun Insert(viajes: Viajes) {
        valores = ContentValues()
        valores.put(Tablas.Personas.COLUMNA_FECHA, viajes.request_time)
        valores.put(Tablas.Personas.COLUMNA_DESC, viajes.desc)
        valores.put(Tablas.Personas.COLUMNA_HORA, viajes.hora)
        valores.put(Tablas.Personas.COLUMNA_DEMAND, viajes.demand)
        valores.put(Tablas.Personas.COLUMNA_SUPPLY, viajes.supply)
        valores.put(Tablas.Personas.COLUMNA_ORIGIN, viajes.features.origin)
        valores.put(Tablas.Personas.COLUMNA_DESTINO, viajes.features.destination)
        valores.put(Tablas.Personas.COLUMNA_TKS, viajes.billing.fare.amount)
        valores.put(Tablas.Personas.COLUMNA_SUPPLY_ACCEPT_TIME, viajes.supply_accept_time)
        valores.put(Tablas.Personas.COLUMNA_CANCEL_REASON, viajes.cancel_reason)
        valores.put(Tablas.Personas.COLUMNA_USER_CANCEL, viajes.user_cancel)
        valores.put(Tablas.Personas.COLUMNA_SUPPLY_ARRIVE_TIME, viajes.supply_arrive_time)
        valores.put(Tablas.Personas.COLUMNA_SUPPLY_ARRIVE_LOCATION, viajes.supply_arrive_location)
        valores.put(Tablas.Personas.COLUMNA_SUPPLY_ACCEPT_LOCATION, viajes.supply_accept_location)
        valores.put(Tablas.Personas.COLUMNA_SUPPLY_CANCEL_LOCATION, viajes.supply_cancel_location)
        valores.put(Tablas.Personas.COLUMNA_CANCEL_TIME, viajes.cancel_time)
        bd.insert(Tablas.Personas.NOMBRE_TABLA, null, valores)
    }


    fun Drop() {
        bd.execSQL("DROP TABLE IF EXISTS ${Tablas.Personas.NOMBRE_TABLA}")


        //bd!!.execSQL("DROP TABLE IF EXISTS ${Tablas.Usuarios.NOMBRE_TABLA}")
    }

    fun Create() {
        bd.execSQL("CREATE TABLE ${Tablas.Personas.NOMBRE_TABLA} " +
                "(" +
                "${Tablas.Personas.COLUMNA_FECHA} TEXT NULL," +
                "${Tablas.Personas.COLUMNA_DESC} TEXT NULL DEFAULT '-'," +
                "${Tablas.Personas.COLUMNA_HORA} TEXT NULL DEFAULT '-'," +
                "${Tablas.Personas.COLUMNA_DEMAND} TEXT NULL DEFAULT '-'," +
                "${Tablas.Personas.COLUMNA_SUPPLY} TEXT NULL DEFAULT '-'," +
                "${Tablas.Personas.COLUMNA_ORIGIN} TEXT NULL DEFAULT '-'," +
                "${Tablas.Personas.COLUMNA_DESTINO} TEXT NULL DEFAULT '-'," +
                "${Tablas.Personas.COLUMNA_TKS} TEXT NULL DEFAULT '0'," +
                "${Tablas.Personas.COLUMNA_SUPPLY_ACCEPT_TIME} TEXT NULL," +
                "${Tablas.Personas.COLUMNA_CANCEL_REASON} TEXT NULL," +
                "${Tablas.Personas.COLUMNA_USER_CANCEL} TEXT NULL," +
                "${Tablas.Personas.COLUMNA_SUPPLY_ARRIVE_TIME} TEXT NULL," +
                "${Tablas.Personas.COLUMNA_SUPPLY_ARRIVE_LOCATION} TEXT NULL," +
                "${Tablas.Personas.COLUMNA_SUPPLY_ACCEPT_LOCATION} TEXT NULL," +
                "${Tablas.Personas.COLUMNA_SUPPLY_CANCEL_LOCATION} TEXT NULL," +
                "${Tablas.Personas.COLUMNA_CANCEL_TIME} TEXT NULL" +
                ")")

        bd.execSQL("CREATE TABLE IF NOT EXISTS ${Tablas.Usuarios.NOMBRE_TABLA} " +
                "(" +
                "${Tablas.Usuarios.COLUMNA_UID} TEXT NOT NULL , " +
                "${Tablas.Usuarios.COLUMNA_NOMBRE} TEXT NOT NULL DEFAULT '-', " +
                "${Tablas.Usuarios.COLUMNA_TELEFONO} TEXT NOT NULL DEFAULT '-', " +
                "${Tablas.Usuarios.COLUMNA_EMAIL} TEXT NOT NULL DEFAULT '-'" +
                ")")

    }


    fun Select(): MutableList<ViajesContados> {
        val lista: MutableList<ViajesContados> = mutableListOf()
        val cursor: Cursor
        cursor = bd.rawQuery(
                "SELECT " +
                        "A.${Tablas.Personas.COLUMNA_FECHA}, " +
                        "IFNULL(B.C,'0'), " +
                        "IFNULL(C.N,'0'), " +
                        "IFNULL(D.CA, '0') " +
                        "FROM " +
                        "(SELECT ${Tablas.Personas.COLUMNA_FECHA} " +
                        "FROM " +
                        "${Tablas.Personas.NOMBRE_TABLA} " +
                        "GROUP BY ${Tablas.Personas.COLUMNA_FECHA}) A " +
                        "LEFT OUTER JOIN " +
                        "(SELECT ${Tablas.Personas.COLUMNA_FECHA}, COUNT(*) AS C " +
                        "FROM ${Tablas.Personas.NOMBRE_TABLA} " +
                        "WHERE ${Tablas.Personas.COLUMNA_DESC} == 'TRIP_ENDED' " +
                        "GROUP BY ${Tablas.Personas.COLUMNA_FECHA}) B " +
                        "ON A.${Tablas.Personas.COLUMNA_FECHA} == B.${Tablas.Personas.COLUMNA_FECHA} " +
                        "LEFT OUTER JOIN " +
                        "(SELECT ${Tablas.Personas.COLUMNA_FECHA}, COUNT(*) AS N " +
                        "FROM ${Tablas.Personas.NOMBRE_TABLA} " +
                        "WHERE ${Tablas.Personas.COLUMNA_DESC} == 'REQUEST_TIMEOUT' " +
                        "GROUP BY ${Tablas.Personas.COLUMNA_FECHA}) C " +
                        "ON A.${Tablas.Personas.COLUMNA_FECHA} == C.${Tablas.Personas.COLUMNA_FECHA} " +
                        "LEFT OUTER JOIN " +
                        "(SELECT ${Tablas.Personas.COLUMNA_FECHA}, COUNT(*) AS CA " +
                        "FROM ${Tablas.Personas.NOMBRE_TABLA} " +
                        "WHERE ${Tablas.Personas.COLUMNA_DESC} == 'TRIP_CANCELLED' " +
                        "GROUP BY ${Tablas.Personas.COLUMNA_FECHA}) D " +
                        "ON A.${Tablas.Personas.COLUMNA_FECHA} == D.${Tablas.Personas.COLUMNA_FECHA} " +
                        "ORDER BY A.${Tablas.Personas.COLUMNA_FECHA} DESC", null)
        if (cursor != null) {
            if (cursor.count > 0) {
                cursor.moveToFirst()
                do {
                    lista.add(ViajesContados(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3)))
                } while (cursor.moveToNext())
            }
        }

        return lista
    }


    fun ConsultaViaje(fecha: String): MutableList<ViajeDetalle> {
        val lista = ArrayList<ViajeDetalle>()
        val cursor: Cursor
        cursor = bd.rawQuery(
                "SELECT " +
                        "${Tablas.Personas.COLUMNA_DEMAND},  " +
                        "${Tablas.Personas.COLUMNA_DESC},  " +
                        "COUNT(*), " +
                        "${Tablas.Personas.COLUMNA_FECHA} " +
                        "FROM ${Tablas.Personas.NOMBRE_TABLA} " +
                        "WHERE ${Tablas.Personas.COLUMNA_FECHA} == '${fecha.trim()}' " +
                        "GROUP BY ${Tablas.Personas.COLUMNA_DEMAND}, ${Tablas.Personas.COLUMNA_DESC}, ${Tablas.Personas.COLUMNA_FECHA} " +
                        "ORDER BY ${Tablas.Personas.COLUMNA_DEMAND}, ${Tablas.Personas.COLUMNA_DESC} , COUNT(*) DESC"
                , null)
        if (cursor != null) {
            if (cursor.count > 0) {
                cursor.moveToFirst()
                do {
                    lista.add(ViajeDetalle(cursor.getString(0), cursor.getString(1), cursor.getInt(2), cursor.getString(3)))
                } while (cursor.moveToNext())
            }
        }
        return lista
    }


    fun ConsultaRider(fecha: String, status: String, rider: String): MutableList<ViajeRiderDetalle> {
        val lista = ArrayList<ViajeRiderDetalle>()
        val cursor: Cursor
        cursor = bd.rawQuery(
                "SELECT " +
                        "${Tablas.Personas.COLUMNA_SUPPLY},  " +
                        "${Tablas.Personas.COLUMNA_HORA}, " +
                        "${Tablas.Personas.COLUMNA_ORIGIN}, " +
                        "${Tablas.Personas.COLUMNA_DESTINO}, " +
                        "${Tablas.Personas.COLUMNA_TKS}, " +
                        "${Tablas.Personas.COLUMNA_SUPPLY_ACCEPT_TIME}, " +
                        "${Tablas.Personas.COLUMNA_CANCEL_REASON}, " +
                        "${Tablas.Personas.COLUMNA_USER_CANCEL}, " +
                        "${Tablas.Personas.COLUMNA_SUPPLY_ARRIVE_TIME}, " +
                        "${Tablas.Personas.COLUMNA_SUPPLY_ARRIVE_LOCATION}, " +
                        "${Tablas.Personas.COLUMNA_SUPPLY_ACCEPT_LOCATION}, " +
                        "${Tablas.Personas.COLUMNA_SUPPLY_CANCEL_LOCATION}, " +
                        "${Tablas.Personas.COLUMNA_CANCEL_TIME}, " +
                        "${Tablas.Personas.COLUMNA_DESC} " +
                        "FROM ${Tablas.Personas.NOMBRE_TABLA} " +
                        "WHERE ${Tablas.Personas.COLUMNA_FECHA} == '${fecha.trim()}' " +
                        "AND ${Tablas.Personas.COLUMNA_DEMAND} == '${rider.trim()}' " +
                        "AND ${Tablas.Personas.COLUMNA_DESC} == '${status.trim()}' " +
                        "ORDER BY ${Tablas.Personas.COLUMNA_HORA} ASC "
                , null)
        if (cursor != null) {
            if (cursor.count > 0) {
                cursor.moveToFirst()
                do {

                    lista.add(ViajeRiderDetalle(cursor.getString(0),
                            cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(8), cursor.getString(9), cursor.getString(10), cursor.getString(11), cursor.getString(12), cursor.getString(13)))
                } while (cursor.moveToNext())
            }
        }
        return lista
    }

    fun ActualizarDatosUsuarios(context: Context) {
        val lista = ArrayList<Datos>()
        var cursor: Cursor
        cursor = bd.rawQuery(
                "SELECT ${Tablas.Personas.COLUMNA_DEMAND} " +
                        "FROM ${Tablas.Personas.NOMBRE_TABLA} " +
                        "WHERE ${Tablas.Personas.COLUMNA_DEMAND} NOT IN " +
                        "(SELECT ${Tablas.Usuarios.COLUMNA_UID} FROM " +
                        "${Tablas.Usuarios.NOMBRE_TABLA})" +
                        "GROUP BY ${Tablas.Personas.COLUMNA_DEMAND}"
                , null)
        if (cursor != null) {
            if (cursor.count > 0) {
                cursor.moveToFirst()
                do {
                    lista.add(Datos(cursor.getString(0)))

                } while (cursor.moveToNext())
            }

        }
        lista.forEach {

            var url = "http://registry.taksio.net/rider/${it.uid}"

            val client = OkHttpClient()

            client.newCall(Request.Builder()
                    .url(url)
                    .build()).enqueue(object : Callback {

                override fun onFailure(call: Call?, e: IOException?) {
                    println("ERROR: ${e!!.message}")
                }

                override fun onResponse(call: Call?, response: Response?) {
                    (context as MainActivity).runOnUiThread {

                        var list = GsonBuilder().create().fromJson(response?.body()?.string(), DatosRider::class.java)
                        Log.d("RIDER UUID", it.uid)
                        Log.d("RIDER NOMBRE", list.name)
                        Log.d("", "------------------------")
                        bd.execSQL("INSERT INTO ${Tablas.Usuarios.NOMBRE_TABLA} " +
                                "( " +
                                "${Tablas.Usuarios.COLUMNA_UID} ," +
                                "${Tablas.Usuarios.COLUMNA_NOMBRE} ," +
                                "${Tablas.Usuarios.COLUMNA_EMAIL} ," +
                                "${Tablas.Usuarios.COLUMNA_TELEFONO}" +
                                ") " +
                                "VALUES " +
                                "( " +
                                "'${it.uid.trim()}', " +
                                "'${list.name.trim().replace("'","´")}', " +
                                "'${list.email.trim()}', " +
                                "'${list.phone.trim()}' " +
                                ") ")
                    }
                }
            })
        }

        lista.clear()


        cursor.close()

        cursor = bd.rawQuery(
                "SELECT ${Tablas.Personas.COLUMNA_SUPPLY} " +
                        "FROM ${Tablas.Personas.NOMBRE_TABLA} " +
                        "WHERE ${Tablas.Personas.COLUMNA_SUPPLY} NOT IN " +
                        "(SELECT ${Tablas.Usuarios.COLUMNA_UID} FROM " +
                        "${Tablas.Usuarios.NOMBRE_TABLA}) " +
                        "AND ${Tablas.Personas.COLUMNA_SUPPLY} != '-' " +
                        "GROUP BY ${Tablas.Personas.COLUMNA_SUPPLY}"
                , null)
        if (cursor != null) {
            if (cursor.count > 0) {
                cursor.moveToFirst()
                do {
                    lista.add(Datos(cursor.getString(0)))

                } while (cursor.moveToNext())
            }

        }



        lista.forEach {
            var url = "http://driver.taksio.net/taksio/public/ws.php?uuid=${it.uid}"

            val client = OkHttpClient()

            client.newCall(Request.Builder()
                    .url(url)
                    .build()).enqueue(object : Callback {

                override fun onFailure(call: Call?, e: IOException?) {
                    println("ERROR: ${e!!.message}")
                }

                override fun onResponse(call: Call?, response: Response?) {
                    (context as MainActivity).run {
                        var list = GsonBuilder().create().fromJson(response?.body()?.string(), DatosDriver::class.java)
                        Log.d("RIDER UUID", it.uid)
                        Log.d("RIDER NOMBRE", "${list.name1} ${list.lastname1}")
                        Log.d("", "------------------------")
                        bd.execSQL("INSERT INTO ${Tablas.Usuarios.NOMBRE_TABLA} " +
                                "( " +
                                "${Tablas.Usuarios.COLUMNA_UID} ," +
                                "${Tablas.Usuarios.COLUMNA_NOMBRE} ," +
                                "${Tablas.Usuarios.COLUMNA_TELEFONO}" +
                                ") " +
                                "VALUES " +
                                "( " +
                                "'${it.uid.trim()}', " +
                                "'${list.name1.trim()} ${list.lastname1.trim().replace("'","´")}', " +
                                "'${list.phone.trim()}' " +
                                ") ")

                    }
                }
            })

        }
    }


    fun GraficoTotalF(): MutableList<GraficoTotalO> {
        var datos: MutableList<GraficoTotalO> = mutableListOf()
        val cursor: Cursor

        cursor = bd.rawQuery(
                "SELECT CASE " +
                        "WHEN ${Tablas.Personas.COLUMNA_DESC} == 'TRIP_ENDED' THEN" +
                        "'COMPLETADO' ELSE " +
                        "'NO ATENDIDO' END, " +
                        "COUNT(*) " +
                        "FROM ${Tablas.Personas.NOMBRE_TABLA} " +
                        "GROUP BY ${Tablas.Personas.COLUMNA_DESC} " +
                        "ORDER BY ${Tablas.Personas.COLUMNA_DESC} DESC", null
        )

        if (cursor != null) {
            if (cursor.count > 0) {
                cursor.moveToFirst()
                do {
                    datos.add(GraficoTotalO(cursor.getString(0), cursor.getString(1)))
                } while (cursor.moveToNext())
            }
        }

        return datos
    }


    fun GraficoTotalDetalleCompletado(): MutableList<GraficoTotalD> {
        var datos: MutableList<GraficoTotalD> = mutableListOf()
        val cursor: Cursor

        cursor = bd.rawQuery(
                "SELECT A.${Tablas.Personas.COLUMNA_FECHA}, " +
                        "IFNULL(B.C,'0') " +
                        "FROM " +
                        "(SELECT ${Tablas.Personas.COLUMNA_FECHA} " +
                        "FROM ${Tablas.Personas.NOMBRE_TABLA} " +
                        "GROUP BY ${Tablas.Personas.COLUMNA_FECHA}) A " +
                        "LEFT OUTER JOIN " +
                        "(SELECT ${Tablas.Personas.COLUMNA_FECHA}, " +
                        "COUNT(*) AS C " +
                        "FROM ${Tablas.Personas.NOMBRE_TABLA} " +
                        "WHERE ${Tablas.Personas.COLUMNA_DESC} == 'TRIP_ENDED' " +
                        "GROUP BY ${Tablas.Personas.COLUMNA_FECHA}) B " +
                        "ON A.${Tablas.Personas.COLUMNA_FECHA} == B.${Tablas.Personas.COLUMNA_FECHA}", null)

        if (cursor != null) {
            if (cursor.count > 0) {
                cursor.moveToFirst()
                do {
                    datos.add(GraficoTotalD(cursor.getString(0), "COMPLETADO", cursor.getString(1)))
                } while (cursor.moveToNext())
            }
        }

        return datos
    }


    fun GraficoTotalDetalleCNoAtendido(): MutableList<GraficoTotalD> {
        var datos: MutableList<GraficoTotalD> = mutableListOf()
        val cursor: Cursor

        cursor = bd.rawQuery(
                "SELECT A.${Tablas.Personas.COLUMNA_FECHA}, " +
                        "IFNULL(B.C,'0') " +
                        "FROM " +
                        "(SELECT ${Tablas.Personas.COLUMNA_FECHA} " +
                        "FROM ${Tablas.Personas.NOMBRE_TABLA} " +
                        "GROUP BY ${Tablas.Personas.COLUMNA_FECHA}) A " +
                        "LEFT OUTER JOIN " +
                        "(SELECT ${Tablas.Personas.COLUMNA_FECHA}, " +
                        "COUNT(*) AS C " +
                        "FROM ${Tablas.Personas.NOMBRE_TABLA} " +
                        "WHERE ${Tablas.Personas.COLUMNA_DESC} == 'REQUEST_TIMEOUT' " +
                        "GROUP BY ${Tablas.Personas.COLUMNA_FECHA}) B " +
                        "ON A.${Tablas.Personas.COLUMNA_FECHA} == B.${Tablas.Personas.COLUMNA_FECHA}", null)

        if (cursor != null) {
            if (cursor.count > 0) {
                cursor.moveToFirst()
                do {
                    datos.add(GraficoTotalD(cursor.getString(0), "NO ATENDIDO", cursor.getString(1)))
                } while (cursor.moveToNext())
            }
        }

        return datos
    }


    fun GraficoTotalTD(): MutableList<GraficoTD> {
        var datos: MutableList<GraficoTD> = mutableListOf()
        val cursor: Cursor

        cursor = bd.rawQuery(
                "SELECT ${Tablas.Personas.COLUMNA_SUPPLY}, " +
                        "COUNT(*) " +
                        "FROM ${Tablas.Personas.NOMBRE_TABLA} " +
                        "WHERE ${Tablas.Personas.COLUMNA_DESC} == 'TRIP_ENDED' " +
                        "GROUP BY ${Tablas.Personas.COLUMNA_SUPPLY} " +
                        "ORDER BY COUNT(*) DESC", null
        )

        if (cursor != null) {
            if (cursor.count > 0) {
                cursor.moveToFirst()
                do {
                    datos.add(GraficoTD(cursor.getString(0), cursor.getString(1)))
                } while (cursor.moveToNext())
            }
        }

        return datos
    }


    fun GraficoTotalDetalleRider(): MutableList<GraficoTotalR> {
        val datos: MutableList<GraficoTotalR> = mutableListOf()
        val cursor: Cursor


        cursor = bd.rawQuery("SELECT " +
                "A.${Tablas.Personas.COLUMNA_DEMAND}, " +
                "IFNULL(B.C,'0'), " +
                "IFNULL(C.N,'0') " +
                "FROM " +
                "(SELECT ${Tablas.Personas.COLUMNA_DEMAND} " +
                "FROM ${Tablas.Personas.NOMBRE_TABLA} " +
                "GROUP BY ${Tablas.Personas.COLUMNA_DEMAND}) A " +
                "LEFT OUTER JOIN " +
                "(SELECT ${Tablas.Personas.COLUMNA_DEMAND}, COUNT(*) as C " +
                "FROM ${Tablas.Personas.NOMBRE_TABLA} " +
                "WHERE ${Tablas.Personas.COLUMNA_DESC} == 'TRIP_ENDED' " +
                "GROUP BY ${Tablas.Personas.COLUMNA_DEMAND}) B " +
                "ON A.${Tablas.Personas.COLUMNA_DEMAND} == B.${Tablas.Personas.COLUMNA_DEMAND} " +
                "LEFT OUTER JOIN " +
                "(SELECT ${Tablas.Personas.COLUMNA_DEMAND}, COUNT(*) as N " +
                "FROM ${Tablas.Personas.NOMBRE_TABLA} " +
                "WHERE ${Tablas.Personas.COLUMNA_DESC} == 'REQUEST_TIMEOUT' " +
                "AND ${Tablas.Personas.COLUMNA_DEMAND} != '-' " +
                "GROUP BY ${Tablas.Personas.COLUMNA_DEMAND}) C " +
                "ON A.${Tablas.Personas.COLUMNA_DEMAND} == C.${Tablas.Personas.COLUMNA_DEMAND} " +
                "ORDER BY B.C DESC, C.N DESC "
                , null)

        if (cursor != null) {
            if (cursor.count > 0) {
                cursor.moveToFirst()
                do {
                    datos.add(GraficoTotalR(cursor.getString(0), cursor.getString(1), cursor.getString(2)))
                } while (cursor.moveToNext())
            }
        }

        return datos
    }

    fun UsuarioDatos(uid: String): DatosUsuario {

        var nombre: String
        var cursor: Cursor
        cursor = bd.rawQuery(
                "SELECT " +
                        "${Tablas.Usuarios.COLUMNA_NOMBRE}," +
                        "${Tablas.Usuarios.COLUMNA_EMAIL}," +
                        "${Tablas.Usuarios.COLUMNA_TELEFONO} " +
                        "FROM ${Tablas.Usuarios.NOMBRE_TABLA} " +
                        "WHERE ${Tablas.Usuarios.COLUMNA_UID} == '${uid.trim()}'" +
                        "", null)
        cursor.moveToFirst()

        Log.d("", "----------------------------------")
        Log.d("UID USUARIO: ", uid)
        Log.d("NOMBRE USUARIO: ", cursor.getString(0))

        if (cursor.getString(0).split(" ").size > 2) {
            nombre = "${cursor.getString(0).split(" ")[0]} ${cursor.getString(0).split(" ")[1]}"
        } else {
            nombre = cursor.getString(0)
        }


        return DatosUsuario(nombre, cursor.getString(1), cursor.getString(2))

    }


    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }


}