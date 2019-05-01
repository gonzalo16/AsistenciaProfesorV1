package com.example.usuario.asistenciaprofesorv1.modelo;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.example.usuario.asistenciaprofesorv1.R;
import com.example.usuario.asistenciaprofesorv1.adaptadores.AdminAdapter;
import com.example.usuario.asistenciaprofesorv1.utilidades.Administracion;

import java.util.ArrayList;

public class VentanaAdmin extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private AdminAdapter adminAdapter;
    private ArrayList<Administracion> administracionArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ventana_admin);

        toolbar=findViewById(R.id.toolbar);
        recyclerView=findViewById(R.id.recyclerAdmin);

        toolbar.setTitle("Administracion");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        administracionArrayList=new ArrayList<Administracion>();
        administracionArrayList.add(new Administracion("Guardias",R.drawable.guardias));
        administracionArrayList.add(new Administracion("Profesores",R.drawable.profesores));
        adminAdapter=new AdminAdapter(getApplicationContext(),administracionArrayList,"Admin");
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        recyclerView.setAdapter(adminAdapter);
    }
}
