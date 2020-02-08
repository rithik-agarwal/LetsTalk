package com.example.letstalk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Messages extends AppCompatActivity implements MessageAdapter.OnItemClickListener {
    DatabaseReference reference;
    DatabaseReference ref;
    FirebaseUser firebaseUser;
    TextView t;
    CircleImageView c;
    MessageAdapter adapter;
    List<Chat> chats;
    String look;
    List<String> chat1;
    ImageButton img;
    EditText e;
    RecyclerView recyclerView;
    TextView state;
    ValueEventListener seenlistener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        Intent in=getIntent();
        recyclerView=findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);

        recyclerView.setLayoutManager(linearLayoutManager);

        final String userid=in.getStringExtra("id");

        reference= FirebaseDatabase.getInstance().getReference("Users").child(userid);
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        t=findViewById(R.id.text);
        c=findViewById(R.id.profile);
        state=findViewById(R.id.state);
        e=findViewById(R.id.editText);
        img=findViewById(R.id.image);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                send(firebaseUser.getUid(),userid,e.getText().toString());
            }
        });
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user=dataSnapshot.getValue(User.class);
                t.setText(user.getUsername());
                state.setText(user.getState());
                if(user.getDpurl().equals("default")){
                    c.setImageResource(R.mipmap.ic_launcher);
                Toast.makeText(Messages.this, "huh", Toast.LENGTH_SHORT).show();}
                else
                {
                    Picasso.get().load(user.getDpurl()).resize(30,30).into(c);
                }
                read(firebaseUser.getUid(),userid,user.getDpurl());


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Messages.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
        seen(userid);

    }
    public void send(String sender,String receiver,String message)
    {
        DatabaseReference reference1=FirebaseDatabase.getInstance().getReference();
        HashMap<String,Object> hash=new HashMap<>();
        hash.put("sender",sender);
        hash.put("receiver",receiver);
        hash.put("message",message);
        hash.put("isseen",false);


        reference1.child("Chats").push().setValue(hash);
        e.setText("");

    }
    public void seen(final String userid)
    {
        reference=FirebaseDatabase.getInstance().getReference("Chats");
        seenlistener=reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot:dataSnapshot.getChildren())
                {
                    Chat c=snapshot.getValue(Chat.class);
                    if(c.getReceiver().equals(firebaseUser.getUid()) && c.getSender().equals(userid))
                    {
                        HashMap<String,Object> map=new HashMap<>();
                        map.put("isseen",true);
                        snapshot.getRef().updateChildren(map);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void read(final String myid, final String userid, final String imgurl)
    {
        chats=new ArrayList<>();
        chat1=new ArrayList<>();
        ref=FirebaseDatabase.getInstance().getReference("Chats");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chats.clear();
                chat1.clear();
                for(DataSnapshot snapshot:dataSnapshot.getChildren())
                {
                    Chat c=snapshot.getValue(Chat.class);
                    String c1=snapshot.getKey();

                        if (c.getReceiver().equals(myid) && c.getSender().equals(userid)  || c.getReceiver().equals(userid) && c.getSender().equals(myid)) {
                            chats.add(c);
                            chat1.add(c1);
                        }



                }
                adapter=new MessageAdapter(Messages.this,chats,imgurl);
                recyclerView.setAdapter(adapter);
                adapter.setOnItemClickListener(Messages.this);
                Log.i("rihtik","pahucha");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    public void state(String state)
    {if(firebaseUser.getUid() != null) {
        if (!state.equals("online"))
            FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid()).child("state").setValue("last seen at " + state);
        else
            FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid()).child("state").setValue(state);
    }

    }

    @Override
    protected void onResume() {
        super.onResume();
        state("online");
    }
    protected void onPause() {

        super.onPause();
        reference.removeEventListener(seenlistener);
        Calendar calendar=Calendar.getInstance();
        SimpleDateFormat sdf=new SimpleDateFormat("HH:mm:ss");

        state(sdf.format(calendar.getTime()));
    }

    @Override
    public void onDelete(int pos) {
        final Chat c=chats.get(pos);
        final String key=chat1.get(pos);
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Chats").child(key);
        reference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                Toast.makeText(Messages.this, "deleted", Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(Messages.this, "oops", Toast.LENGTH_SHORT).show();
            }
        });



    }

    @Override
    public void click(int pos) {
        Log.i("rithik","aaya");
        Toast.makeText(this, "Long click on the message to perform actions", Toast.LENGTH_SHORT).show();

    }
}
