package com.example.jstalin.practicacontador;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {


    // VARIABLES QUE INDICAR EL INICIO Y FIN DE LA CUENTA
    private static final int INICIOCUENTA = 0;
    private static final int FINCUENTA = 10;

    // VARIBLES NECESARIAS PARA EL MANEJO DE LA APLICACION
    private TextView contador;

    private ProgressBar barraProgreso;

    private Button btComenzar;
    private Button btCancelar;
    private Button btParar;
    private Button btReaunudar;


    // INSTANCIA DE LA CLASE QUE PERMITE EJCUTARA LA TAREAS EN SEGUNDO PLANO
    private EjecutarCuenta hilo; // hilo a ejecutar


    /**
     * Metodo que es llamado cuando la Activity se crea
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // REferencia al archivo xml que contiene los
        // componentes que va a cargar la Actibity


        // OBTENCION MEDIANTE EL ID DE LOS VIEW DEL XML PARA MANIPULARLOS A CONTINUACION
        btComenzar = (Button) findViewById(R.id.btComenzar);

        btCancelar = (Button) findViewById(R.id.btCancelar);
        btCancelar.setEnabled(false);// Desabilitamos el boton de Cancelar Cuenta


        btParar = (Button) findViewById((R.id.btParar));
        btParar.setVisibility(View.INVISIBLE); // Hacemos invisible el boton de Parar Cuenta

        btReaunudar = (Button) findViewById(R.id.btReaundar);
        btReaunudar.setVisibility(View.INVISIBLE); // Hacemos invisible el boton de Reaunudar Cuenta

        contador = (TextView) findViewById(R.id.tvContador);

        barraProgreso = (ProgressBar) findViewById(R.id.barraProgeso);

    }


    /**
     * Metodo que inicia el proceso que posee el attibuto hilo
     *
     * @param v
     */
    public void comenzarCuenta(View v) {

        hilo = new EjecutarCuenta();
        hilo.execute();

    }

    /**
     * Metodo que cancela el proceso que esta ejecutando el atributo hilo
     *
     * @param v
     */
    public void cancelarCuenta(View v) {

        hilo.cancel(true);

    }

    /**
     * Metodo que llama el metodo reanudar() que se encuentra dentro de la clase del objeto hilo
     *
     * @param v
     */
    public void reaunudarCuenta(View v) {

        hilo.reaunudar();

    }

    /**
     * Metodo que llama el metodo pausar() que se encuentra dentro de la clase del objeto hilo
     *
     * @param v
     */
    public void pausarCuenta(View v) {
        hilo.pausar();
    }

    /**
     * CLASE QUE EXTIENDE DE LA CLASE ASYNTASK EJECUTARA UN CONTADOR EN SEGUNDO PLANO Y LO IRA ACTUALIZANDO CON
     * LA INTERFAZ PRINCIPAL EN CADA ITERACION
     */
    class EjecutarCuenta extends AsyncTask<Void, Integer, Boolean> {


        // VARIABLES NECESARIAS PARA EL PROCESO
        private int cont;  // Guarda el estado del contador

        private boolean pausado; // Indica si el proceso ha sido pausado (true=pausado false=no esta pausado)

        /**
         * Metodo que es llamado cuando se iniciar el proceso mediante el metodo .execute()
         */
        @Override
        protected void onPreExecute() {

            Toast.makeText(MainActivity.this, "INICIO CUENTA",
                    Toast.LENGTH_SHORT).show();

            // Inicializacion de la variables para comenzar el proceso
            cont = MainActivity.INICIOCUENTA;

            pausado = false;
            contador.setText(cont + "");

            barraProgreso.setMax(MainActivity.FINCUENTA);
            barraProgreso.setProgress(cont);

            btComenzar.setEnabled(false);
            btCancelar.setEnabled(true);
            btReaunudar.setVisibility(View.INVISIBLE);
            btParar.setVisibility(View.VISIBLE);


        }


        /**
         * Metodo que sera ejecutara en segundo plano
         *
         * @param n
         * @return Boolean. YRUE si el proceso ha finalizado correctamente
         */
        @Override
        protected Boolean doInBackground(Void... n) {

            // Realizamos un bucle que ira cont, hasta el valor de la variable FINCUENTA de la clase MainActiity
            // Ademas comprobamos si el metodo isCancelled() devuelve falso, en caso contrario saldremos del bucle
            for (int i = cont; i <= MainActivity.FINCUENTA && !isCancelled(); i++) {
                publishProgress(i); // enviamos del valor de i al metodo onProgessUpdate()
                cont = i; // Asignamos a la variable cont el valor de i
                try {
                    Thread.sleep(1000); // Dormimos 1000 segundo el hilo para simular el paso de 1 segundo
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                while (pausado && !isCancelled()) { // Bucle que comprueba si se ha pausado y si el hilo no ha sido cancelado
                    try {
                        Thread.sleep((int) (Math.random() * 100));  // Duerme el hilo un tiempo aleatorio y vuelve a comprobar
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }


            }

            return true; // Si el proceso ha ido bien devuelve true
        }

        /**
         * Metodo que se comunica con la UI principal para actuzlizar los datos q
         * que estan siendo tratados en el metodo doInBackground()
         *
         * @param valores
         */
        @Override
        protected void onProgressUpdate(Integer... valores) {
            int progeso = valores[0].intValue(); // Almacenamos el valor que es pasado por el metodo doInBackground()
            contador.setText(progeso + ""); // Se actualiza el contador
            barraProgreso.setProgress(progeso); // Se actualiza la barra de progreso

        }

        /**
         * Metodo que se ejecuta cuando temrina el metodo doInBackground()
         *
         * @param res
         */
        @Override
        protected void onPostExecute(Boolean res) {
            if (res) { // Comprobamos si el metodo doInBackgroun() ha finalizado correctamente
                Toast.makeText(MainActivity.this, "CUENTA FINALIZADA",
                        Toast.LENGTH_SHORT).show();

                // Configuracion de botones

                btComenzar.setEnabled(true);
                btCancelar.setEnabled(false);
                btReaunudar.setVisibility(View.INVISIBLE);
                btParar.setVisibility(View.INVISIBLE);
            }

        }

        /**
         * Metodo que es llamado si hace ejecuta el emtodo cancel()
         */
        @Override
        protected void onCancelled() {
            Toast.makeText(MainActivity.this, "CUENTA CANCELADA",
                    Toast.LENGTH_SHORT).show();


            contador.setText("CANCELADO"); // INDICAMOS EN EL CONTADOR QUE LA CUENTA ESTA CANCELADA


            //Configutaion de botones

            btComenzar.setEnabled(true);
            btCancelar.setEnabled(false);
            btParar.setVisibility(View.INVISIBLE);
            btReaunudar.setVisibility(View.INVISIBLE);
        }


        /**
         * Metodo que pausa el proceso de el metodo doInBackground
         */
        public void pausar() {

            pausado = true; // Cambiamos el valor de la variable pausado a true p
            // para indicar que el proceso esta en pausa

            contador.setText("En Pausa"); // Indicamos en el contador que el proceso esta en pausa


            // Configuracion de botones

            btComenzar.setEnabled(false);
            btCancelar.setEnabled(true);
            btParar.setVisibility(View.INVISIBLE);
            btReaunudar.setVisibility(View.VISIBLE);


        }

        /**
         * Metodo que raunuda  el proceso de el metodo doInBackground si ha sido pausado
         */
        public void reaunudar() {

            pausado = false; // Cambiamos el valor de la variable pausado a false indicando
            // que el proceos ya no esta pausado


            // Configuracion de Botones
            btComenzar.setEnabled(false);
            btCancelar.setEnabled(true);
            btReaunudar.setVisibility(View.INVISIBLE);
            btParar.setVisibility(View.VISIBLE);

        }


    }

}
