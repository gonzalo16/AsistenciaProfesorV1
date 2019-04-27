package com.example.usuario.asistenciaprofesorv1;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.usuario.asistenciaprofesorv1.entidades.Usuario;
import com.example.usuario.asistenciaprofesorv1.modelo.CrearUsuario;
import com.example.usuario.asistenciaprofesorv1.modelo.VentanaAdmin;
import com.example.usuario.asistenciaprofesorv1.modelo.VentanaPrincipal;
import com.example.usuario.asistenciaprofesorv1.modelo.VentanaProfesor;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button btnLogin,buttonCreate;
    private EditText editTextemail,editTextemailpassword;
    private FirebaseAuth autentication;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private Usuario usuario;
    private String email,password;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLogin=findViewById(R.id.btnLogin);
        buttonCreate=findViewById(R.id.btnCreate);
        editTextemail=findViewById(R.id.etEmail);
        editTextemailpassword=findViewById(R.id.etPass);

        FirebaseApp.initializeApp(this);
        progressDialog=new ProgressDialog(this);
        buttonCreate.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        autentication= FirebaseAuth.getInstance();
        firebaseDatabase= FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference();

    }

    public void obtenerUser(){
        databaseReference.child("Usuario").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot d:dataSnapshot.getChildren()){
                        Usuario user=d.getValue(Usuario.class);
                        if(user.getEmail().equals(email)&&user.getPassword().equals(password)){
                            usuario=user;
                        }
                    }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void registrar(){
        Intent i=new Intent(MainActivity.this, CrearUsuario.class);
        startActivity(i);
    }

    private void logearse(){
        email=editTextemail.getText().toString().trim();
        password=editTextemailpassword.getText().toString().trim();

        if(TextUtils.isEmpty(email)||TextUtils.isEmpty(password)){
            Toast.makeText(getApplicationContext(),"Campos vacíos",Toast.LENGTH_LONG).show();
        }else{

            obtenerUser();
            progressDialog.setMessage("Iniciando sesion...");
            progressDialog.show();
            autentication.signInWithEmailAndPassword(email,password).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){

                        if(usuario.getPerfil().equals("Administrador")){
                            Intent i=new Intent(getApplicationContext(),VentanaAdmin.class);
                            startActivity(i);
                        }else{
                            Intent i=new Intent(getApplicationContext(), VentanaProfesor.class);
                            startActivity(i);
                        }
                    }else{
                        Toast.makeText(getApplicationContext(),"El correo o la contraseña son incorrectos",Toast.LENGTH_LONG).show();
                    }
                   progressDialog.dismiss();
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnLogin:
                logearse();
                break;
            case R.id.btnCreate:
                registrar();
                break;
        }
    }
}
