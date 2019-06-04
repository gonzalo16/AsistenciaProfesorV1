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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.usuario.asistenciaprofesorv1.R;
import com.example.usuario.asistenciaprofesorv1.control.OpeAddGuardia;
import com.example.usuario.asistenciaprofesorv1.entidades.Aulas;
import com.example.usuario.asistenciaprofesorv1.entidades.Guardia;
import com.example.usuario.asistenciaprofesorv1.entidades.Usuario;

import java.util.Calendar;
import java.util.Date;

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


    private Usuario usuario;
    private Aulas aula;
    private int horainicio,minutoinicio,horafin,minutofin;
    private Date horaInicio,horaFin;

    private OpeAddGuardia opeAddGuardia;


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


        botonHoraInicio.setOnClickListener(this);
        botonHoraFin.setOnClickListener(this);
        botonAceptar.setOnClickListener(this);

        opeAddGuardia=new OpeAddGuardia(getApplicationContext());
        opeAddGuardia.cargarAulas(spinnerAula);
        opeAddGuardia.cargarProfesores(spinnerProf);


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


    private void registrarGuardia(){
        if(TextUtils.isEmpty(editTextHInicio.getText().toString())||TextUtils.isEmpty(editTextHFin.getText().toString())||usuario==null||aula==null){
            Toast.makeText(getApplicationContext(),"Selecciona los datos correspondientes",Toast.LENGTH_LONG).show();
        }else{
            String uidGuardia=opeAddGuardia.getFirebaseHelper().getDatabaseReference().push().getKey();
            Guardia guardia=opeAddGuardia.registrarGuardia(uidGuardia,aula,horaInicio,horaFin,usuario.getNombre(),usuario.getUid());

            if(guardia!=null){
                opeAddGuardia.getFirebaseHelper().getDatabaseReference().child("Guardia").child(uidGuardia).setValue(guardia);
                Toast.makeText(getApplicationContext(),"Guardia asignada correctamente",Toast.LENGTH_LONG).show();
                finish();
            }

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

                    }
                },horainicio,minutoinicio, android.text.format.DateFormat.is24HourFormat(getApplicationContext()));
                timePickerDialog.show();

                break;
            case R.id.btnHoraFin:
                calendarHoraFin=Calendar.getInstance();
                timePickerDialog=new TimePickerDialog(this,R.style.TimerStyle, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {

                        if(hour<=horainicio){
                            Toast.makeText(getApplicationContext(), "Selecciona una hora mayor a la de entrada", Toast.LENGTH_SHORT).show(); }
                            else{
                                horaFin=new Date();
                                calendarHoraFin.set(Calendar.HOUR_OF_DAY,hour);
                                calendarHoraFin.set(Calendar.MINUTE,minute);
                                editTextHFin.setText(hour+": "+minute);
                                horafin=hour;
                                minutofin=minute;
                                horaFin=calendarHoraFin.getTime();
                        }
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
