package com.example.usuario.asistenciaprofesorv1.modelo;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.example.usuario.asistenciaprofesorv1.R;
import com.example.usuario.asistenciaprofesorv1.adaptadores.GuardiaAdapter;
import com.example.usuario.asistenciaprofesorv1.control.OpeGuardias;
import com.example.usuario.asistenciaprofesorv1.entidades.Guardia;
import com.example.usuario.asistenciaprofesorv1.entidades.Usuario;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

public class VentanaGuardias extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private ArrayList<Guardia> guardias;
    private Usuario usuario;
    private GuardiaAdapter adapter;

    private FirebaseUser fuser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private OpeGuardias opeGuardias;

    private ArrayList<Long> distanciaHoras;

    private final static int NOTIFICATION_ID=0;
    private int guardiasRecibidas=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ventana_guardias);

        toolbar=findViewById(R.id.toolbar);
        recyclerView=findViewById(R.id.recyclerGuardias);

        toolbar.setTitle("Guardias");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        usuario=(Usuario)getIntent().getExtras().getSerializable("Usuario");

        guardias=new ArrayList<Guardia>();
        opeGuardias=new OpeGuardias(getApplicationContext(),usuario,recyclerView);
        //FirebaseApp.initializeApp(this);
        //firebaseDatabase=FirebaseDatabase.getInstance();
        //databaseReference=firebaseDatabase.getReference();
        fuser= FirebaseAuth.getInstance().getCurrentUser();

        distanciaHoras=new ArrayList<Long>();

        obtenerGuardias();

    }


    private long calcularHora(Date horaInicio,Date horaFin){
        long milisegundos=horaInicio.getTime()-horaFin.getTime();
        return milisegundos;
    }

    private void obtenerGuardias(){
        opeGuardias.obtenerGuardias("Guardia");
        /*databaseReference.child("Guardia").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                guardias.clear();
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    Guardia guardia=dataSnapshot1.getValue(Guardia.class);
                    if(guardia.getUidUsuario().equals(usuario.getUid())){
                        guardias.add(guardia);
                        //distanciaHoras.add(calcularHora(guardia.getHoraInicio(),guardia.getHoraFin()));
                    }
                }

                adapter=new GuardiaAdapter(VentanaGuardias.this,guardias);
                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false));
                recyclerView.setAdapter(adapter);
                guardiasRecibidas++;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/
    }
}
