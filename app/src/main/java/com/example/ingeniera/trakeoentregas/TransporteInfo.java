package com.example.ingeniera.trakeoentregas;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.ingeniera.trakeoentregas.Destino.Destinos;
import com.example.ingeniera.trakeoentregas.Destino.ListaDestinos;
import com.example.ingeniera.trakeoentregas.Destino.MapsActivity;
import com.example.ingeniera.trakeoentregas.Destino.TaskCancelarDestino;
import com.example.ingeniera.trakeoentregas.Destino.TaskCancerlarHojaDeRuta;
import com.example.ingeniera.trakeoentregas.Destino.Usuarios;
import com.example.ingeniera.trakeoentregas.Entregas.QrReader;
import com.example.ingeniera.trakeoentregas.Entregas.TaskConsultarQrCode;

import java.util.ArrayList;
import java.util.TreeMap;

import static com.example.ingeniera.trakeoentregas.Ingreso.SolicitarDestinos.almacenDestinos;

public class TransporteInfo extends AppCompatActivity {

    private static final int CAMBIAR_DIRECCION = 1000;
    private TextView transporteTv,direccionTv,clienteTv,tipoTv,telefonoTv,cierreTV,idDestinoTv,entregadoTv,entregadoTv3,entregarTransporteInfoTv,irTransporteInfoTv,cancelarEntregaTransporteInfoTv;
    private ImageView tipoEnvioInfoIv;
    private FloatingActionButton entregarTransporteInfoFb,irTransporteInfoFb,cancelarEntregaBt,cambiarDireccionBt,cambiarHorarioBt;
    Switch switchAB;

