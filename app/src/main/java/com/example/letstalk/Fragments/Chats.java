package com.example.letstalk.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.letstalk.Chat;
import com.example.letstalk.Messages;
import com.example.letstalk.R;
import com.example.letstalk.User;
import com.example.letstalk.UserAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class Chats extends Fragment implements UserAdapter.OnItemClickListener{

    public RecyclerView recyclerView;
    public UserAdapter userAdapter;
    private Set<String> mUsers;

    FirebaseUser firebaseUser;
    String incognito;
    DatabaseReference reference;
    DatabaseReference reference2;
    private List<User> userList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_chats,container,false);

        recyclerView=view.findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        userList=new ArrayList<>();
        mUsers= new HashSet<>();
        reference= FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(firebaseUser.getUid() != null) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {


                        Chat c = snapshot.getValue(Chat.class);
                        Log.i("parse", c.getSender() + "==" + firebaseUser.getUid());

                        if (c.getSender().equals(firebaseUser.getUid()) ) {
                            mUsers.add(c.getReceiver());
                            Log.i("parse", Integer.toString(mUsers.size()));

                        } else if (c.getReceiver().equals(firebaseUser.getUid()) ) {
                            mUsers.add(c.getSender());

                        }

                    }
                }
                charge();

            }





            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        Log.i("parse","yaha pe size hai "+Integer.toString(mUsers.size()));






        return view;

    }

    public void charge()
    {Log.i("pares",mUsers.toString());
        reference=FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for(String u:mUsers)
                {
                    for(DataSnapshot snapshot:dataSnapshot.getChildren())
                    {
                        User user=snapshot.getValue(User.class);
                        if(u.equals(user.getId()))
                            userList.add(user);

                    }
                }
                userAdapter=new UserAdapter(getContext(),userList,true);
                userAdapter.setOnItemClickListener(Chats.this);
                recyclerView.setAdapter(userAdapter);




            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }



    @Override
    public void itemClick(int pos) {
        User current=userList.get(pos);
        Log.i("info",current.getUsername());
        Intent intent=new Intent(getContext(), Messages.class);
        intent.putExtra("id",current.getId());

        startActivity(intent);

    }

    @Override
    public void imageclick(int pos) {
        User current=userList.get(pos);
        Toast.makeText(getContext(), current.getUsername(), Toast.LENGTH_SHORT).show();

    }
}
