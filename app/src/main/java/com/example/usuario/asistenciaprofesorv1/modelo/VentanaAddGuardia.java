package com.example.usuario.asistenciaprofesorv1.modelo;

import android.app.TimePickerDialog;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.usuario.asistenciaprofesorv1.R;
import com.example.usuario.asistenciaprofesorv1.adaptadores.AulasSpinAdapter;
import com.example.usuario.asistenciaprofesorv1.adaptadores.ProfesorSpinAdapter;
import com.example.usuario.asistenciaprofesorv1.entidades.Aulas;
import com.example.usuario.asistenciaprofesorv1.entidades.Guardia;
import com.example.usuario.asistenciaprofesorv1.entidades.Usuario;
import com.example.usuario.asistenciaprofesorv1.notificaciones.APIService;
import com.example.usuario.asistenciaprofesorv1.notificaciones.Client;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class VentanaAddGuardia extends AppCompatActivity implements View.OnClickListener{

    /*
    Diseño
     */
    private Toolbar toolbar;
    private Button botonHoraInicio,botonHoraFin,botonAceptar;
    private Spinner spinnerProf,spinnerAula;
    private EditText editTextHInicio,editTextHFin;
    private Calendar calendarHoraInicio,calendarHoraFin;

    /*
    Base de datos
     */
    private TimePickerDialog timePickerDialog;
    private DatabaseReference databaseReference;
    private DatabaseReference dbGuardia;
    private DatabaseReference guardiasReference;
    private FirebaseDatabase firebaseDatabase;


    private ArrayList<Usuario> profesores;
    private ArrayList<Aulas> aulas;
    private ArrayList<Guardia> guardias;

    private Usuario usuario;
    private Aulas aula;
    private Guardia guardia;
    private int horainicio,minutoinicio,horafin,minutofin;
    private Date horaInicio,horaFin;


    private APIService apiService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ventana_add_guardia);

        toolbar=findViewById(R.id.toolbar);
        toolbar.setTitle("Añadir guardia");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        botonHoraInicio=findViewById(R.id.btnHoraInicio);
        botonHoraFin=findViewById(R.id.btnHoraFin);
        botonAceptar=findViewById(R.id.btnRegistrar);
        spinnerProf=findViewById(R.id.spinProfesor);
        spinnerAula=findViewById(R.id.spinAula);
        editTextHInicio=findViewById(R.id.textoHoraInicio);
        editTextHFin=findViewById(R.id.textoHoraFin);


        profesores=new ArrayList<Usuario>();
        aulas=new ArrayList<Aulas>();
        guardias=new ArrayList<Guardia>();

        apiService= Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        FirebaseApp.initializeApp(this);
        firebaseDatabase=FirebaseDatabase.getInstance();


        botonHoraInicio.setOnClickListener(this);
        botonHoraFin.setOnClickListener(this);
        botonAceptar.setOnClickListener(this);
        cargarDatos();


        spinnerAula.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                aula=(Aulas)adapterView.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinnerProf.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                usuario=(Usuario)adapterView.getItemAtPosition(position);
                obtenerGuardias();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }




    private void cargarDatos(){

        databaseReference=firebaseDatabase.getReference();

        final ArrayList<String> nomProfesores=new ArrayList<String>();
        final ArrayList<String> nomAulas=new ArrayList<String>();

        databaseReference.child("Usuario").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                nomProfesores.clear();
                profesores.clear();
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    Usuario u=dataSnapshot1.getValue(Usuario.class);
                    if(u.getPerfil().equals("Profesor")){
                        profesores.add(u);
                        nomProfesores.add(u.getNombre());
                        spinnerProf.setAdapter(new ProfesorSpinAdapter(getApplicationContext(),profesores));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DatabaseReference aulasReference=firebaseDatabase.getReference();
        aulasReference.child("Aulas").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                nomAulas.clear();
                aulas.clear();
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    Aulas aula=dataSnapshot1.getValue(Aulas.class);
                    aulas.add(aula);
                    nomAulas.add(aula.getNombre());
                    spinnerAula.setAdapter(new AulasSpinAdapter(getApplicationContext(),aulas));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void obtenerGuardias(){
        guardiasReference=firebaseDatabase.getReference();
        guardiasReference.child("Guardia").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                guardias.clear();
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    Guardia g=ds.getValue(Guardia.class);
                    if(g.getUidUsuario().equals(usuario.getUid())){
                        guardias.add(g);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



    private void registrarGuardia(){

        boolean guardiaEnHora=false;

        if(TextUtils.isEmpty(editTextHInicio.getText().toString())||TextUtils.isEmpty(editTextHFin.getText().toString())||usuario==null||aula==null){
            Toast.makeText(getApplicationContext(),"Selecciona los datos correspondientes",Toast.LENGTH_LONG).show();
        }else{

            guardia=new Guardia();
            guardia.setAulas(aula);
            guardia.setHoraInicio(horaInicio);
            guardia.setHoraFin(horaFin);
            guardia.setUidUsuario(usuario.getUid());
            guardia.setNombreProfesor(usuario.getNombre());
            String uid=UUID.randomUUID().toString();
            guardia.setUid(uid);

            int horaInicial=guardia.getHoraInicio().getHours();

            for(int i=0;i<guardias.size();i++){
                if(horaInicial==guardias.get(i).getHoraInicio().getHours()){
                    guardiaEnHora=true;
                }
            }

            dbGuardia=firebaseDatabase.getReference();
            dbGuardia.child("Guardia").push().setValue(guardia);
            Toast.makeText(getApplicationContext(),"Guardia asignada correctamente",Toast.LENGTH_LONG).show();
           /* if(guardiaEnHora==false){
                if(horafin<=horainicio){
                    Toast.makeText(getApplicationContext(),"Selecciona una hora mayor que la de inicio",Toast.LENGTH_LONG).show();
                }else{
                    dbGuardia=firebaseDatabase.getReference();
                    dbGuardia.child("Guardia").push().setValue(guardia);
                    Toast.makeText(getApplicationContext(),"Guardia asignada correctamente",Toast.LENGTH_LONG).show();

                    finish();
                }
                dbGuardia=firebaseDatabase.getReference();
                dbGuardia.child("Guardia").push().setValue(guardia);
                Toast.makeText(getApplicationContext(),"Guardia asignada correctamente",Toast.LENGTH_LONG).show();

                finish();
            }else{
                Toast.makeText(getApplicationContext(),"Ya hay una guardia asignada a esa hora",Toast.LENGTH_LONG).show();
            }*/
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnHoraInicio:
                calendarHoraInicio=Calendar.getInstance();
               timePickerDialog=new TimePickerDialog(this,R.style.TimerStyle, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {

                        horaInicio=new Date();
                        calendarHoraInicio.setTime(horaInicio);
                        calendarHoraInicio.set(Calendar.HOUR_OF_DAY,hour);
                        calendarHoraInicio.set(Calendar.MINUTE,minute);

                        editTextHInicio.setText(hour+": "+minute);
                        horainicio=hour;
                        minutoinicio=minute;
                        horaInicio=calendarHoraInicio.getTime();
                        botonHoraFin.setEnabled(true);
                        //Descomentar esto
                        /*if(hour>=8&&hour<=14){
                            horaInicio=new Date();
                            calendarHoraInicio.setTime(horaInicio);
                            calendarHoraInicio.set(Calendar.HOUR_OF_DAY,hour);
                            calendarHoraInicio.set(Calendar.MINUTE,minute);

                            editTextHInicio.setText(hour+": "+minute);
                            horainicio=hour;
                            minutoinicio=minute;
                            horaInicio=calendarHoraInicio.getTime();
                            botonHoraFin.setEnabled(true);
                        }else{
                            Toast.makeText(getApplicationContext(),"Establece un horario entre las 8 y las 15 horas",Toast.LENGTH_LONG).show();
                        }*/
                    }
                },horainicio,minutoinicio, android.text.format.DateFormat.is24HourFormat(getApplicationContext()));
                timePickerDialog.show();

                break;
            case R.id.btnHoraFin:
                calendarHoraFin=Calendar.getInstance();
                timePickerDialog=new TimePickerDialog(this,R.style.TimerStyle, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {

                        horaFin=new Date();
                        calendarHoraFin.set(Calendar.HOUR_OF_DAY,hour);
                        calendarHoraFin.set(Calendar.MINUTE,minute);
                        editTextHFin.setText(hour+": "+minute);
                        horafin=hour;
                        minutofin=minute;
                        horaFin=calendarHoraFin.getTime();
                        //Descomentar esto
                        /*if(hour>=8&&hour<=15){
                            if(hour<=horainicio){
                                Toast.makeText(getApplicationContext(), "Selecciona una hora mayor a la de entrada", Toast.LENGTH_SHORT).show();
                            }else{
                                horaFin=new Date();
                                calendarHoraFin.set(Calendar.HOUR_OF_DAY,hour);
                                calendarHoraFin.set(Calendar.MINUTE,minute);
                                editTextHFin.setText(hour+": "+minute);
                                horafin=hour;
                                minutofin=minute;
                                horaFin=calendarHoraFin.getTime();
                            }
                        }else{
                            Toast.makeText(getApplicationContext(),"Establece un horario entre las 8 y las 15 horas",Toast.LENGTH_LONG).show();
                        }*/
                    }
                },horafin,minutofin, android.text.format.DateFormat.is24HourFormat(getApplicationContext()));
                timePickerDialog.show();

                break;
            case R.id.btnRegistrar:
                registrarGuardia();
                break;

        }
    }
}
