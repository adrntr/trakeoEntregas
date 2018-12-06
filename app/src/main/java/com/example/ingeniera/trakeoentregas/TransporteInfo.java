package com.example.ingeniera.trakeoentregas;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ingeniera.trakeoentregas.Destino.Destinos;
import com.example.ingeniera.trakeoentregas.Destino.Usuarios;
import com.example.ingeniera.trakeoentregas.Entregas.QrReader;
import com.example.ingeniera.trakeoentregas.Entregas.TaskConsultarQrCode;

import java.util.ArrayList;

import static com.example.ingeniera.trakeoentregas.Ingreso.SolicitarDestinos.almacenDestinos;

public class TransporteInfo extends AppCompatActivity {

    private TextView transporteTv,direccionTv,clienteTv,cantidadTv,tipoTv,telefonoTv,cierreTV,idDestinoTv,entregadoTv,entregadoTv3,entregarTransporteInfoTv,irTransporteInfoTv,cancelarEntregaTransporteInfoTv;
    private ImageView tipoEnvioInfoIv;
    private FloatingActionButton entregarTransporteInfoFb,irTransporteInfoFb,cancelarEntregaBt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transporte_info);

        transporteTv=findViewById(R.id.transporteTv);
        direccionTv=findViewById(R.id.direccionTv);
        clienteTv=findViewById(R.id.clienteTv);
        cantidadTv=findViewById(R.id.cantidadTv);
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

        cancelarEntregaBt.setOnClickListener(clicListener);
        entregarTransporteInfoFb.setOnClickListener(clicListener);
        irTransporteInfoFb.setOnClickListener(clicListener);

        Bundle extras = getIntent().getExtras();
        int idDestino=extras.getInt("idDestino");

        ArrayList<Destinos> destinos = almacenDestinos.getArrayList("arrayDestinosKey");

        for (int i=0;i<destinos.size();i++){
            if(destinos.get(i).getId()==idDestino){
                Destinos destino = new Destinos();
                destino=destinos.get(i);
                transporteTv.setText(destino.getNombre_transporte());
                direccionTv.setText(destino.getDireccion());
                clienteTv.setText(destino.getNombre_cliente());
                cantidadTv.setText(String.valueOf(destino.getCantidad()));
                tipoTv.setText(destino.getNombre_tipo_registro());
                telefonoTv.setText(destino.getTelefono());
                cierreTV.setText("18:00");
                idDestinoTv.setText(String.valueOf(destino.getId()));
                if(destino.getEntregado()){
                    cancelarEntregaBt.setVisibility(View.VISIBLE);
                    entregadoTv.setVisibility(View.VISIBLE);
                    entregadoTv3.setVisibility(View.VISIBLE);
                    entregarTransporteInfoFb.setVisibility(View.GONE);
                    irTransporteInfoFb.setVisibility(View.GONE);
                    entregadoTv.setText(destino.getFechaHoraEntrega());
                    irTransporteInfoTv.setVisibility(View.GONE);
                    entregarTransporteInfoTv.setVisibility(View.GONE);
                    cancelarEntregaTransporteInfoTv.setVisibility(View.VISIBLE);
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
                switch (destino.getId_tipo_registro()){
                    case 1:
                        tipoEnvioInfoIv.setImageResource(R.drawable.delivery);
                        break;
                    case 2:
                        tipoEnvioInfoIv.setImageResource(R.drawable.send);
                        break;

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
                            taskConsultarQrCode.execute(String.valueOf(destinos3.get(i).getId_externo()),String.valueOf(1),"1",String.valueOf(destinos3.get(i).getId()));
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

            }
        }
    };
}
