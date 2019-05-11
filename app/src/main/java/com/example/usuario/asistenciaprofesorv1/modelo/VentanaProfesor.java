package com.example.usuario.asistenciaprofesorv1.modelo;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.example.usuario.asistenciaprofesorv1.R;
import com.example.usuario.asistenciaprofesorv1.adaptadores.AdminAdapter;
import com.example.usuario.asistenciaprofesorv1.entidades.Asistencia;
import com.example.usuario.asistenciaprofesorv1.entidades.Guardia;
import com.example.usuario.asistenciaprofesorv1.entidades.Usuario;
import com.example.usuario.asistenciaprofesorv1.utilidades.Administracion;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class VentanaProfesor extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private AdminAdapter adapter;
    private ArrayList<Administracion> administracionArrayList;


    /*
    Usuario que se ha registrado
     */
    private Usuario usuario;
    private Asistencia asistencia;

    private Location locSanJose;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private final int REQUEST_ACCES_FINE=0;

    public static float distancia=0;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private int guardiasRecibidas=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ventana_profesor);

        recyclerView=findViewById(R.id.recyclerAdminProfe);
        toolbar=findViewById(R.id.toolbar);

        toolbar.setTitle("Administracion");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference();



        /**
         * Obtenemos desde el bundle el usuario que ha iniciado sesion
         */
        usuario=(Usuario)getIntent().getExtras().getSerializable("Usuario");

        /**
         * Establecemos la localizacion el colegio
         */
        locSanJose=new Location("");
        locSanJose.setLatitude(36.733296);
        locSanJose.setLongitude(-4.415612);

        //Iniciamos el servicio
        locationManager=(LocationManager) getSystemService(Context.LOCATION_SERVICE);

        /**
         * Instanciamos el objeto de asistencia
         */

        administracionArrayList=new ArrayList<Administracion>();

        obtenerGuardias();
        generarDatos();
        activarGPS();
    }

    private void obtenerGuardias(){
        databaseReference.child("Guardia").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                 Guardia guardia=dataSnapshot.getValue(Guardia.class);
                 if(guardia.getUsuario().getUid().equals(usuario.getUid())){
                     guardiasRecibidas++;
                 }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void generarDatos(){
        administracionArrayList.add(new Administracion("Guardias",R.drawable.guardias));
        administracionArrayList.add(new Administracion("Picar",R.drawable.ic_pan_tool_black_24dp));
        administracionArrayList.add(new Administracion("Agenda",R.drawable.agenda));
        administracionArrayList.add(new Administracion("Salir",R.drawable.salir));

        adapter=new AdminAdapter(getApplicationContext(),administracionArrayList,"Profesor",usuario);
        adapter.setGuardiasRecibidas(guardiasRecibidas);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            return;
        }else{
            locationManager.removeUpdates(locationListener);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        activarGPS();
    }

    private void activarGPS(){

        //Solicitamos el permiso
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_ACCES_FINE);
        }

        if(REQUEST_ACCES_FINE==PackageManager.PERMISSION_GRANTED){
            locationListener=new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    distancia=location.distanceTo(locSanJose);

                    if(distancia<200){
                        Toast.makeText(getApplicationContext(),"Estas en la zona",Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(getApplicationContext(),"No estas en la zona",Toast.LENGTH_LONG).show();
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
}
