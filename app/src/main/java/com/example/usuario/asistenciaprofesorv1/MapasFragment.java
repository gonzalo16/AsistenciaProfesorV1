package com.example.usuario.asistenciaprofesorv1;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.usuario.asistenciaprofesorv1.modelo.VentanaLocalizacion;
import com.example.usuario.asistenciaprofesorv1.modelo.VentanaProfesor;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


public class MapasFragment extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap map;
    private Boolean actualPosition = true;
    private JSONObject jsonObject;
    private Double longitudOrigen, latitudOrigen;
    private SupportMapFragment mapFragment;
    private final Location locActual=new Location("");

    private OnFragmentInteractionListener mListener;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_mapas);

        mapFragment=(SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locActual.setLatitude(36.7336407d);
        locActual.setLongitude(-4.4144515d);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void trazarRuta(JSONObject jso) {
        JSONArray jRoutes;
        JSONArray jLegs;
        JSONArray jSteps;

        try {
            jRoutes = jso.getJSONArray("routes");
            for (int i = 0; i < jRoutes.length(); i++) {
                jLegs = ((JSONObject) (jRoutes.get(i))).getJSONArray("legs");

                for (int j = 0; j < jLegs.length(); j++) {
                    jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");

                    for (int k = 0; k < jSteps.length(); k++) {
                        String polyline = "" + ((JSONObject) ((JSONObject) jSteps.get(k)).get("polyline")).getString("points");
                        List<LatLng> list = PolyUtil.decode(polyline);
                        map.addPolyline(new PolylineOptions().addAll(list).color(Color.RED).width(6));

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            map.setMyLocationEnabled(true);
           map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
               @Override
               public void onMyLocationChange(Location location) {
                   if(actualPosition){
                       map.clear();
                       latitudOrigen=location.getLatitude();
                       longitudOrigen=location.getLongitude();

                       LatLng marker=new LatLng(latitudOrigen,longitudOrigen);
                       LatLng markerDestino=new LatLng(36.720242,-4.443076);
                       float distancia=locActual.distanceTo(location);

                       //Toast.makeText(getApplicationContext(),"Latitud: "+String.valueOf(location.getLatitude())+"Longitud: "+String.valueOf(location.getLongitude()),Toast.LENGTH_LONG).show();
                       map.addMarker(new MarkerOptions().position(marker)
                               .title("Posicion actual"));
                       map.addMarker(new MarkerOptions().position(markerDestino)
                               .title("Destino"));
                       CameraPosition cameraPosition=new CameraPosition.Builder().target(new LatLng(latitudOrigen,longitudOrigen)).zoom(14).build();
                       map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                       //actualPosition=false;

                       String url="https://maps.googleapis.com/maps/api/directions/json?origin="+latitudOrigen+","+longitudOrigen+"&destination=36.720242,-4.443076&key=AIzaSyDs4Io_Ml6SADhYnP33AnMsO9aM34adEl0";

                       RequestQueue queue= Volley.newRequestQueue(getApplicationContext());
                       StringRequest stringRequest=new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                           @Override
                           public void onResponse(String response) {
                               try {
                                   jsonObject=new JSONObject(response);
                                   trazarRuta(jsonObject);
                                   Log.i("jsonRuta",""+response);
                               } catch (JSONException e) {
                                   e.printStackTrace();
                               }
                           }
                       }, new Response.ErrorListener() {
                           @Override
                           public void onErrorResponse(VolleyError error) {

                           }
                       });

                       queue.add(stringRequest);
                   }
               }
           });


    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 1:
                if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){

                }else{

                }
                return;
        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
