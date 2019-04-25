package com.example.usuario.asistenciaprofesorv1.modelo;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.usuario.asistenciaprofesorv1.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

public class CrearUsuario extends AppCompatActivity implements View.OnClickListener{

    private EditText editTextNombre,editTextApellido,editTextNick,editTextEmail,editTextPassword;
    private Button botonCrear;
    private FirebaseAuth autentication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_usuario);

        editTextNombre=findViewById(R.id.etNombre);
        editTextApellido=findViewById(R.id.etApellido);
        editTextNick=findViewById(R.id.etNick);
        editTextEmail=findViewById(R.id.etEmail);
        editTextPassword=findViewById(R.id.etPass);
        botonCrear=findViewById(R.id.btnUnirse);
        botonCrear.setOnClickListener(this);

        autentication=FirebaseAuth.getInstance();
    }

    private void registrar(){
        final String nombre=editTextNombre.getText().toString();
        final String apellido=editTextApellido.getText().toString();
        final String nick=editTextNick.getText().toString();
        final String email=editTextEmail.getText().toString();
        final String password=editTextPassword.getText().toString();

        if(TextUtils.isEmpty(nombre)||TextUtils.isEmpty(apellido)||TextUtils.isEmpty(nick)||TextUtils.isEmpty(email)||TextUtils.isEmpty(password)){
            Toast.makeText(getApplicationContext(),"Campos vacios",Toast.LENGTH_LONG).show();
        }else{
            autentication.createUserWithEmailAndPassword(email,password).addOnCompleteListener(CrearUsuario.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(getApplicationContext(),"Se ha registrado correctamente",Toast.LENGTH_LONG).show();
                        finish();
                        //cargarWebService(nombre,apellido,nick,email,password);
                    }else{
                        if(task.getException() instanceof FirebaseAuthUserCollisionException){
                            Toast.makeText(getApplicationContext(),"El correo ya existe",Toast.LENGTH_LONG).show();
                        }
                    }
                }
            });
        }
    }


    @Override
    public void onClick(View v) {
        registrar();
    }
}
