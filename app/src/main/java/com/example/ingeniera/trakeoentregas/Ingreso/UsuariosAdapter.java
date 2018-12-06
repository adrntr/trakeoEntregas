package com.example.ingeniera.trakeoentregas.Ingreso;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ingeniera.trakeoentregas.Destino.Usuarios;
import com.example.ingeniera.trakeoentregas.R;

import java.util.ArrayList;

import static com.example.ingeniera.trakeoentregas.Ingreso.SolicitarDestinos.almacenDestinos;

public class UsuariosAdapter extends RecyclerView.Adapter<UsuariosAdapter.ListViewHolder> {

    private Context mCtx;//para pasarle las vistas

    public UsuariosAdapter(Context mCtx) {
        this.mCtx = mCtx;
    }


    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.card_usuarios, null);
        ListViewHolder holder = new ListViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
        ArrayList<Usuarios> usuarios = almacenDestinos.getArrayUsuarios("arrayUsuariosKey");
        Usuarios usuario = usuarios.get(position);

        if (usuario.getTipo().equals("Responsable")){
            holder.usuarioCardTv.setTextSize(20);
            holder.usuarioCardTv.setTypeface(null, Typeface.BOLD);
            holder.usuarioIconIv.setImageResource(R.drawable.responsable);
        }else {
            holder.usuarioCardTv.setTextSize(18);
            holder.usuarioCardTv.setTypeface(null, Typeface.NORMAL);
            holder.usuarioIconIv.setImageResource(R.drawable.acompanante);
        }
        holder.usuarioCardTv.setText(String.valueOf(usuario.getNombre()));


        holder.setOnClickListeners();
    }


    @Override
    public int getItemCount() {
        int itemCount = almacenDestinos.getArrayUsuarios("arrayUsuariosKey").size();
        return itemCount;
    }


    public class ListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        TextView usuarioCardTv;
        CardView usuariosCv;
        ImageView usuarioIconIv;

        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            usuarioCardTv = itemView.findViewById(R.id.usuarioCardTv);
            usuariosCv = itemView.findViewById(R.id.usuariosCv);
            usuarioIconIv=itemView.findViewById(R.id.UsuarioIconIv);
        }

        public void setOnClickListeners() {
            usuariosCv.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.usuariosCv:
                    final String usuarioTv = usuarioCardTv.getText().toString();

                    final ArrayList<Usuarios> usuarios = almacenDestinos.getArrayUsuarios("arrayUsuariosKey");
                    for (int i = 0; i < usuarios.size(); i++) {
                        if (usuarioTv.equals(usuarios.get(i).getNombre())) {
                            final int j= i;
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mCtx);
                            alertDialogBuilder
                                    .setTitle("Eliminar a "+usuarioTv)
                                    .setCancelable(false)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            if(usuarios.get(j).getTipo().equals("Responsable")){
                                                usuarios.remove(j);
                                                if (usuarios.size()!=0){
                                                    usuarios.get(0).setTipo("Responsable");
                                                }
                                            }else {
                                                usuarios.remove(j);
                                            }

                                            almacenDestinos.setArrayUsuarios(usuarios);
                                            if(usuarios.size()==0){
                                                almacenDestinos.setEstadoRuta(0);
                                                ((Activity)mCtx).recreate();
                                            }else{
                                                RecyclerView recyclerViewUsuarios=((Activity)mCtx).findViewById(R.id.recyclerViewUsuarios);
                                                recyclerViewUsuarios.getAdapter().notifyDataSetChanged();
                                            }

                                        }
                                    })
                                    .setNegativeButton("Cancel",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    dialog.cancel();

                                                }
                                            });
                            // create alert dialog
                            AlertDialog alertDialog = alertDialogBuilder.create();
                            // show it
                            alertDialog.show();
                        }
                    }


            //Toast.makeText(mCtx, usuarioCardTv.getText().toString(), Toast.LENGTH_SHORT).show();
            //TaskObtenerDatosRuta taskObtenerDatosRuta = new TaskObtenerDatosRuta(mCtx);
            //taskObtenerDatosRuta.execute("id_ruta",idHojaDeRutaTv.getText().toString());
            break;
            }

        }
    }
}

