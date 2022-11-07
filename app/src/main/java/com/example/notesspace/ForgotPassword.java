package com.example.notesspace;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class ForgotPassword extends AppCompatActivity {

    private EditText memail_recovery;
    private Button mbtnrecover;
    private TextView mback;
    FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        Objects.requireNonNull(getSupportActionBar()).hide();
        memail_recovery = findViewById(R.id.email_recovery);
        mbtnrecover = findViewById(R.id.btnrecover);
        mback=findViewById(R.id.back);
        firebaseAuth = FirebaseAuth.getInstance();
        mback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ForgotPassword.this,MainActivity.class);
                startActivity(intent);
            }
        });
        mbtnrecover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mail = memail_recovery.getText().toString().trim();
                if (mail.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Please Enter Your Email",Toast.LENGTH_LONG).show();
                }
                else {
                    // we will send email later :)
                    firebaseAuth.sendPasswordResetEmail(mail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(getApplicationContext(),"Please Verify Your Email",Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(new Intent(ForgotPassword.this,MainActivity.class));
                            }else{
                                Toast.makeText(getApplicationContext(),"Email Not Found!",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }


}