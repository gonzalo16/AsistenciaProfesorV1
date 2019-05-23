package com.example.usuario.asistenciaprofesorv1.modelo;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.usuario.asistenciaprofesorv1.MapasFragment;
import com.example.usuario.asistenciaprofesorv1.adaptadores.AdminAdapter;

import com.example.usuario.asistenciaprofesorv1.R;
import com.example.usuario.asistenciaprofesorv1.entidades.Asistencia;
import com.example.usuario.asistenciaprofesorv1.entidades.Guardia;
import com.example.usuario.asistenciaprofesorv1.entidades.Usuario;
import com.example.usuario.asistenciaprofesorv1.utilidades.Administracion;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class VentanaProfesor extends AppCompatActivity implements MapasFragment.OnFragmentInteractionListener{

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
    private final int REQUEST_LOCATION=1;

    private BottomNavigationView bottomNavigationView;
    public static float distancia=0;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    public static int guardiasRecibidas=0;
    private ArrayList<Guardia> guardias;
    private boolean nuevaGuardia=false;
    private int index=0;
    public static boolean permiso=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ventana_profesor);

        recyclerView=findViewById(R.id.recyclerAdminProfe);
        toolbar=findViewById(R.id.toolbar);
        bottomNavigationView=findViewById(R.id.navigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.ubicacion:

                        //Fragment fragmento=new MapasFragment();
                        //getSupportFragmentManager().beginTransaction().replace(R.id.contenedor,fragmento).commit();


                        /*MapsActivity fragment=new MapsActivity();
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.contenedor,fragment);
                        transaction.commit();*/
                        break;
                }
                return false;
            }
        });

        toolbar.setTitle("Administracion");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        guardias=new ArrayList<Guardia>();

        FirebaseApp.initializeApp(this);
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
        locSanJose.setLatitude(36.733635);
        locSanJose.setLongitude(-4.414539);

        //Iniciamos el servicio
        locationManager=(LocationManager) getSystemService(Context.LOCATION_SERVICE);

        /**
         * Instanciamos el objeto de asistencia
         */

        administracionArrayList=new ArrayList<Administracion>();

        generarDatos();
        //obtenerGuardias();
        checkPermission();

    }


    private boolean isServiceOk(){
        int available= GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(VentanaProfesor.this);

        if(available== ConnectionResult.SUCCESS){
            Log.d("Map","Servicio OKEY");
            return true;
        }else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            Log.d("Map","Servicio OKEY:error ");
        }
        return false;
    }



    private void obtenerGuardias(){
        databaseReference.child("Guardia").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                guardias.clear();
                if(nuevaGuardia==true){
                    for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                        Guardia guardia=dataSnapshot1.getValue(Guardia.class);
                        guardias.add(guardia);
                    }

                    Guardia ultimaGuardia=guardias.get(guardias.size()-1);
                    if(ultimaGuardia.getUidUsuario().equals(usuario.getUid())){
                        guardiasRecibidas++;
                    }

                    adapter.setGuardiasRecibidas(guardiasRecibidas);
                    adapter.notifyDataSetChanged();


                }else{
                    for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                        Guardia guardia=dataSnapshot1.getValue(Guardia.class);
                        guardias.add(guardia);
                    }
                    nuevaGuardia=true;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void generarDatos(){
        administracionArrayList.add(new Administracion("Guardias",R.drawable.guardias));
        administracionArrayList.add(new Administracion("Picar",R.drawable.ic_touch_app_black_24dp));
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


    private void checkPermission(){
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this,"Ya tienes garantizados los permisos",Toast.LENGTH_LONG);
            permiso=true;
        }else{
            requestLocationPermission();
        }
    }

    public void requestLocationPermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
            new AlertDialog.Builder(this).setTitle("Permiso denegado").setMessage("Se necesitan permisos de ubicacion").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    ActivityCompat.requestPermissions(VentanaProfesor.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_LOCATION);
                }
            })
            .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            })
            .create().show();
        }else{
            ActivityCompat.requestPermissions(VentanaProfesor.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==REQUEST_LOCATION){
            if(grantResults.length>0&&  grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"Permisos garantizados",Toast.LENGTH_LONG).show();
                permiso=true;
                activarGPS();
            }else{
                Toast.makeText(this,"Permisos denegados",Toast.LENGTH_LONG).show();
            }
        }
    }

    private void activarGPS(){
        int permissionCheck= ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION);

        //Si el permiso esta denegado, si no tenemos permiso
        if(permissionCheck==PackageManager.PERMISSION_DENIED){

            //Preguntamos
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){

            }else{
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            }
        }

        if(permissionCheck==PackageManager.PERMISSION_GRANTED){
            locationListener=new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    distancia=location.distanceTo(locSanJose);
                    String test="asfaw";
                    if(distancia<10){
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

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
