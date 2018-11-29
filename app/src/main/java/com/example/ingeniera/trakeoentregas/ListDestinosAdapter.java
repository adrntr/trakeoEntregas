package com.example.ingeniera.trakeoentregas;

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
import android.widget.TextView;

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
        holder.dirTransporteTv.setText(destino.getDireccion_transporte());
        holder.clienteTv.setText(String.valueOf(destino.getNombre_cliente()));
        holder.cantidadTv.setText("Cantidad: "+String.valueOf(destino.getCantidad()));
        holder.codigoClienteTv.setText(String.valueOf(destino.getId()));
        if (destino.getEntregado()) {
            holder.cardViewTransporte.setCardBackgroundColor(0xFF00FF00);
        }
        holder.setOnClickListeners();
    }

    @Override
    public int getItemCount() {
        int itemCount = almacenDestinos.getArrayList("arrayDestinosKey").size();
        return itemCount;
    }


    public class ListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView tipoEnvioIv;
        TextView transporteTv, dirTransporteTv, clienteTv, cantidadTv,codigoClienteTv;
        CardView cardViewTransporte;
        Button irBt;

        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            transporteTv = itemView.findViewById(R.id.transporteListaTv);
            dirTransporteTv = itemView.findViewById(R.id.dirTransporteListaTv);
            clienteTv = itemView.findViewById(R.id.textview34);
            cantidadTv = itemView.findViewById(R.id.texview32);
            cardViewTransporte = itemView.findViewById(R.id.cardviewTransporte);
            codigoClienteTv=itemView.findViewById(R.id.codigoClienteListaTv);
            irBt=itemView.findViewById(R.id.irBt);

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

