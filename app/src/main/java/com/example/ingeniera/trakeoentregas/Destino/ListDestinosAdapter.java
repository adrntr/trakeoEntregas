package com.example.ingeniera.trakeoentregas.Destino;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ingeniera.trakeoentregas.R;
import com.example.ingeniera.trakeoentregas.SingleToast;
import com.example.ingeniera.trakeoentregas.TransporteInfo;

import java.util.ArrayList;

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

        ArrayList<Destinos> destinos = almacenDestinos.getArrayList("arrayDestinosKey");
        Destinos destino = destinos.get(position);

        holder.transporteTv.setText(destino.getNombre_transporte());
        holder.dirTransporteTv.setText(destino.getDireccion());
        holder.clienteTv.setText(String.valueOf(destino.getNombre_cliente()));
        holder.cantidadTv.setText("Cantidad: "+String.valueOf(destino.getCantidad()));
        holder.codigoClienteTv.setText(String.valueOf(destino.getId()));
        String lat=almacenDestinos.getLat();
        if (almacenDestinos.getLat()!=""){
            Double difLat=destino.getLatitude()-Double.parseDouble(almacenDestinos.getLat());
            Double difLng=destino.getLongitude()-Double.parseDouble(almacenDestinos.getLng());
            Double dist=Math.sqrt(Math.pow(difLat,2)+Math.pow(difLng,2));
            holder.distanciaListaDestinosTv.setText("Dist: "+String.valueOf(Math.round(dist*10000.0)/100.0)+" Km");
            holder.progressBar.setMax(100);
            Double porcentaje=dist*100/0.3;
            if (porcentaje>100){
                porcentaje=100.0;
            }
            holder.progressBar.setProgress((int)(100.0-porcentaje));
        }

        if (destino.getAgregadoDurRecorrido()!=null&&destino.getAgregadoDurRecorrido()){
            holder.transporteTv.append(" (Agregado)");
        }

        if (destino.getEntregado()) {
            holder.cardViewTransporte.setCardBackgroundColor(0xFFDAF7A6);
            holder.irBt.setVisibility(View.GONE);
            holder.progressBar.setVisibility(View.GONE);
            holder.distanciaListaDestinosTv.setVisibility(View.GONE);
            holder.switchAdapterDestinos.setVisibility(View.GONE);
        }else if (destino.getCancelado()){
            holder.cardViewTransporte.setCardBackgroundColor(0x44FF4000);
            holder.irBt.setVisibility(View.GONE);
            holder.progressBar.setVisibility(View.GONE);
            holder.distanciaListaDestinosTv.setVisibility(View.GONE);
            holder.switchAdapterDestinos.setVisibility(View.VISIBLE);
        }else{
            holder.cardViewTransporte.setCardBackgroundColor(0xFFFFFFFF);
            holder.irBt.setVisibility(View.VISIBLE);
            holder.progressBar.setVisibility(View.VISIBLE);
            holder.distanciaListaDestinosTv.setVisibility(View.VISIBLE);
            holder.switchAdapterDestinos.setVisibility(View.VISIBLE);
        }
        switch (destino.getId_tipo_registro()) {
            case 1:
                holder.tipoImagenIv.setImageResource(R.drawable.delivery);
                break;
            case 2:
                holder.tipoImagenIv.setImageResource(R.drawable.send);
                break;
        }
        holder.switchAdapterDestinos.setChecked(destino.getCancelado());

        holder.setOnClickListeners();

    }

    @Override
    public int getItemCount() {
        int itemCount = almacenDestinos.getArrayList("arrayDestinosKey").size();
        return itemCount;
    }


    public class ListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView tipoImagenIv;
        TextView transporteTv, dirTransporteTv, clienteTv, cantidadTv,codigoClienteTv,distanciaListaDestinosTv;
        CardView cardViewTransporte;
        ProgressBar progressBar;
        Button irBt;

        Switch switchAdapterDestinos;

        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            transporteTv = itemView.findViewById(R.id.usuarioCardTv);
            dirTransporteTv = itemView.findViewById(R.id.dirTransporteListaTv);
            clienteTv = itemView.findViewById(R.id.textview34);
            cantidadTv = itemView.findViewById(R.id.texview32);
            cardViewTransporte = itemView.findViewById(R.id.cardviewTransporte);
            codigoClienteTv=itemView.findViewById(R.id.codigoClienteListaTv);
            irBt=itemView.findViewById(R.id.irBt);
            tipoImagenIv=itemView.findViewById(R.id.tipoImagenIv);
            progressBar=itemView.findViewById(R.id.progressBarListaDestinos);
            distanciaListaDestinosTv=itemView.findViewById(R.id.distanciaListaDestinosTv);
            switchAdapterDestinos=itemView.findViewById(R.id.switchAdapterDestino);

        }

        public void setOnClickListeners() {
            cardViewTransporte.setOnClickListener(this);
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
                    if (switchAdapterDestinos.isChecked()) {
                        SingleToast.show(mCtx, "ON"+codigoClienteTv.getText().toString(), Toast.LENGTH_SHORT);
                        TaskCancelarDestino taskCancelarDestino = new TaskCancelarDestino(mCtx);
                        taskCancelarDestino.execute(codigoClienteTv.getText().toString(),String.valueOf(1));
                    } else {
                        SingleToast.show(mCtx, "OFF"+codigoClienteTv.getText().toString(), Toast.LENGTH_SHORT);
                        TaskCancelarDestino taskCancelarDestino = new TaskCancelarDestino(mCtx);
                        taskCancelarDestino.execute(codigoClienteTv.getText().toString(),String.valueOf(0));
                    }
                    break;
            }
        }
    }
}

