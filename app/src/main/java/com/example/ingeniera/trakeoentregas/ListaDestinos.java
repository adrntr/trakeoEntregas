package com.example.ingeniera.trakeoentregas;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.ingeniera.trakeoentregas.Entregas.QrReader;
import com.example.ingeniera.trakeoentregas.Ingreso.SolicitarDestinos;

import static com.example.ingeniera.trakeoentregas.Ingreso.SolicitarDestinos.almacenDestinos;

public class ListaDestinos extends AppCompatActivity {


    private static final int QR_REQUEST = 600;
    RecyclerView recyclerView;
    ListDestinosAdapter listDestinosAdapter;
    Button irATodos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_layout);

        irATodos=findViewById(R.id.irATodosBt);
        irATodos.setOnClickListener(clicListener);
        recyclerView=findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        listDestinosAdapter = new ListDestinosAdapter(this);
        recyclerView.setAdapter(listDestinosAdapter);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Intent intent=new Intent(ListaDestinos.this,ListaDestinos.class);
        startActivity(intent);
        finish();
    }

    View.OnClickListener clicListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.irATodosBt:

                    DireccionesMapsApi direccionesMapsApi=new DireccionesMapsApi();
                    //ordenarArrayListDestinos();
                    direccionesMapsApi.getRequestedUrl(almacenDestinos.getArrayList("arrayDestinosKey"),false);
                    Uri uri = Uri.parse(almacenDestinos.getUrlGoogleMaps());
                    Intent intent2 = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent2);
                    break;
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.listaactivity_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        int id = item.getItemId();

        switch (id){

            case R.id.Terminar:
                almacenDestinos.setEstadoRuta(0);
                Intent intent=new Intent(ListaDestinos.this,SolicitarDestinos.class);
                startActivity(intent);
                finish();
                break;
            case R.id.LeerQR:
                Intent intent1=new Intent(ListaDestinos.this,QrReader.class);
                startActivityForResult(intent1,QR_REQUEST);
                break;
            case R.id.MapsApp:
                //almacenDestinos.setEstadoRuta(0);
                Intent intent2=new Intent(ListaDestinos.this,MapsActivity.class);
                startActivity(intent2);
                finish();

                break;
        }

        return super.onOptionsItemSelected(item);
    }

}