    int idDestino;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transporte_info);

        transporteTv=findViewById(R.id.transporteTv);
        direccionTv=findViewById(R.id.direccionTv);
        clienteTv=findViewById(R.id.clienteTv);

        tipoTv=findViewById(R.id.tipoTv);
        telefonoTv=findViewById(R.id.telefonoTv);
        cierreTV=findViewById(R.id.cierreTv);
        idDestinoTv=findViewById(R.id.idDestinoTv);
        cancelarEntregaBt=findViewById(R.id.cancelarEntregaBt);
        entregadoTv=findViewById(R.id.entregadoTv);
        entregadoTv3=findViewById(R.id.entregadoTv3);
        tipoEnvioInfoIv=findViewById(R.id.tipoEnvioInfoIv);
        entregarTransporteInfoFb=findViewById(R.id.entregarTransporteInfoFb);
        irTransporteInfoFb=findViewById(R.id.irTransporteInfoFb);
        entregarTransporteInfoTv=findViewById(R.id.entregarTransporteInfoTv);
        irTransporteInfoTv=findViewById(R.id.irTransporteInfoTv);
        cancelarEntregaTransporteInfoTv=findViewById(R.id.cancelarEntregaTransporteInfoTv);
        cambiarDireccionBt=findViewById(R.id.cambiarDireccionBt);
        cambiarHorarioBt=findViewById(R.id.cambiarHorariobt);




        cancelarEntregaBt.setOnClickListener(clicListener);
        entregarTransporteInfoFb.setOnClickListener(clicListener);
        irTransporteInfoFb.setOnClickListener(clicListener);
        cambiarDireccionBt.setOnClickListener(clicListener);
        cambiarHorarioBt.setOnClickListener(clicListener);

        Bundle extras = getIntent().getExtras();
        idDestino=extras.getInt("idDestino");

        ArrayList<Destinos> destinos = almacenDestinos.getArrayList("arrayDestinosKey");

        for (int i=0;i<destinos.size();i++){
            if(destinos.get(i).getId()==idDestino){
                Destinos destino = new Destinos();
                destino=destinos.get(i);
                transporteTv.setText(destino.getNombre_transporte());
                direccionTv.setText(destino.getDireccion());
                clienteTv.setText(destino.getNombre_cliente());
                tipoTv.setText(destino.getMotivo());
                telefonoTv.setText(destino.getTelefono());

                String horario1Inicio=destino.gethorario1_inicio();
                String horario1Fin=destino.gethorario1_fin();
                String horario2Inicio=destino.gethorario2_incio();
                String horario2Fin=destino.gethorario2_fin();




                Boolean horario1InicioBool=(horario1Inicio==null||horario1Inicio.equals(""))? false : true;
                Boolean horario1FinBool=(horario1Fin==null||horario1Fin.equals(""))? false : true;
                Boolean horario2InicioBool=(horario2Inicio==null||horario2Inicio.equals(""))? false : true;
                Boolean horario2FinBool=(horario2Fin==null||horario2Fin.equals(""))? false : true;

                if (!horario1FinBool||!horario1InicioBool){
                    horario1Fin="00:00";
                    horario1Inicio="00:00";
                }
                if (!horario2InicioBool||!horario2FinBool){
                    cierreTV.setText(horario1Inicio +" a " +horario1Fin  );
                }else {
                    cierreTV.setText(horario1Inicio +" a "+horario1Fin +" - "+ horario2Inicio +" a " +horario2Fin  );
                }

                idDestinoTv.setText(String.valueOf(destino.getId()));
                if(destino.getEntregado()){
                    cancelarEntregaBt.setVisibility(View.VISIBLE);
                    entregadoTv.setVisibility(View.VISIBLE);
                    entregadoTv3.setVisibility(View.VISIBLE);
                    entregarTransporteInfoFb.setVisibility(View.GONE);
                    irTransporteInfoFb.setVisibility(View.GONE);
                    entregadoTv.setText(destino.getFecha_despacho());
                    irTransporteInfoTv.setVisibility(View.GONE);
                    entregarTransporteInfoTv.setVisibility(View.GONE);
                    cancelarEntregaTransporteInfoTv.setVisibility(View.VISIBLE);
                }else if (destino.getCancelado()){
                    cancelarEntregaBt.setVisibility(View.GONE);
                    entregadoTv.setVisibility(View.GONE);
                    entregadoTv3.setVisibility(View.GONE);
                    entregarTransporteInfoFb.setVisibility(View.GONE);
                    irTransporteInfoFb.setVisibility(View.GONE);
                    irTransporteInfoTv.setVisibility(View.GONE);
                    entregarTransporteInfoTv.setVisibility(View.GONE);
                    cancelarEntregaTransporteInfoTv.setVisibility(View.GONE);
                }else {
                    cancelarEntregaBt.setVisibility(View.GONE);
                    entregadoTv.setVisibility(View.GONE);
                    entregadoTv3.setVisibility(View.GONE);
                    entregarTransporteInfoFb.setVisibility(View.VISIBLE);
                    irTransporteInfoFb.setVisibility(View.VISIBLE);
                    irTransporteInfoTv.setVisibility(View.VISIBLE);
                    entregarTransporteInfoTv.setVisibility(View.VISIBLE);
                    cancelarEntregaTransporteInfoTv.setVisibility(View.GONE);
                }
                Boolean pedido=false,sobre=false,publicidad=false;
                ArrayList<Integer> idsTiposRegistro=destino.getIds_tipos_registro();
                try{
                    for (int j=0;j<idsTiposRegistro.size();j++){
                        if (idsTiposRegistro.get(j)==1){
                            pedido=true;
                        }else if(idsTiposRegistro.get(j)==2){
                            sobre=true;
                        }else if (idsTiposRegistro.get(j)==3){
                            publicidad=true;
                        }
                    }
                    if(pedido||publicidad){
                       tipoEnvioInfoIv.setImageResource(R.drawable.delivery);
                    }else if (sobre){
                        tipoEnvioInfoIv.setImageResource(R.drawable.send);
                    }
                    if ((pedido||publicidad)&&sobre){
                        tipoEnvioInfoIv.setImageResource(R.drawable.sobrecaja);
                    }
                }catch (NullPointerException e){
                    SingleToast.show(TransporteInfo.this,e.toString(),1);
                }


            }
        }

        ArrayList<Usuarios> usuarios=almacenDestinos.getArrayUsuarios("arrayUsuariosKey");
        for (int i=0;i<usuarios.size();i++){
            if (usuarios.get(i).getTipo().equals("Responsable")){
                setTitle("INFORMACIÓN - "+usuarios.get(i).getNombre());
            }
        }


    }

    View.OnClickListener clicListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.cancelarEntregaBt:
                    ArrayList<Destinos> destinos = almacenDestinos.getArrayList("arrayDestinosKey");
                    int ID= Integer.parseInt(idDestinoTv.getText().toString());
                    for (int i=0;i<destinos.size();i++){
                        if(destinos.get(i).getId()==ID){
                            TaskConsultarQrCode taskConsultarQrCode = new TaskConsultarQrCode(TransporteInfo.this,destinos,i);
                            taskConsultarQrCode.execute(String.valueOf(destinos.get(i).getId_externo()),String.valueOf(destinos.get(i).getId_tipo_registro()),"0",String.valueOf(destinos.get(i).getId()));
                        }
                    }
                    break;
                case R.id.entregarTransporteInfoFb:
                    int id=Integer.parseInt(idDestinoTv.getText().toString());
                    ArrayList<Destinos> destinos3 = almacenDestinos.getArrayList("arrayDestinosKey");
                    for(int i=0;i<destinos3.size();i++){
                        if (destinos3.get(i).getId()==id){
                            TaskConsultarQrCode taskConsultarQrCode = new TaskConsultarQrCode(TransporteInfo.this,destinos3,i);
                            taskConsultarQrCode.execute(String.valueOf(destinos3.get(i).getId_externo()),String.valueOf(destinos3.get(i).getId_tipo_registro()),"1",String.valueOf(destinos3.get(i).getId()));
                        }
                    }


                    break;
                case R.id.irTransporteInfoFb:
                    String latDest = null,lngDest = null;
                    int id2=Integer.parseInt(idDestinoTv.getText().toString());
                    ArrayList<Destinos> destinos2 = almacenDestinos.getArrayList("arrayDestinosKey");
                    for(int i=0;i<destinos2.size();i++){
                        if (destinos2.get(i).getId()==id2){
                            latDest=String.valueOf(destinos2.get(i).getLatitude());
                            lngDest=String.valueOf(destinos2.get(i).getLongitude());
                        }
                    }
                    Uri uri = Uri.parse("https://www.google.com/maps/dir/?api=1&origin="+almacenDestinos.getLat()+","+almacenDestinos.getLng()+"&destination="+latDest+","+lngDest+"&sensor=false&mode=driving");
                    Intent intent2 = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent2);
                    break;

                case R.id.cambiarDireccionBt:
                    Intent intent3= new Intent(TransporteInfo.this,CambiarDireccion.class);
                    intent3.putExtra("id",idDestino);
                    startActivityForResult(intent3,CAMBIAR_DIRECCION);
                    break;

                case R.id.cambiarHorariobt:
                    Intent intent4= new Intent(TransporteInfo.this,CambiarHorarios.class);
                    intent4.putExtra("id",idDestino);
                    startActivityForResult(intent4,CAMBIAR_DIRECCION);
                    break;
                case R.id.switchAB:

                    int id3=Integer.parseInt(idDestinoTv.getText().toString());
                    int h=0;

                    ArrayList<Destinos> destinos4 = almacenDestinos.getArrayList("arrayDestinosKey");
                    for(int i=0;i<destinos4.size();i++){
                        if (destinos4.get(i).getId()==id3){
                            h=i;
                        }
                    }


                    if (switchAB.isChecked()) {
                        TaskCancelarDestino taskCancelarDestino = new TaskCancelarDestino(TransporteInfo.this,destinos4,h);
                        taskCancelarDestino.execute(String.valueOf(idDestino),String.valueOf(1));
                    } else {
                        TaskCancelarDestino taskCancelarDestino = new TaskCancelarDestino(TransporteInfo.this,destinos4,h);
                        taskCancelarDestino.execute(String.valueOf(idDestino),String.valueOf(0));
                    }
                    break;
            }
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.transporte_info_menu, menu);
        MenuItem item = (MenuItem) menu.findItem(R.id.switchId);
        item.setActionView(R.layout.switch_layout);
        switchAB = item
                .getActionView().findViewById(R.id.switchAB);
        ArrayList<Destinos> destinos = almacenDestinos.getArrayList("arrayDestinosKey");

        for (int i = 0; i<destinos.size();i++){
            if (destinos.get(i).getId()==idDestino){
                if (destinos.get(i).getEntregado()){
                    switchAB.setVisibility(View.GONE);
                }else{
                    switchAB.setChecked(destinos.get(i).getCancelado());
                }

            }
        }

        switchAB.setOnClickListener(clicListener);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CAMBIAR_DIRECCION) {
            recreate();
        }

    }//onActivityResult

}
