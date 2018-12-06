package com.example.ingeniera.trakeoentregas.Ingreso;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ingeniera.trakeoentregas.AlmacenDestinos;
import com.example.ingeniera.trakeoentregas.R;
import com.example.ingeniera.trakeoentregas.Destino.TaskObtenerDatosRuta;

import java.util.ArrayList;

import static com.example.ingeniera.trakeoentregas.Ingreso.SolicitarDestinos.almacenDestinos;

public class HojasDeRutaAdapter extends RecyclerView.Adapter<HojasDeRutaAdapter.ListViewHolder> {

    private Context mCtx;//para pasarle las vistas

    public HojasDeRutaAdapter(Context mCtx) {
        this.mCtx = mCtx;
    }


    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.card_hojas_de_ruta, null);
        ListViewHolder holder = new ListViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
        ArrayList<HojasDeRuta> hojasDeRutas = almacenDestinos.getArrayHojasDeRutas("arrayHojasDeRutaKey");
        HojasDeRuta hojaDeRuta = hojasDeRutas.get(position);

        holder.idHojaDeRutaTv.setText(String.valueOf(hojaDeRuta.getCodigo()));
        holder.creadoTv.setText(hojaDeRuta.getFecha());

        holder.setOnClickListeners();
    }


    @Override
    public int getItemCount() {
        int itemCount = almacenDestinos.getArrayHojasDeRutas("arrayHojasDeRutaKey").size();
        return itemCount;
    }


    public class ListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        TextView idHojaDeRutaTv,creadoTv,cantidadDestinosTv;
        CardView hojasDeRutaCv;

        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            idHojaDeRutaTv = itemView.findViewById(R.id.IdHojaDeRutaTv);
            creadoTv = itemView.findViewById(R.id.creadoTv);
            cantidadDestinosTv = itemView.findViewById(R.id.cantidadDestinosTv);
            hojasDeRutaCv=itemView.findViewById(R.id.HojasDeRutaCv);


        }

        public void setOnClickListeners() {
            hojasDeRutaCv.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.HojasDeRutaCv:
                    TaskObtenerDatosRuta taskObtenerDatosRuta = new TaskObtenerDatosRuta(mCtx);
                    taskObtenerDatosRuta.execute(idHojaDeRutaTv.getText().toString());
                    break;
            }
        }
    }
}

