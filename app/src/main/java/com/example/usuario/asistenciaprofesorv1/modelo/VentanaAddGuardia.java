package com.example.usuario.asistenciaprofesorv1.modelo;

import android.app.TimePickerDialog;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.example.usuario.asistenciaprofesorv1.R;
import com.example.usuario.asistenciaprofesorv1.entidades.Aulas;
import com.example.usuario.asistenciaprofesorv1.entidades.Usuario;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class VentanaAddGuardia extends AppCompatActivity implements View.OnClickListener{

    private Toolbar toolbar;
    private Button botonHoraInicio,botonHoraFin,botonAceptar;
    private Spinner spinnerProf,spinnerAula;
    private EditText editTextHInicio,editTextHFin;
    private Calendar calendar;
    private TimePickerDialog timePickerDialog;
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;
    private ArrayList<Usuario> profesores;
    private ArrayList<Aulas> aulas;


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


        botonHoraInicio.setOnClickListener(this);
        botonHoraFin.setOnClickListener(this);
        cargarDatos();

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
                        spinnerProf.setAdapter(new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_spinner_item,nomProfesores));
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
                    spinnerAula.setAdapter(new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_spinner_item,nomAulas));
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
            case R.id.btnHoraInicio:
                calendar=Calendar.getInstance();
               int hora=calendar.get(Calendar.HOUR_OF_DAY);
               int minutos=calendar.get(Calendar.MINUTE);

               timePickerDialog=new TimePickerDialog(this,R.style.TimerStyle, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                        editTextHInicio.setText(hour+": "+minute);
                    }
                },hora,minutos,false);


                timePickerDialog.show();
                break;
            case R.id.btnHoraFin:
                calendar=Calendar.getInstance();
                int horafin=calendar.get(Calendar.HOUR_OF_DAY);
                int minutosfin=calendar.get(Calendar.MINUTE);

                timePickerDialog=new TimePickerDialog(this,R.style.TimerStyle, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                        editTextHFin.setText(hour+": "+minute);
                    }
                },horafin,minutosfin,false);
                timePickerDialog.show();
                break;
            case R.id.btnRegistrar:

        }
    }
}
