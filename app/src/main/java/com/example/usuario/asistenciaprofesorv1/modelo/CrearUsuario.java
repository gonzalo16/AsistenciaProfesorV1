package com.example.usuario.asistenciaprofesorv1.modelo;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.usuario.asistenciaprofesorv1.R;
import com.example.usuario.asistenciaprofesorv1.entidades.Asistencia;
import com.example.usuario.asistenciaprofesorv1.entidades.Localizacion;
import com.example.usuario.asistenciaprofesorv1.entidades.Usuario;
import com.example.usuario.asistenciaprofesorv1.utilidades.FirebaseHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.UUID;

/**
 * Esta clase se encargará de crear un nuevo usuario para la aplicacion
 * Author: Gonzalo Marinucci
 * Version 1.1
 */
public class CrearUsuario extends AppCompatActivity implements View.OnClickListener{

    /*
    Objetos de diseño
     */
    private EditText editTextNombre,editTextApellido,editTextNick,editTextEmail,editTextPassword;
    private Button botonCrear;
    private ProgressDialog progressDialog;

    private String nombre,email,password,nick,apellido;

    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private FirebaseHelper firebaseHelper;

    private Toolbar toolbar;

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

        toolbar=findViewById(R.id.toolbar);
        toolbar.setTitle("Creacion de cuenta");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        FirebaseApp.initializeApp(this);
        firebaseHelper=new FirebaseHelper();

        auth=FirebaseAuth.getInstance();
        currentUser=FirebaseAuth.getInstance().getCurrentUser();
        progressDialog=new ProgressDialog(this);

    }

    private void registrar(){
        nombre=editTextNombre.getText().toString();
        apellido=editTextApellido.getText().toString();
        nick=editTextNick.getText().toString();
        email=editTextEmail.getText().toString();
        password=editTextPassword.getText().toString();

        if(TextUtils.isEmpty(nombre)||TextUtils.isEmpty(apellido)||TextUtils.isEmpty(nick)||TextUtils.isEmpty(email)||TextUtils.isEmpty(password)){
            Toast.makeText(getApplicationContext(),"Campos vacios",Toast.LENGTH_LONG).show();
        }else{
            progressDialog.setMessage("Creando cuenta...");
            progressDialog.show();
            auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        Log.d("CreateUser", "createUserWithEmail:success");

                        String uidUsuario=currentUser.getUid();
                        Usuario usuario=new Usuario();
                        Asistencia asistencia=new Asistencia();
                        Localizacion loc=new Localizacion();

                        usuario.setNombre(nombre);
                        usuario.setPerfil("Profesor");
                        usuario.setApellido(apellido);
                        usuario.setUid(uidUsuario);
                        usuario.setEmail(email);
                        usuario.setPassword(password);

                        asistencia.setUsuario(usuario);
                        asistencia.setUid(UUID.randomUUID().toString());
                        asistencia.setFichado(false);

                        loc.setUidLoc(UUID.randomUUID().toString());
                        loc.setLatitud(0d);
                        loc.setLongitud(0d);
                        loc.setUidUsuario(usuario.getUid());


                        firebaseHelper.getDatabaseReference().child("Usuario").child(usuario.getUid()).setValue(usuario);
                        //firebaseHelper.getDatabaseReference().child("Asistencia").child(asistencia.getUid()).setValue(asistencia);
                        firebaseHelper.getDatabaseReference().child("Localizacion").child(loc.getUidLoc()).setValue(loc);

                        Toast.makeText(getApplicationContext(),"Cuenta creada correctamente",Toast.LENGTH_LONG).show();
                        finish();
                    }else{
                        Log.w("CreateUser", "createUserWithEmail:failure", task.getException());
                        //Toast.makeText(getApplicationContext(),"No se pudo crear la cuenta",Toast.LENGTH_LONG).show();
                        if(task.getException() instanceof FirebaseAuthUserCollisionException){
                            Toast.makeText(getApplicationContext(),"El correo ya existe",Toast.LENGTH_LONG).show();
                        }
                    }

                    progressDialog.dismiss();
                }
            });
        }
    }


    @Override
    public void onClick(View v) {
        registrar();
    }
}
