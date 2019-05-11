package com.example.usuario.asistenciaprofesorv1.modelo;

import android.app.TimePickerDialog;
import android.graphics.Color;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

public class VentanaAddGuardia extends AppCompatActivity implements View.OnClickListener{

    /*
    Diseño
     */
    private Toolbar toolbar;
    private Button botonHoraInicio,botonHoraFin,botonAceptar;
    private Spinner spinnerProf,spinnerAula;
    private EditText editTextHInicio,editTextHFin;
    private Calendar calendar;

    /*
    Base de datos
     */
    private TimePickerDialog timePickerDialog;
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;
    private ArrayList<Usuario> profesores;
    private ArrayList<Aulas> aulas;
    private Usuario usuario;
    private Aulas aula;
    private Guardia guardia;
    private int horainicio,minutoinicio,horafin,minutofin;
    private APIService apiService;
    private String ampm;


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
        apiService= Client.getClient("https://fcm.googleapis.com/").create(APIService.class);


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
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }




    private void cargarDatos(){
        FirebaseApp.initializeApp(this);
        firebaseDatabase=FirebaseDatabase.getInstance();
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


    private void registrarGuardia(){

        if(TextUtils.isEmpty(editTextHInicio.getText().toString())||TextUtils.isEmpty(editTextHFin.getText().toString())||usuario==null||aula==null){
            Toast.makeText(getApplicationContext(),"Selecciona los datos correspondientes",Toast.LENGTH_LONG).show();
        }else{
            guardia=new Guardia();
            guardia.setAulas(aula);
            guardia.setUsuario(usuario);
            guardia.setHorainicio(horainicio);
            guardia.setMinutoinicio(minutoinicio);
            guardia.setHorafin(horafin);
            guardia.setMinutofin(minutofin);
            String uid=UUID.randomUUID().toString();
            guardia.setUid(uid);
            DatabaseReference dbGuardia=firebaseDatabase.getReference();
            dbGuardia.child("Guardia").child(guardia.getUsuario().getUid()).setValue(guardia);
            Toast.makeText(getApplicationContext(),"Guardia asignada correctamente",Toast.LENGTH_LONG).show();

            databaseReference=FirebaseDatabase.getInstance().getReference("Usuario").child(uid);

            finish();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnHoraInicio:
                calendar=Calendar.getInstance();
                //horainicio=calendar.get(Calendar.HOUR_OF_DAY);
                //minutoinicio=calendar.get(Calendar.MINUTE);

               timePickerDialog=new TimePickerDialog(this,R.style.TimerStyle, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                        if(hour>=12){
                            ampm="PM";
                        }else{
                            ampm="AM";
                        }
                        editTextHInicio.setText(hour+": "+minute);
                        horainicio=hour;
                        minutoinicio=minute;
                    }
                },horainicio,minutoinicio,false);


                timePickerDialog.show();
                break;
            case R.id.btnHoraFin:
                calendar=Calendar.getInstance();
                 //horafin=calendar.get(Calendar.HOUR_OF_DAY);
                 //minutofin=calendar.get(Calendar.MINUTE);

                timePickerDialog=new TimePickerDialog(this,R.style.TimerStyle, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                        editTextHFin.setText(hour+": "+minute);
                        horafin=hour;
                        minutofin=minute;
                    }
                },horafin,minutofin,false);
                timePickerDialog.show();
                break;
            case R.id.btnRegistrar:
                registrarGuardia();
                break;

        }
    }
}
