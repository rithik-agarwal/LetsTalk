package com.example.letstalk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class Forgot extends AppCompatActivity {
    Toolbar toolbar;
    EditText e;
    Button b;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot);
        toolbar=findViewById(R.id.toolbar);
        toolbar.setTitle("Reset Password");

        e=findViewById(R.id.send_email);
        b=findViewById(R.id.reset);

        auth=FirebaseAuth.getInstance();
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=e.getText().toString();
                if(email.equals(""))
                    Toast.makeText(Forgot.this, "hatt", Toast.LENGTH_SHORT).show();
                else
                    auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                Toast.makeText(Forgot.this, "Please check your email", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(Forgot.this,Login.class));
                            }
                            else
                            {
                                String error=task.getException().getMessage();
                                Toast.makeText(Forgot.this, error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            }
        });

    }
}
