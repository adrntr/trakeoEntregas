package com.example.ingeniera.trakeoentregas;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public class ListaDestinos extends AppCompatActivity {


    RecyclerView recyclerView;
    ListDestinosAdapter listDestinosAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_layout);

        recyclerView=findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        listDestinosAdapter = new ListDestinosAdapter(this);
        recyclerView.setAdapter(listDestinosAdapter);
    }
}
