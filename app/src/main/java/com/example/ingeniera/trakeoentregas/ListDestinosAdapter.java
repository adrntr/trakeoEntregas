package com.example.ingeniera.trakeoentregas;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import static com.example.ingeniera.trakeoentregas.SolicitarDestinos.almacenDestinos;

public class ListDestinosAdapter extends RecyclerView.Adapter<ListDestinosAdapter.ListViewHolder>{

    private Context mCtx;//para pasarle las vistas
    public ListDestinosAdapter(Context mCtx) {
        this.mCtx = mCtx;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(mCtx);
        View view=inflater.inflate(R.layout.activity_lista_destinos,null);
        ListViewHolder holder = new ListViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {

        ArrayList<Destinos> destinos=almacenDestinos.getArrayList("arrayDestinosKey");
        Destinos destino = destinos.get(position);

        holder.transporteTv.setText(destino.getTransporte());
        holder.dirTransporteTv.setText(destino.getDireccion_transporte());
        holder.clienteTv.setText(String.valueOf(destino.getNombre_cliente()));
        holder.cantidadTv.setText(String.valueOf(destino.getCantidadBultos()));
        //holder.tipoEnvioIv.setImageDrawable(mCtx.getResources().getDrawable(destino.getImage()));
    }

    @Override
    public int getItemCount() {
        return almacenDestinos.getArrayList("arrayDestinosKey").size();
    }

    public class ListViewHolder extends RecyclerView.ViewHolder {

        ImageView tipoEnvioIv;
        TextView transporteTv,dirTransporteTv,clienteTv,cantidadTv;
        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            tipoEnvioIv=itemView.findViewById(R.id.tipoTv);
            transporteTv=itemView.findViewById(R.id.transporteTv);
            dirTransporteTv=itemView.findViewById(R.id.direccionTv);
            clienteTv=itemView.findViewById(R.id.clienteTv);
            cantidadTv=itemView.findViewById(R.id.cantidadTv);

        }
    }
}
