package com.example.ingeniera.trakeoentregas.Destino;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import com.example.ingeniera.trakeoentregas.Entregas.TaskConsultarQrCode;
import com.example.ingeniera.trakeoentregas.R;
import com.example.ingeniera.trakeoentregas.SingleToast;
import com.example.ingeniera.trakeoentregas.TransporteInfo;

import java.util.ArrayList;
import java.util.Calendar;

import static android.text.Spanned.SPAN_INCLUSIVE_INCLUSIVE;
import static com.example.ingeniera.trakeoentregas.Ingreso.SolicitarDestinos.almacenDestinos;

public class ListDestinosAdapter extends RecyclerView.Adapter<ListDestinosAdapter.ListViewHolder> {

    private Context mCtx;//para pasarle las vistas

    public ListDestinosAdapter(Context mCtx) {
        this.mCtx = mCtx;
    }


    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.activity_lista_destinos, null);
        ListViewHolder holder = new ListViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {

        ArrayList<Destinos> destinos = almacenDestinos.getArrayDestinosFiltrados();
        Destinos destino = destinos.get(position);
        holder.cardViewTransporte.setVisibility(View.VISIBLE);
        if (!(destino.getNombre_transporte().equals("SUCURSAL CLIENTE"))){
            String clienteString=destino.getNombre_cliente();
            holder.transporteTv.setText(destino.getOrden()+" - "+destino.getNombre_transporte()+" ("+clienteString+" )");
        }else {
            holder.transporteTv.setText(destino.getOrden()+" - "+destino.getNombre_cliente());
        }

        holder.dirTransporteTv.setText(destino.getDireccion());
        //holder.clienteTv.setText(String.valueOf(destino.getNombre_cliente()));

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
            holder.horariosCardTv.setText(horario1Inicio +" a " +horario1Fin  );
        }else {
            holder.horariosCardTv.setText(horario1Inicio +" a "+horario1Fin +" - "+ horario2Inicio +" a " +horario2Fin  );
        }

        holder.cantidadTv.setText(destino.getMotivo());
        holder.codigoClienteTv.setText(String.valueOf(destino.getId()));
        String lat = almacenDestinos.getLat();
        if (almacenDestinos.getLat() != "") {
            Double difLat = destino.getLatitude() - Double.parseDouble(almacenDestinos.getLat());
            Double difLng = destino.getLongitude() - Double.parseDouble(almacenDestinos.getLng());
            Double dist = Math.sqrt(Math.pow(difLat, 2) + Math.pow(difLng, 2));
            holder.distanciaListaDestinosTv.setText("Dist: " + String.valueOf(Math.round(dist * 10000.0) / 100.0) + " Km");
            holder.progressBar.setMax(100);
            Double porcentaje = dist * 100 / 0.3;
            if (porcentaje > 100) {
                porcentaje = 100.0;
            }
            holder.progressBar.setProgress((int) (100.0 - porcentaje));
        }

        holder.cardViewTransporte.setCardBackgroundColor(0xFFFFFFFF);


        //Calendario para obtener fecha & hora
        final Calendar c = Calendar.getInstance();

        //Variables para obtener la hora hora
        final int hora = c.get(Calendar.HOUR_OF_DAY);
        final int minuto = c.get(Calendar.MINUTE);
        String minutoString;
        if (minuto<10){
            minutoString="0"+minuto;
        }else {
            minutoString= String.valueOf(minuto);
        }

        int horaActualInteger=Integer.parseInt(String.valueOf(hora)+minutoString);

        try{
            String horario1InicioString=destino.gethorario1_inicio().replace(":","");
            String horario1FinString=destino.gethorario1_fin().replace(":","");
            int horario2InicioInteger = 0;
            int horario2FinInteger=0;
            if (destino.gethorario2_incio()!=null&&destino.gethorario2_fin()!=null){
                String horario2InicioString=destino.gethorario2_incio().replace(":","");
                String horario2FinString=destino.gethorario2_fin().replace(":","");
                horario2InicioInteger=Integer.valueOf(horario2InicioString);
                horario2FinInteger=Integer.valueOf(horario2FinString);
            }

            int horario1InicioInteger=Integer.valueOf(horario1InicioString);
            int horario1FinInteger=Integer.valueOf(horario1FinString);


            if (!((horaActualInteger>horario1InicioInteger&&horaActualInteger<horario1FinInteger)||(horaActualInteger>horario2InicioInteger&&horaActualInteger<horario2FinInteger))){
                holder.cardViewTransporte.setCardBackgroundColor(0x5Ffff400);
            }
        }catch (NullPointerException e){
        }catch (NumberFormatException e1){
        }



        if (destino.getAgregadoDurRecorrido() != null && destino.getAgregadoDurRecorrido()) {
            holder.transporteTv.append(" (+)");
            holder.cardViewTransporte.setCardBackgroundColor(0x6672AFFF);
        }
        if (destino.getEntregado()) {
            holder.cardViewTransporte.setCardBackgroundColor(0xFFDAF7A6);
            holder.irBt.setVisibility(View.GONE);
            holder.progressBar.setVisibility(View.GONE);
            holder.distanciaListaDestinosTv.setVisibility(View.GONE);
            holder.switchAdapterDestinos.setVisibility(View.GONE);
        } else if (destino.getCancelado()) {
            holder.cardViewTransporte.setCardBackgroundColor(0x44FF4000);
            holder.irBt.setVisibility(View.GONE);
            holder.progressBar.setVisibility(View.GONE);
            holder.distanciaListaDestinosTv.setVisibility(View.GONE);
            holder.switchAdapterDestinos.setVisibility(View.VISIBLE);
        } else {
            holder.irBt.setVisibility(View.VISIBLE);
            holder.progressBar.setVisibility(View.VISIBLE);
            holder.distanciaListaDestinosTv.setVisibility(View.VISIBLE);
            holder.switchAdapterDestinos.setVisibility(View.VISIBLE);

        }

        Boolean pedido=false,sobre=false,publicidad=false;
        ArrayList<Integer> idsTiposRegistro=destino.getIds_tipos_registro();
        try{
            for (int i=0;i<idsTiposRegistro.size();i++){
                if (idsTiposRegistro.get(i)==1){
                    pedido=true;
                }else if(idsTiposRegistro.get(i)==2){
                    sobre=true;
                }else if (idsTiposRegistro.get(i)==3){
                    publicidad=true;
                }
            }
            if(pedido||publicidad){
                holder.tipoImagenIv.setImageResource(R.drawable.delivery);
            }else if (sobre){
                holder.tipoImagenIv.setImageResource(R.drawable.send);
            }
            if ((pedido||publicidad)&&sobre){
                holder.tipoImagenIv.setImageResource(R.drawable.sobrecaja);
            }
        }catch (NullPointerException e){
            SingleToast.show(mCtx,e.toString(),1);
        }


        holder.switchAdapterDestinos.setChecked(destino.getCancelado());

        holder.setOnClickListeners();

    }

    @Override
    public int getItemCount() {
        ArrayList<Destinos> destinos = almacenDestinos.getArrayList("arrayDestinosKey");
        ArrayList<Destinos> destinosFiltrados=new ArrayList<>();
        for (int i = 0 ; i<destinos.size();i++){
            Destinos destino = destinos.get(i);
            Boolean entregado,cancelado,agregado;
            entregado=destino.getEntregado();
            cancelado=destino.getCancelado();
            agregado=destino.getAgregadoDurRecorrido();

            CheckBox cumplidoCb,agregadoCb,canceladoCb,pendienteCb;

            cumplidoCb=((Activity)mCtx).findViewById(R.id.cumplidosCb);
            agregadoCb=((Activity)mCtx).findViewById(R.id.agregadosCb);
            canceladoCb=((Activity)mCtx).findViewById(R.id.canceladosCb);
            pendienteCb=((Activity)mCtx).findViewById(R.id.pendienteCb);

            Boolean cumplidoCbBool=cumplidoCb.isChecked();
            Boolean agregadoCbBool=agregadoCb.isChecked();
            Boolean canceladoCbBool =canceladoCb.isChecked();
            Boolean pendienteCbBool =pendienteCb.isChecked();

            if ((entregado&&cumplidoCbBool) || (!entregado&&pendienteCbBool&&!cancelado) || (cancelado&&canceladoCbBool) ||(agregado&&agregadoCbBool&&!cancelado)) {
                destinosFiltrados.add(destino);
            }

        }
        almacenDestinos.saveArrayDestinosFiltrados(destinosFiltrados);



        int itemCount=0;
        if (almacenDestinos.getArrayDestinosFiltrados()!=null){
            itemCount = almacenDestinos.getArrayDestinosFiltrados().size();
        }
        return itemCount;
    }


    public class ListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView tipoImagenIv;
        TextView transporteTv, dirTransporteTv, horariosCardTv, cantidadTv,codigoClienteTv,distanciaListaDestinosTv;
        CardView cardViewTransporte;
        ProgressBar progressBar;
        Button irBt;
        View divider2;

        Switch switchAdapterDestinos;

        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            transporteTv = itemView.findViewById(R.id.usuarioCardTv);
            dirTransporteTv = itemView.findViewById(R.id.dirTransporteListaTv);
            horariosCardTv = itemView.findViewById(R.id.horariosCardTv);
            cantidadTv = itemView.findViewById(R.id.texview32);
            cardViewTransporte = itemView.findViewById(R.id.cardviewTransporte);
            codigoClienteTv=itemView.findViewById(R.id.codigoClienteListaTv);
            irBt=itemView.findViewById(R.id.irBt);
            tipoImagenIv=itemView.findViewById(R.id.tipoImagenIv);
            progressBar=itemView.findViewById(R.id.progressBarListaDestinos);
            distanciaListaDestinosTv=itemView.findViewById(R.id.distanciaListaDestinosTv);
            switchAdapterDestinos=itemView.findViewById(R.id.switchAdapterDestino);
            divider2=itemView.findViewById(R.id.divider2);

        }

        public void setOnClickListeners() {
            cardViewTransporte.setOnClickListener(this);
            cardViewTransporte.setOnLongClickListener(onLongClickListener);
            irBt.setOnClickListener(this);
            switchAdapterDestinos.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.cardviewTransporte:
                    Intent intent = new Intent(mCtx,TransporteInfo.class);
                    intent.putExtra("idDestino",Integer.parseInt(codigoClienteTv.getText().toString()));
                    mCtx.startActivity(intent);
                    break;
                case R.id.irBt:
                    String latDest = null,lngDest = null;
                    int id=Integer.parseInt(codigoClienteTv.getText().toString());
                    ArrayList<Destinos> destinos = almacenDestinos.getArrayList("arrayDestinosKey");
                    for(int i=0;i<destinos.size();i++){
                        if (destinos.get(i).getId()==id){
                            latDest=String.valueOf(destinos.get(i).getLatitude());
                            lngDest=String.valueOf(destinos.get(i).getLongitude());
                        }
                    }
                    Uri uri = Uri.parse("https://www.google.com/maps/dir/?api=1&origin="+almacenDestinos.getLat()+","+almacenDestinos.getLng()+"&destination="+latDest+","+lngDest+"&sensor=false&mode=driving");
                    Intent intent2 = new Intent(Intent.ACTION_VIEW, uri);
                    mCtx.startActivity(intent2);
                    break;
                case R.id.switchAdapterDestino:

                    int id2=Integer.parseInt(codigoClienteTv.getText().toString());
                    int h=0;

                    ArrayList<Destinos> destinos4 = almacenDestinos.getArrayList("arrayDestinosKey");
                    for(int i=0;i<destinos4.size();i++){
                        if (destinos4.get(i).getId()==id2){
                            h=i;
                        }
                    }
                    if (switchAdapterDestinos.isChecked()) {
                        TaskCancelarDestino taskCancelarDestino = new TaskCancelarDestino(mCtx,destinos4,h);
                        taskCancelarDestino.execute(codigoClienteTv.getText().toString(),String.valueOf(1));
                    } else {
                        TaskCancelarDestino taskCancelarDestino = new TaskCancelarDestino(mCtx,destinos4,h);
                        taskCancelarDestino.execute(codigoClienteTv.getText().toString(),String.valueOf(0));
                    }
                    break;
            }
        }


        View.OnLongClickListener onLongClickListener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                switch (v.getId()){
                    case R.id.cardviewTransporte:

                        int id=Integer.parseInt(codigoClienteTv.getText().toString());

                        ArrayList<Destinos> destinos3 = almacenDestinos.getArrayList("arrayDestinosKey");
                        for(int i=0;i<destinos3.size();i++){
                            if (destinos3.get(i).getId()==id&&(!destinos3.get(i).getEntregado())&&(!destinos3.get(i).getCancelado())){
                                TaskConsultarQrCode taskConsultarQrCode = new TaskConsultarQrCode(mCtx,destinos3,i);
                                taskConsultarQrCode.execute(String.valueOf(destinos3.get(i).getId_externo()),String.valueOf(destinos3.get(i).getId_tipo_registro()),"1",String.valueOf(destinos3.get(i).getId()));
                            }
                        }
                        break;
                }
                return true;
            }
        };
    }
}

