package com.example.usuario.asistenciaprofesorv1;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.usuario.asistenciaprofesorv1.entidades.Usuario;
import com.example.usuario.asistenciaprofesorv1.modelo.CrearUsuario;
import com.example.usuario.asistenciaprofesorv1.modelo.VentanaAdmin;
import com.example.usuario.asistenciaprofesorv1.modelo.VentanaGuardias;
import com.example.usuario.asistenciaprofesorv1.modelo.VentanaPrincipal;
import com.example.usuario.asistenciaprofesorv1.modelo.VentanaProfesor;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Locale;
import java.util.regex.Pattern;


/**
 * Clase mainAcitivy que es la principal donde se inicia la aplicacion
 * Author: Gonzalo Marinuncci
 * Version 1.1
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    /*
    Objetos de diseño
    btnLogin: boton para iniciar sesion
    buttonCreate: boton para registrarse
     */
    private Button btnLogin,buttonCreate;
    private EditText editTextemail,editTextemailpassword;
    private ProgressDialog progressDialog;

    /*
    Objetos de firebase
    autentication: objeto de firebase para poder realizar la autenticacion

     */
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth autentication;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    /*
    Objetos para el manejo del login y del usuario
    Usuario: almacena el objeto que se ha logeado
     */
    private Usuario usuario;

    /*
    Email: almacena en string el email del usurio
    Password: almacena en string la pssword del usuario
     */
    private String email,password;

    private FirebaseAuth.AuthStateListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
        Referencias a los objetos de diseño en el layout
         */
        btnLogin=findViewById(R.id.btnLogin);
        buttonCreate=findViewById(R.id.btnCreate);
        editTextemail=findViewById(R.id.etEmail);
        editTextemailpassword=findViewById(R.id.etPass);

        /*
        Inicializacion de objetos
         */
        FirebaseApp.initializeApp(this);
        progressDialog=new ProgressDialog(this);
        autentication= FirebaseAuth.getInstance();
        firebaseDatabase= FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference();
        listener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user=autentication.getCurrentUser();
                if(user==null){
                    //No esta logueado

                }else{
                    //Esta logueado

                }
            }
        };

        /*
        Asignacion de eventos
         */
        buttonCreate.setOnClickListener(this);
        btnLogin.setOnClickListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        autentication.addAuthStateListener(listener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(listener!=null){
            autentication.removeAuthStateListener(listener);
        }
    }

    private void getUsers(){
        DatabaseReference db=firebaseDatabase.getReference();
        db.child("Usuario").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    Usuario user=ds.getValue(Usuario.class);
                    if(user.getEmail().equals(email)){
                        usuario=user;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /*
     Este metodo se utiliza para obtener de la base de datos el usuario que se quiere loguear, recorre la base de datos
     en busca de una coinsidencia con el email y la password y lo almacena en el objeto 'Usuario'
     */
    public void obtenerUser(){
        email=editTextemail.getText().toString().trim();
        password=editTextemailpassword.getText().toString().trim();

        if(TextUtils.isEmpty(email)||TextUtils.isEmpty(password)){
            Toast.makeText(getApplicationContext(),getApplicationContext().getString(R.string.campos_vacios),Toast.LENGTH_LONG).show();
        }else{
            if(isValidEmail(email)==false){
                Toast.makeText(getApplicationContext(),getApplicationContext().getString(R.string.email_invalido),Toast.LENGTH_LONG).show();
            }else{
                progressDialog.setMessage(getApplicationContext().getString(R.string.iniciando_sesion));
                progressDialog.show();
                autentication.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful()){
                            try{
                                throw task.getException();
                            }catch(FirebaseAuthInvalidUserException invalidEmail){
                                Toast.makeText(getApplicationContext(),"El usuario no existe o esta deshabilidatado",Toast.LENGTH_LONG).show();
                            } catch (FirebaseAuthInvalidCredentialsException  e) {
                                Toast.makeText(getApplicationContext(),"La contraseña es incorrecta",Toast.LENGTH_LONG).show();
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                        }else{
                            getUsers();
                            Log.d("UserLogin", "signInWithEmail:success");
                            String currentUser=autentication.getCurrentUser().getUid();
                            String deviceToken= FirebaseInstanceId.getInstance().getToken();
                            DatabaseReference userDB=FirebaseDatabase.getInstance().getReference().child("Usuario");

                            userDB.child(currentUser).child("device_token").setValue(deviceToken).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    if(usuario!=null){
                                        if(usuario.getPerfil().equals("Administrador")){
                                            Intent i=new Intent(getApplicationContext(),VentanaAdmin.class);
                                            startActivity(i);

                                        }else{
                                            Intent i=new Intent(getApplicationContext(), VentanaProfesor.class);
                                            i.putExtra("Usuario",usuario);
                                            startActivity(i);
                                        }
                                    }
                                }
                            });
                        }
                        progressDialog.dismiss();
                    }
                });
            }
        }


    }

    /*
    Este metodo se utiliza cuando se pulsa en el boton registrarse y iniciará una nueva actividad
     */
    private void registrar(){
        Intent i=new Intent(MainActivity.this, CrearUsuario.class);
        startActivity(i);
    }

    private boolean isValidEmail(String email){
        Pattern pattern= Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }

    /*
    Este metodo se utiliza para loguearse, primero comprueba que los campos de email y password no estén vacios y luego mediante
    el objeto autentication comprueba si es correcto, si es correcto se unicia una nueva actividad
     */

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnLogin:
                obtenerUser();
                break;
            case R.id.btnCreate:
                registrar();
                break;
        }
    }
}
