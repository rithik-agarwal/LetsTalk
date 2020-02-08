package com.example.letstalk.Fragments;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.letstalk.R;
import com.example.letstalk.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import static android.app.Activity.RESULT_OK;


public class UsersProfilr extends Fragment {
    TextView u;
    ImageView img;
    TextView status;
    ImageButton b1;
    ImageButton b2;
    Button b;
    public int req=1;
    Uri image;
    StorageReference mStorage;

    DatabaseReference reference;
    private StorageTask uploadTask;
    FirebaseUser firebaseUser;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_users_profilr, container, false);
        u=view.findViewById(R.id.username);
        img=view.findViewById(R.id.profileimage);
        b1=view.findViewById(R.id.imagebutton);
        b2=view.findViewById(R.id.imagebutton2);
        b=view.findViewById(R.id.button);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upload();
            }
        });
        status=view.findViewById(R.id.showstatus);
        reference= FirebaseDatabase.getInstance().getReference("Users");
        mStorage= FirebaseStorage.getInstance().getReference("uploads");
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                change();

            }
        });
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot:dataSnapshot.getChildren())
                {
                    User user=snapshot.getValue(User.class);
                    if(user.getId().equals(firebaseUser.getUid()))
                    {
                        u.setText(user.getUsername());
                        status.setText(user.getStatus());
                        if(user.getDpurl().equals("default"))
                            img.setImageResource(R.mipmap.ic_launcher);
                        else
                            Picasso.get().load(user.getDpurl()).resize(200,200).into(img);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        return view;
    }
    public void change()
    {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,req);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

            if(requestCode==req && resultCode==RESULT_OK && data != null)
            {
                image=data.getData();
                Picasso.get().load(image).into(img);
                b.setVisibility(View.VISIBLE);

            }
        }
    private String resolver(Uri uri)
    {ContentResolver cr=getContext().getContentResolver();
        MimeTypeMap mime=MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }
    public void upload()
    {
        final ProgressDialog pd=new ProgressDialog(getContext());
        pd.setMessage("Uploading");
        pd.show();
        if(image != null)
        {
            final StorageReference filereference=mStorage.child(System.currentTimeMillis()+"."+resolver(image));
            uploadTask=filereference.putFile(image).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(getContext(), "uploaded", Toast.LENGTH_SHORT).show();
                    filereference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Uri down=uri;
                            String u=down.toString();
                            reference.child(firebaseUser.getUid()).child("dpurl").setValue(u);


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            e.printStackTrace();
                        }
                    });

                    pd.dismiss();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), "Can't upload dp..try again later", Toast.LENGTH_SHORT).show();

                }
            });

        }
        else
        {
            Toast.makeText(getContext(), "No item selected", Toast.LENGTH_SHORT).show();
        }
    }
    }

