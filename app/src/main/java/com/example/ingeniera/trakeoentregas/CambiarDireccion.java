package com.example.ingeniera.trakeoentregas;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class CambiarDireccion extends AppCompatActivity {

    private EditText calleCambiarEt,numeroCambiarEt,localidadCambiarEt,provinciaCambiarEt;
    private FloatingActionButton convertiDirBt;

    private int idDestino;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cambiar_direccion);

        calleCambiarEt=findViewById(R.id.calleCambiarEt);
        numeroCambiarEt=findViewById(R.id.numeroCambiarEt);
        localidadCambiarEt=findViewById(R.id.localidadCambiarEt);
        provinciaCambiarEt=findViewById(R.id.provinciaCambiarEt);
        convertiDirBt=findViewById(R.id.convertirDirBt);

        convertiDirBt.setOnClickListener(ClicListener);

        Bundle extras = getIntent().getExtras();
        idDestino=extras.getInt("id");
    }

    View.OnClickListener ClicListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.convertirDirBt:
                    String calle=calleCambiarEt.getText().toString();
                    String numero=numeroCambiarEt.getText().toString();
                    String localidad=localidadCambiarEt.getText().toString();
                    String provincia=provinciaCambiarEt.getText().toString();

                    if (calle.equals("")||numero.equals("")){
                        SingleToast.show(CambiarDireccion.this,"Coloque calle y número",Toast.LENGTH_SHORT);
                    }else{
                        TaskCambiarDireccion taskCambiarDireccion=new TaskCambiarDireccion(CambiarDireccion.this,idDestino);
                        taskCambiarDireccion.execute(calle+" "+numero + ", "+localidad+", "+provincia);
                    }
            }
        }
    };


}
