package com.example.ingeniera.trakeoentregas;

import android.app.TimePickerDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.ingeniera.trakeoentregas.Destino.Destinos;

import java.util.ArrayList;
import java.util.Calendar;

import static com.example.ingeniera.trakeoentregas.Ingreso.SolicitarDestinos.almacenDestinos;

public class CambiarHorarios extends AppCompatActivity {

    private static final String CERO = "0";
    private static final String DOS_PUNTOS = ":";

    //Calendario para obtener fecha & hora
    public final Calendar c = Calendar.getInstance();

    //Variables para obtener la hora hora
    final int hora = c.get(Calendar.HOUR_OF_DAY);
    final int minuto = c.get(Calendar.MINUTE);

    //Widgets
    TextView horario1InicioTv,horario1FinTv,horario2InicioTv,horario2FinTv;
    FloatingActionButton horario1InicioFb,horario1FinFb,horario2InicioFb,horario2FinFb,cambiarhorarioTotalFb;

    private int idDestino;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cambiar_horarios);

        horario1InicioTv=findViewById(R.id.horario1InicioTv);
        horario1FinTv=findViewById(R.id.horario1FinTv);
        horario2InicioTv=findViewById(R.id.horaria2InicioTv);
        horario2FinTv=findViewById(R.id.horario2FinTv);

        horario1InicioFb=findViewById(R.id.horario1IncioFb);
        horario1FinFb=findViewById(R.id.horario1FinFb);
        horario2InicioFb=findViewById(R.id.horario2InicioFb);
        horario2FinFb=findViewById(R.id.horario2FinFb);
        cambiarhorarioTotalFb=findViewById(R.id.cambiarHorarioTotalFb);

        horario1InicioFb.setOnClickListener(ClicListener);
        horario1FinFb.setOnClickListener(ClicListener);
        horario2InicioFb.setOnClickListener(ClicListener);
        horario2FinFb.setOnClickListener(ClicListener);
        cambiarhorarioTotalFb.setOnClickListener(ClicListener);


        Bundle extras = getIntent().getExtras();
        idDestino=extras.getInt("id");

        mostrarHorarios();
    }

    private void mostrarHorarios() {
        ArrayList<Destinos> destinos=almacenDestinos.getArrayList("arrayDestinosKey");
        for (int i = 0;i<destinos.size();i++){
            if (idDestino==destinos.get(i).getId()){
                horario1InicioTv.setText(destinos.get(i).gethorario1_inicio());
                horario1FinTv.setText(destinos.get(i).gethorario1_fin());
                horario2InicioTv.setText(destinos.get(i).gethorario2_incio());
                horario2FinTv.setText(destinos.get(i).gethorario2_fin());
                break;
                }
            }
    }

    View.OnClickListener ClicListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.horario1IncioFb:
                    obtenerHora(horario1InicioTv);
                    break;
                case R.id.horario1FinFb:
                    obtenerHora(horario1FinTv);
                    break;
                case R.id.horario2InicioFb:
                    obtenerHora(horario2InicioTv);
                    break;
                case R.id.horario2FinFb:
                    obtenerHora(horario2FinTv);
                    break;
                case R.id.cambiarHorarioTotalFb:

                    Boolean horario1InicioBool=!horario1InicioTv.getText().toString().equals("");
                    Boolean horario1FinBool=!horario1FinTv.getText().toString().equals("");
                    Boolean horario2InicioBool=!horario2InicioTv.getText().toString().equals("");
                    Boolean horario2FinBool=!horario2FinTv.getText().toString().equals("");

                    String horario1InicioString=horario1InicioTv.getText().toString();
                    String horario1FinString=horario1FinTv.getText().toString();

                    String horario2InicioString=horario2InicioTv.getText().toString();
                    String horario2FinString=horario2FinTv.getText().toString();

                    if (horario1InicioBool&&horario1FinBool){ //SI se ingreso HORARIO 1
                        if (horario2InicioBool&&horario2FinBool){ //Si se ingreso HORARIO 2
                            if(!(horario1FinString.compareTo(horario1InicioString)<=0|| horario2InicioString.compareTo(horario1FinString)<=0||horario2FinString.compareTo(horario2InicioString)<=0)){ //Si TODOS estan BIEN ORDENADOS

                                int idTransporte=0,idCliente=0;
                                ArrayList<Destinos> destinos=almacenDestinos.getArrayList("arrayDestinosKey");
                                for (int i = 0;i<destinos.size();i++){
                                    if (idDestino==destinos.get(i).getId()){
                                        idTransporte=destinos.get(i).getId_transporte();
                                        idCliente=destinos.get(i).getId_cliente();
                                    }
                                }
                                almacenDestinos.saveArrayList(destinos);
                                TaskCambiarHorarios taskCambiarHorarios=new TaskCambiarHorarios(CambiarHorarios.this);
                                taskCambiarHorarios.execute(horario1InicioString,horario1FinString,horario2InicioString,horario2FinString,String.valueOf(idDestino),String.valueOf(idTransporte),String.valueOf(idCliente));

                            }else {
                                SingleToast.show(CambiarHorarios.this,"Horarios mal ordenados",0);
                            }
                        }else{ //SI NO SE INGRESO HORARIO 2
                            if(!(horario1FinString.compareTo(horario1InicioString)<=0)){ //SI HORARIO 1 ESTA BIEN ORDENADO

                                int idTransporte=0,idCliente=0;
                                ArrayList<Destinos> destinos=almacenDestinos.getArrayList("arrayDestinosKey");
                                for (int i = 0;i<destinos.size();i++){
                                    if (idDestino==destinos.get(i).getId()){
                                        idTransporte=destinos.get(i).getId_transporte();
                                        idCliente=destinos.get(i).getId_cliente();
                                    }
                                }
                                almacenDestinos.saveArrayList(destinos);
                                TaskCambiarHorarios taskCambiarHorarios=new TaskCambiarHorarios(CambiarHorarios.this);
                                taskCambiarHorarios.execute(horario1InicioString,horario1FinString,horario2InicioString,horario2FinString,String.valueOf(idDestino),String.valueOf(idTransporte),String.valueOf(idCliente));
                            }else {
                                SingleToast.show(CambiarHorarios.this,"Horario 1 mal ordenado",0);
                            }
                        }
                    }else {
                        SingleToast.show(CambiarHorarios.this,"Ingrese Horario 1",0);
                    }
                    break;
            }

        }
    };

    private void obtenerHora(final TextView textView){
        TimePickerDialog recogerHora = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                //Formateo el hora obtenido: antepone el 0 si son menores de 10
                String horaFormateada =  (hourOfDay < 10)? String.valueOf(CERO + hourOfDay) : String.valueOf(hourOfDay);
                //Formateo el minuto obtenido: antepone el 0 si son menores de 10
                String minutoFormateado = (minute < 10)? String.valueOf(CERO + minute):String.valueOf(minute);
                //Obtengo el valor a.m. o p.m., dependiendo de la selección del usuario
                /*String AM_PM;
                if(hourOfDay < 12) {
                    AM_PM = "a.m.";
                } else {
                    AM_PM = "p.m.";
                }*/
                //Muestro la hora con el formato deseado
                textView.setText(horaFormateada + DOS_PUNTOS + minutoFormateado);
                /*ArrayList<Destinos> destinos=almacenDestinos.getArrayList("arrayDestinosKey");
                for (int i = 0;i<destinos.size();i++){
                    if (idDestino==destinos.get(i).getId()){
                        int id=textView.getId();
                        switch (textView.getId()){
                            case R.id.jornadaIncioTv:
                                destinos.get(i).setJornada_inicio(jornadaIncioTv.getText().toString());
                                break;
                            case R.id.jornadaFinTv:
                                destinos.get(i).setJornada_fin(jornadaFinTv.getText().toString());
                                break;
                            case R.id.almuerzoInicioTv:
                                destinos.get(i).setAlmuerzo_incio(almuerzoInicioTv.getText().toString());
                                break;
                            case R.id.almuerzoFinTv:
                                destinos.get(i).setAlmuerzo_fin(almuerzoFinTv.getText().toString());
                                break;
                        }
                    }

                }

                almacenDestinos.saveArrayList(destinos);
                */

            }
            //Estos valores deben ir en ese orden
            //Al colocar en false se muestra en formato 12 horas y true en formato 24 horas
            //Pero el sistema devuelve la hora en formato 24 horas
        }, hora, minuto, true);

        recogerHora.show();
    }
}
