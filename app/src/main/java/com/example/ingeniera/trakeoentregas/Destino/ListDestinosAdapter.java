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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.ingeniera.trakeoentregas.R;
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
        Double difLat=destino.getLatitude()-Double.parseDouble(almacenDestinos.getLat());
        Double difLng=destino.getLongitude()-Double.parseDouble(almacenDestinos.getLng());
        Double dist=Math.sqrt(Math.pow(difLat,2)+Math.pow(difLng,2));
        holder.distanciaListaDestinosTv.setText("Dist: "+String.valueOf(Math.round(dist*10000.0)/100.0));
        holder.progressBar.setMax(100);
        if (dist>0.6){
            holder.progressBar.setProgress(0);
        }else if(dist>5&&dist<=0.6) {
            holder.progressBar.setProgress(10);
        } else if(dist>0.45&&dist<=0.5) {
        holder.progressBar.setProgress(20);
        }else if(dist>0.4&&dist<=0.45) {
            holder.progressBar.setProgress(30);
        }else if(dist>0.35&&dist<=0.4) {
            holder.progressBar.setProgress(40);
        }else if(dist>0.3&&dist<=0.35) {
            holder.progressBar.setProgress(50);
        }else if(dist>0.25&&dist<=0.3) {
            holder.progressBar.setProgress(60);
        }else if(dist>0.2&&dist<=0.25) {
            holder.progressBar.setProgress(70);
        }else if(dist>0.15&&dist<=0.2) {
            holder.progressBar.setProgress(80);
        }else if(dist>0.1&&dist<=0.15) {
            holder.progressBar.setProgress(90);
        }else if(dist>0.05&&dist<=0.1) {
            holder.progressBar.setProgress(100);
        }

        if (destino.getEntregado()) {
            holder.cardViewTransporte.setCardBackgroundColor(0xFFDAF7A6);
            holder.irBt.setVisibility(View.GONE);
            holder.progressBar.setVisibility(View.GONE);
            holder.distanciaListaDestinosTv.setVisibility(View.GONE);

        }else{
            holder.cardViewTransporte.setCardBackgroundColor(0xFFFFFFFF);
            holder.irBt.setVisibility(View.VISIBLE);
            holder.progressBar.setVisibility(View.VISIBLE);
            holder.distanciaListaDestinosTv.setVisibility(View.VISIBLE);
        }
        switch (destino.getId_tipo_registro()){
            case 1:
                holder.tipoImagenIv.setImageResource(R.drawable.delivery);
                break;
            case 2:
                holder.tipoImagenIv.setImageResource(R.drawable.send);
        }
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

        }

        public void setOnClickListeners() {
            cardViewTransporte.setOnClickListener(this);
            irBt.setOnClickListener(this);
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
            }
        }
    }
}

