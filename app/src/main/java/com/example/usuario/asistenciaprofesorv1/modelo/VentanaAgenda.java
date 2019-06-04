package com.example.usuario.asistenciaprofesorv1.modelo;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.example.usuario.asistenciaprofesorv1.R;
import com.example.usuario.asistenciaprofesorv1.control.OpeAsistencia;
import com.example.usuario.asistenciaprofesorv1.entidades.Usuario;

public class VentanaAgenda extends AppCompatActivity {

    private Toolbar toolbar;
    private Usuario usuario;
    private OpeAsistencia opeAsistencia;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ventana_agenda);

        toolbar=findViewById(R.id.toolbar);
        toolbar.setTitle("Asistencia");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        recyclerView=findViewById(R.id.recyclerAgenda);

        usuario=(Usuario)getIntent().getExtras().getSerializable("Usuario");
        opeAsistencia=new OpeAsistencia(getApplicationContext(),usuario,recyclerView);
        obtenerAsistencia();
    }

    private void obtenerAsistencia(){
        opeAsistencia.obtenerAsistencia("Asistencia");
    }
}
