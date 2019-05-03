package com.example.usuario.asistenciaprofesorv1.modelo;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.Toast;

import com.example.usuario.asistenciaprofesorv1.R;
import com.example.usuario.asistenciaprofesorv1.adaptadores.GuardiaAdapter;
import com.example.usuario.asistenciaprofesorv1.adaptadores.ProfesorSpinAdapter;
import com.example.usuario.asistenciaprofesorv1.entidades.Guardia;
import com.example.usuario.asistenciaprofesorv1.entidades.Usuario;
import com.example.usuario.asistenciaprofesorv1.notificaciones.Token;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;

public class VentanaGuardias extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private ArrayList<Guardia> guardias;
    private Usuario usuario;
    private GuardiaAdapter adapter;

    private FirebaseUser fuser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private Button botonPicar;
    private final static int NOTIFICATION_ID=0;
    private boolean tieneGuardias=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ventana_guardias);

        toolbar=findViewById(R.id.toolbar);
        botonPicar=findViewById(R.id.btnPicar);
        recyclerView=findViewById(R.id.recyclerGuardias);


        toolbar.setTitle("Administracion");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);


        activarGPS();


        guardias=new ArrayList<Guardia>();
        FirebaseApp.initializeApp(this);
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference();
        fuser= FirebaseAuth.getInstance().getCurrentUser();


        usuario=(Usuario)getIntent().getExtras().getSerializable("Usuario");
        obtenerGuardias();

    }

    private void activarGPS(){

        final Location locationColegio;
        LocationManager locationManager;
        LocationListener locationListener;

        //Asigamos la localizacion del colegio
        locationColegio=new Location("");
        locationColegio.setLatitude(36.733296);
        locationColegio.setLongitude(-4.415612);

        //Iniciamos el servicio
        locationManager=(LocationManager) getSystemService(Context.LOCATION_SERVICE);
        final int REQUEST_ACCES_FINE=0;

        //Solicitamos el permiso
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_ACCES_FINE);
        }

        if(REQUEST_ACCES_FINE==PackageManager.PERMISSION_GRANTED){
            locationListener=new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    float distancia=location.distanceTo(locationColegio);
                    if(distancia<50){
                        Toast.makeText(getApplicationContext(),"Estas en la zona",Toast.LENGTH_LONG).show();
                        botonPicar.setEnabled(true);
                    }else{
                        Toast.makeText(getApplicationContext(),"No estas en la zona",Toast.LENGTH_LONG).show();
                        botonPicar.setEnabled(false);
                    }
                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {

                }

                @Override
                public void onProviderEnabled(String s) {

                }

                @Override
                public void onProviderDisabled(String s) {

                }
            };
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0,locationListener);
        }

    }


    private void mostrarNotification(){

        NotificationCompat.Builder builder=new NotificationCompat.Builder(getApplicationContext());
        builder.setSmallIcon(R.drawable.ic_add_alert_black_24dp);
        builder.setContentTitle("Nueva guardia asignada");
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager=NotificationManagerCompat.from(getApplicationContext());
        notificationManager.notify(NOTIFICATION_ID,builder.build());
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

                if(guardias.size()>0){
                    adapter=new GuardiaAdapter(getApplicationContext(),guardias);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false));
                    recyclerView.setAdapter(adapter);
                    if(tieneGuardias==true){
                        mostrarNotification();

                    }
                    tieneGuardias=true;
                }else{
                    tieneGuardias=true;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
