package com.example.usuario.asistenciaprofesorv1;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.usuario.asistenciaprofesorv1.entidades.Localizacion;
import com.example.usuario.asistenciaprofesorv1.entidades.Usuario;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MapasLocalizacion extends FragmentActivity implements OnMapReadyCallback {

    private OnFragmentInteractionListener mListener;
    private Usuario mUsuario;
    private GoogleMap map;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private Double latitud,longtitud;
    private SupportMapFragment mapFragment;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_mapas_localizacion);
        mUsuario=(Usuario) getIntent().getExtras().getSerializable("user");

        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference();

        mapFragment=(SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapLoc);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mUsuario=(Usuario) getIntent().getExtras().getSerializable("user");
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map=googleMap;
        databaseReference.child("Localizacion").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Localizacion loc=snapshot.getValue(Localizacion.class);
                    if(loc.getUidUsuario().equals(mUsuario.getUid())){
                        latitud=loc.getLatitud();
                        longtitud=loc.getLongitud();
                    }
                }

                map.clear();
                map.addMarker(new MarkerOptions().position(new LatLng(latitud,longtitud)).title("Posicion profesor"));
                CameraPosition cameraPosition=new CameraPosition.Builder().target(new LatLng(latitud,longtitud)).zoom(14).build();
                map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
