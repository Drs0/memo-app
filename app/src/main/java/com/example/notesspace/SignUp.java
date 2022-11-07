package com.example.notesspace;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.security.PrivateKey;
import java.util.Objects;

public class SignUp extends AppCompatActivity {
    private EditText mlogin_email;
    private EditText mlogin_password;
    private RelativeLayout msignup;
    private RelativeLayout mlogin;
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Objects.requireNonNull(getSupportActionBar()).hide();
        mlogin_email = findViewById(R.id.login_email);
        mlogin_password = findViewById(R.id.login_password);
        msignup = findViewById(R.id.signup);
        mlogin = findViewById(R.id.login);
        // get instance of the firebase
        firebaseAuth = FirebaseAuth.getInstance();
        mlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUp.this,MainActivity.class);
                startActivity(intent);
            }
        });
        msignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mail = mlogin_email.getText().toString().trim();
                String pass = mlogin_password.getText().toString().trim();
                if (mail.isEmpty() || pass.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Please Fill The Required Fields!",Toast.LENGTH_LONG).show();
                }
                else if(pass.length()<7){
                    Toast.makeText(getApplicationContext(),"Password Is Too Short!",Toast.LENGTH_LONG).show();
                }
                else{
                    // Register User Later :)
                    firebaseAuth.createUserWithEmailAndPassword(mail,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(getApplicationContext(),"Welcome to the family :)",Toast.LENGTH_SHORT).show();
                                sendemailverification();
                            }else{
                                Toast.makeText(getApplicationContext(),"Failed To Register :(",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

    }
    private void sendemailverification(){
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser!=null){
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(getApplicationContext(),"Please Verify Your Email To Activate Your Account",Toast.LENGTH_SHORT).show();
                    firebaseAuth.signOut();
                    startActivity(new Intent(SignUp.this,MainActivity.class));
                }
            });
        }else{
            Toast.makeText(getApplicationContext(),"Error While Sending Email Please Try Again :(",Toast.LENGTH_SHORT).show();
        }
    }
}