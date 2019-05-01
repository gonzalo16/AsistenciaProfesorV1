package com.example.usuario.asistenciaprofesorv1.modelo;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.example.usuario.asistenciaprofesorv1.R;
import com.example.usuario.asistenciaprofesorv1.adaptadores.GuardiaAdapter;
import com.example.usuario.asistenciaprofesorv1.adaptadores.ProfesorSpinAdapter;
import com.example.usuario.asistenciaprofesorv1.entidades.Guardia;
import com.example.usuario.asistenciaprofesorv1.entidades.Usuario;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class VentanaGuardias extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private ArrayList<Guardia> guardias;
    private Usuario usuario;
    private GuardiaAdapter adapter;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ventana_guardias);

        toolbar=findViewById(R.id.toolbar);
        toolbar.setTitle("Administracion");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        recyclerView=findViewById(R.id.recyclerGuardias);
        guardias=new ArrayList<Guardia>();
        FirebaseApp.initializeApp(this);
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference();
        usuario=(Usuario)getIntent().getExtras().getSerializable("Usuario");
        obtenerGuardias();

    }

    private void obtenerGuardias(){
        databaseReference.child("Guardia").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                guardias.clear();
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    Guardia guardia=dataSnapshot1.getValue(Guardia.class);
                    if(guardia.getUsuario().getUid().equals(usuario.getUid())){
                        guardias.add(guardia);
                    }
                }
                adapter=new GuardiaAdapter(getApplicationContext(),guardias);
                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false));
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
