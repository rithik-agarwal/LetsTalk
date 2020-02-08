package com.example.letstalk.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class UsersFragment extends Fragment implements UserAdapter.OnItemClickListener {
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> mUsers;
    EditText e4;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_users, container, false);
        e4=view.findViewById(R.id.editText4);
        recyclerView=view.findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mUsers=new ArrayList<>();
        read();
        e4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                search(charSequence.toString());

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });




        return view;
    }
    public void search(String form)
    {
        final FirebaseUser fuser=FirebaseAuth.getInstance().getCurrentUser();
        Query q=FirebaseDatabase.getInstance().getReference("Users").orderByChild("username").startAt(form).endAt(form+"\uf8ff");
        q.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                for(DataSnapshot snapshot:dataSnapshot.getChildren())
                {
                    User user=snapshot.getValue(User.class);
                    if(!user.getId().equals(fuser.getUid()))
                        mUsers.add(user);
                }
                userAdapter=new UserAdapter(getContext(),mUsers,false);
                recyclerView.setAdapter(userAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void read()
    {
        final FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (e4.getText().toString().equals("")) {
                    Log.i("parse","rihtikagarwal");
                    mUsers.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        User user = snapshot.getValue(User.class);

                        assert user != null;
                        assert firebaseUser != null;
                        if (!user.getId().equals(firebaseUser.getUid()))
                            mUsers.add(user);
                    }
                    userAdapter = new UserAdapter(getContext(), mUsers, false);
                    userAdapter.setOnItemClickListener(UsersFragment.this);
                    recyclerView.setAdapter(userAdapter);
                }
            }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i("info",databaseError.getMessage());

            }
        });
    }

    @Override
    public void itemClick(int pos) {
        User current=mUsers.get(pos);
        Log.i("info",current.getUsername());
        Intent intent=new Intent(getContext(), Messages.class);
        intent.putExtra("id",current.getId());
        startActivity(intent);



    }

    @Override
    public void imageclick(int pos) {
        User current=mUsers.get(pos);
        Toast.makeText(getContext(),current.getUsername() , Toast.LENGTH_SHORT).show();
    }
}
