package com.example.usuario.asistenciaprofesorv1.modelo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.example.usuario.asistenciaprofesorv1.R;

public class VentanaProfesor extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ventana_profesor);
    }
}
