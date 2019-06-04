package com.example.usuario.asistenciaprofesorv1.modelo;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.usuario.asistenciaprofesorv1.MapasLocalizacion;
import com.example.usuario.asistenciaprofesorv1.R;
import com.example.usuario.asistenciaprofesorv1.adaptadores.ProfesorSpinAdapter;
import com.example.usuario.asistenciaprofesorv1.entidades.Localizacion;
import com.example.usuario.asistenciaprofesorv1.entidades.Usuario;
import com.example.usuario.asistenciaprofesorv1.utilidades.FirebaseHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class VentanaLocalizacion extends AppCompatActivity implements View.OnClickListener, MapasLocalizacion.OnFragmentInteractionListener{

    private Spinner spin;
    private ArrayList<Usuario> profesores;
    private Usuario usuario;
    private Localizacion localizacion;

    private Toolbar toolbar;

    //BBDD references firebase
    private FirebaseHelper firebaseHelper;

    //Button
    private Button button;

    //Constantes
    private static int REQUEST_LOCATION=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ventana_localizacion);

        toolbar=findViewById(R.id.toolbar);
        toolbar.setTitle("Obtener localizacion");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        firebaseHelper=new FirebaseHelper();

        spin=findViewById(R.id.spinLocProfesor);
        button=findViewById(R.id.btnLocalizacion);

        profesores=new ArrayList<Usuario>();
        cargarProfesores();

        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                usuario=(Usuario) adapterView.getItemAtPosition(position);
                obtenerLocalizacion();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        button.setOnClickListener(this);
    }

    private boolean chekearPermiso(){
        boolean permission=false;

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this,"Ya tienes garantizados los permisos",Toast.LENGTH_LONG);
            permission=true;
        }else{
            requestLocationPermission();
        }

        return permission;
    }

    private void obtenerLocalizacion(){
        firebaseHelper.getDatabaseReference().child("Localizacion").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    Localizacion loc=ds.getValue(Localizacion.class);
                    if(loc.getUidUsuario().equals(usuario.getUid())){
                        localizacion=loc;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void requestLocationPermission() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
            new AlertDialog.Builder(this).setTitle("Permiso denegado").setMessage("Se necesitan permisos de ubicacion").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    ActivityCompat.requestPermissions(VentanaLocalizacion.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_LOCATION);
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
            ActivityCompat.requestPermissions(VentanaLocalizacion.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==REQUEST_LOCATION){
            if(grantResults.length>0&&  grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"Permisos garantizados",Toast.LENGTH_LONG).show();
                if(localizacion.getLatitud()==0&&localizacion.getLongitud()==0){
                    Toast.makeText(getApplicationContext(),"El profesor no tiene una ubicacion",Toast.LENGTH_LONG).show();
                }else{
                    Intent i=new Intent(VentanaLocalizacion.this,MapasLocalizacion.class);
                    i.putExtra("user",usuario);
                    startActivity(i);
                }
            }else{
                Toast.makeText(this,"Permisos denegados",Toast.LENGTH_LONG).show();
            }
        }
    }

    private void cargarProfesores(){
        firebaseHelper.getDatabaseReference().child("Usuario").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    Usuario u=ds.getValue(Usuario.class);
                    if(u.getPerfil().equals("Profesor")){
                        profesores.add(u);
                        spin.setAdapter(new ProfesorSpinAdapter(getApplicationContext(),profesores));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnLocalizacion:
                boolean permiso=chekearPermiso();
                obtenerLocalizacion();
                if(permiso==true){
                    if(localizacion!=null){
                        if(localizacion.getLatitud()==0&&localizacion.getLongitud()==0){
                            Toast.makeText(getApplicationContext(),"El profesor no tiene una ubicacion",Toast.LENGTH_LONG).show();
                        }else{
                            Intent i=new Intent(VentanaLocalizacion.this,MapasLocalizacion.class);
                            i.putExtra("user",usuario);
                            startActivity(i);
                        }
                    }

                }
                break;
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
