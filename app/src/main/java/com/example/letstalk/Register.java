package com.example.letstalk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;



import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;



import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Register extends AppCompatActivity {
    EditText user,email,pass;
    Button b;
    FirebaseAuth mAuth;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Intent in=getIntent();
        user=(EditText) findViewById(R.id.editText);




        email=findViewById(R.id.editText2);
        pass=findViewById(R.id.editText3);
        b=findViewById(R.id.button);
        mAuth=FirebaseAuth.getInstance();
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register(user.getText().toString(),email.getText().toString(),pass.getText().toString());
            }
        });

    }
    public void register(final String username, String email, String password)
    {
        mAuth.createUserWithEmailAndPassword(email,password).
                addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            FirebaseUser user=mAuth.getCurrentUser();
                            String u=user.getUid();
                            reference= FirebaseDatabase.getInstance().getReference("Users").child(u);
                            HashMap<String,String> hash=new HashMap<>();
                            hash.put("id",u);
                            hash.put("username",username);
                            hash.put("dpurl","default");
                            hash.put("status","No status");
                            hash.put("state","online");
                            reference.setValue(hash).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Intent intent=new Intent(Register.this,MainPage.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);



                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });

                        }
                        else
                        {
                            Toast.makeText(Register.this, "Something went wrong....Please try again", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
