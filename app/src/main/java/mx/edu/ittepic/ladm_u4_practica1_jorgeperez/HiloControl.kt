package mx.edu.ittepic.ladm_u4_practica1_jorgeperez

class HiloControl (p:MainActivity) : Thread() {
    private var iniciar = false
    private var puntero = p

    override fun run() {
        super.run()
        iniciar = true
        while (iniciar) {
            sleep(1000)
            puntero.runOnUiThread {
                puntero.enviarSMS()
            }
        }
    }
}