package mx.edu.ittepic.ladm_u4_practica1_jorgeperez

import android.database.sqlite.SQLiteException
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main2.*


class Main2Activity : AppCompatActivity() {

    val nombreBD = "contestadora"
    var listaID = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        cargaLista()
        cargaMensaje()

        ckBoxMsj.setOnClickListener{
            cargaMensaje()
        }

        btnGuardarMensaje.setOnClickListener {
            if(txtMensaje.text.isEmpty()) {
                mensaje("DEBE ESCRIBIR UN MENSAJE")
                return@setOnClickListener
            }

            actualizaMensaje(txtMensaje.text.toString())

        }

        btnGuardarTelefono.setOnClickListener {
            if(txtTelefono.text.isEmpty() || txtNombre.text.isEmpty()){
                mensaje("NÚMERO DE TELÉFONO O NOMBRE NO VÁLIDO")
                return@setOnClickListener
            }
            agregaTelefono(txtTelefono.text.toString(), txtNombre.text.toString())
            txtTelefono.setText("")
            txtNombre.setText("")
            cargaLista()
            txtTelefono.requestFocus()
        }
    }

    fun cargaMensaje() {
        try {
            var baseDatos = BaseDatos(this, nombreBD, null, 1)
            var select = baseDatos.readableDatabase
            var SQL1 = "SELECT * FROM MENSAJES WHERE ID = 1"
            var SQL2 = "SELECT * FROM MENSAJES WHERE ID = 2"
            var cursor1 = select.rawQuery(SQL1, null)
            var cursor2 = select.rawQuery(SQL2, null)

            if(ckBoxMsj.isChecked){
                if(cursor1.moveToFirst()){
                    //SI HAY RESULTADO
                    txtMensaje.setText(cursor1.getString(1))
                } else {
                    //NO HAY RESULTADO
                }
            }else if (ckBoxMsj.isChecked == false){
                if(cursor2.moveToFirst()){
                    //SI HAY RESULTADO
                    txtMensaje.setText(cursor2.getString(1))
                } else {
                    //NO HAY RESULTADO
                }
            }

            select.close()
            baseDatos.close()
        } catch (error : SQLiteException){ }
    }

    fun cargaLista() {
        try {
            var baseDatos = BaseDatos(this, nombreBD, null, 1)
            var select = baseDatos.readableDatabase
            var SQL = "SELECT * FROM TELEFONOS"
            var cursor = select.rawQuery(SQL, null)

            if(cursor.count > 0) {
                var arreglo = ArrayList<String>()
                this.listaID.clear()
                cursor.moveToFirst()
                var cantidad = cursor.count-1

                (0..cantidad).forEach {
                        var data = "Teléfono: ${cursor.getString(1)} \nNombre: ${cursor.getString(2)}" +
                                "\nEs: ${statTelefono(cursor.getString(1))}"
                        arreglo.add(data)
                        listaID.add(cursor.getString(0))
                    cursor.moveToNext()
                }

                listaA.adapter = ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, arreglo)
                listaA.setOnItemClickListener { parent, view, position, id ->
                    AlertDialog.Builder(this)
                        .setTitle("ATENCIÓN")
                        .setMessage("¿Desea realmente eliminar el teléfono seleccionado?")
                        .setPositiveButton("Eliminar") {d, i ->
                            eliminaTelefono(listaID[position])
                        }
                        .setNegativeButton("Cancelar") {d, i -> }
                        .show()
                }
            }

            select.close()
            baseDatos.close()
        } catch (error : SQLiteException){
            mensaje(error.message.toString())
        }
    }

    fun eliminaTelefono(id : String) {
        try {
            var baseDatos = BaseDatos(this, nombreBD, null, 1)
            var eliminar = baseDatos.writableDatabase
            var SQL = "DELETE FROM TELEFONOS WHERE ID = ?"
            var parametros = arrayOf(id)

            eliminar.execSQL(SQL,parametros)
            eliminar.close()
            baseDatos.close()
            mensaje("SE ELIMINÓ CORRECTAMENTE")
            cargaLista()
        } catch (error : SQLiteException) {
            mensaje(error.message.toString())
        }
    }

    fun agregaTelefono(numero : String, nombre : String) {
        var baseDatos = BaseDatos(this, nombreBD, null, 1)
        var insertar = baseDatos.writableDatabase
        var SQL1 = "INSERT INTO TELEFONOS VALUES(NULL, '${numero}','${nombre}', '1')"
        var SQL2 = "INSERT INTO TELEFONOS VALUES(NULL, '${numero}','${nombre}', '2')"

        if(ckBoxTelefono.isChecked){
            insertar.execSQL(SQL1)
            mensaje("AGRADABLE")
        }else if(!ckBoxTelefono.isChecked){
            insertar.execSQL(SQL2)
        }
        insertar.close()
        baseDatos.close()

        mensaje("SE INSERTÓ CORRECTAMENTE EL TELÉFONO")
    }

    fun actualizaMensaje(mensaje : String) {
        try {
            var baseDatos = BaseDatos(this, nombreBD, null, 1)
            var actualizar = baseDatos.writableDatabase
            var SQL = "UPDATE MENSAJES SET MENSAJE='${mensaje}' WHERE ID=?"
            var parametros1 = arrayOf(1)
            var parametros2 = arrayOf(2)

            if(ckBoxMsj.isChecked){
                actualizar.execSQL(SQL, parametros1)
            }else if(ckBoxMsj.isChecked == false) {
                actualizar.execSQL(SQL, parametros2)
            }

            actualizar.close()
            baseDatos.close()

            mensaje("SE ACTUALIZÓ CORRECTAMENTE EL MENSAJE")
        } catch (error : SQLiteException) {
            mensaje(error.message.toString())
        }
    }

    fun mensaje(mensaje : String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG)
            .show()
    }

    fun statTelefono(tel : String) : String {
        try {
            var baseDatos = BaseDatos(this, nombreBD, null, 1)
            var select = baseDatos.readableDatabase
            var SQL = "SELECT * FROM TELEFONOS"
            var cursor = select.rawQuery(SQL, null)

            if(cursor.count > 0) {
                cursor.moveToFirst()
                var cantidad = cursor.count-1

                (0..cantidad).forEach {
                    if(cursor.getString(1) == tel) {
                        if(cursor.getString(3) == "1") {
                            return "AGRADABLE"
                        } else if(cursor.getString(3) == "2") {
                            return "NO AGRADABLE"
                        }
                    }
                    cursor.moveToNext()
                }
            }
            select.close()
            baseDatos.close()
        } catch (error : SQLiteException){ }
        return "IGNORADA"
    }
}