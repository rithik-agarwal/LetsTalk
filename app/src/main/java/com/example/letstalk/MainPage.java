package com.example.letstalk;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.letstalk.Fragments.Chats;
import com.example.letstalk.Fragments.UsersFragment;
import com.example.letstalk.Fragments.UsersProfilr;
import com.google.android.material.tabs.TabLayout;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class MainPage extends AppCompatActivity {

    TextView username;
    CircleImageView image;

    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        Intent in=getIntent();
        image=findViewById(R.id.profile);
        username=findViewById(R.id.text);

        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        databaseReference= FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);
                username.setText(user.getUsername());

                image.setImageResource(R.mipmap.ic_launcher);
                if (user.getDpurl().matches("default"))
                    Toast.makeText(MainPage.this, user.getDpurl(), Toast.LENGTH_SHORT).show();
                else
                    Picasso.get().load(user.getDpurl()).resize(30, 30).into(image);
            }




            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainPage.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
        TabLayout tabLayout=findViewById(R.id.tab_layout);
        final ViewPager viewPager=findViewById(R.id.viewpager);
        tabLayout.addTab(tabLayout.newTab().setText("Chats"));
        tabLayout.addTab(tabLayout.newTab().setText("Users"));
        tabLayout.addTab(tabLayout.newTab().setText("Profile"));
// Set the tabs to fill the entire layout.
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        ViewPageAdapter viewPageAdapter=new ViewPageAdapter(getSupportFragmentManager(),tabLayout.getTabCount());




        viewPager.setAdapter(viewPageAdapter);
        viewPager.addOnPageChangeListener(new
                TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new
                                                   TabLayout.OnTabSelectedListener() {
                                                       @Override
                                                       public void onTabSelected(TabLayout.Tab tab) {
                                                           viewPager.setCurrentItem(tab.getPosition());
                                                       }

                                                       @Override
                                                       public void onTabUnselected(TabLayout.Tab tab) {
                                                       }

                                                       @Override
                                                       public void onTabReselected(TabLayout.Tab tab) {
                                                       }
                                                   });


    }
    public void state(String state)
    {
        if(!state.equals("online"))
        FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid()).child("state").setValue("last seen at "+state);
        else
            FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid()).child("state").setValue(state);



    }

    @Override
    protected void onResume() {
        super.onResume();
        state("online");
    }
    protected void onPause() {


        super.onPause();
        Calendar calendar=Calendar.getInstance();
        SimpleDateFormat sdf=new SimpleDateFormat("HH:mm:ss");

        state(sdf.format(calendar.getTime()));
    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainPage.this,MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                return true;
            case R.id.incognito:


                Toast.makeText(this, "incognito chats will now be displayed", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.incognito1:

                Toast.makeText(this, "incognito mode turned off", Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public class ViewPageAdapter extends FragmentStatePagerAdapter {
        int num;

        public ViewPageAdapter(@NonNull FragmentManager fm,int n) {
            super(fm);
            this.num=n;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new Chats();
                case 1:
                    return new UsersFragment();
                case 2:
                    return new UsersProfilr();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return num;
        }
    }



}
