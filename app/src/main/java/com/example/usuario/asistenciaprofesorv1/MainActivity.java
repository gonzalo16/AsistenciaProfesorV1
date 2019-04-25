package com.example.usuario.asistenciaprofesorv1;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.usuario.asistenciaprofesorv1.modelo.CrearUsuario;
import com.example.usuario.asistenciaprofesorv1.modelo.VentanaPrincipal;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button btnLogin,buttonCreate;
    private EditText editTextemail,editTextemailpassword;
    private FirebaseAuth autentication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLogin=findViewById(R.id.btnLogin);
        buttonCreate=findViewById(R.id.btnCreate);
        editTextemail=findViewById(R.id.etEmail);
        editTextemailpassword=findViewById(R.id.etPass);

        buttonCreate.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        autentication= FirebaseAuth.getInstance();
    }

    private void registrar(){
        Intent i=new Intent(MainActivity.this, CrearUsuario.class);
        startActivity(i);
    }

    private void logearse(){
        String email=editTextemail.getText().toString().trim();
        String password=editTextemailpassword.getText().toString().trim();

        if(TextUtils.isEmpty(email)||TextUtils.isEmpty(password)){
            Toast.makeText(getApplicationContext(),"Campos vacíos",Toast.LENGTH_LONG).show();
        }else{
            //progressDialog.setMessage("Iniciando sesion...");
            //progressDialog.show();
            autentication.signInWithEmailAndPassword(email,password).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(getApplicationContext(),"Logueado correctamente",Toast.LENGTH_LONG).show();
                        Intent i=new Intent(getApplicationContext(),VentanaPrincipal.class);
                        startActivity(i);
                    }else{
                        Toast.makeText(getApplicationContext(),"El correo o la contraseña son incorrectos",Toast.LENGTH_LONG).show();
                    }
                   // progressDialog.dismiss();
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
