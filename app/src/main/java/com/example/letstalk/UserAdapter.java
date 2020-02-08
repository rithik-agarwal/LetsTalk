package com.example.letstalk;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    public Context mContext;
    public List<User> users;
    public boolean isChat;
    private OnItemClickListener mListener;
    public String lastmessage;
    public boolean seen;
    public String sender;

    public UserAdapter(Context con,List<User> user,boolean isChat)
    {
        this.mContext=con;
        this.users=user;
        this.isChat=isChat;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.userlayout,parent,false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        User user=users.get(position);
        holder.t.setText(user.getUsername());
        if(user.getDpurl().matches("default"))
            holder.img.setImageResource(R.mipmap.ic_launcher);
        else {
            Picasso.get().load(user.getDpurl()).resize(60,60).into(holder.img);
        }
        if(isChat) {
            if (!user.getState().equals("online"))
                holder.indicator.setVisibility(View.GONE);

        }else
            holder.indicator.setVisibility(View.GONE);
        if(isChat)
            call(user.getId(),holder.last);
        else
            holder.last.setVisibility(View.GONE);



    }

    @Override
    public int getItemCount() {
        return users.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        public TextView t;
        public ImageView img;
        public ImageView indicator;
        public TextView last;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            t=itemView.findViewById(R.id.text);
            img=itemView.findViewById(R.id.profile_img);
            indicator=itemView.findViewById(R.id.state);
            last=itemView.findViewById(R.id.last_msg);
            itemView.setOnClickListener(this);
            itemView.findViewById(R.id.profile_img).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.imageclick(getAdapterPosition());

                }
            });


        }

        @Override
        public void onClick(View view) {
            if(mListener != null)
                mListener.itemClick(getAdapterPosition());

        }
    }
    public void call(final String userid,final TextView last)
    {
        final FirebaseUser fuser= FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Chats");
        lastmessage="default";

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                lastmessage="No messages in this conversation";
                for(DataSnapshot snapshot:dataSnapshot.getChildren())
                {
                    Chat c=snapshot.getValue(Chat.class);
                    if(fuser != null) {
                        if ((c.getReceiver().equals(fuser.getUid()) && c.getSender().equals(userid)) || (c.getReceiver().equals(userid) && c.getSender().equals(fuser.getUid())))
                        {lastmessage = c.getMessage();
                        seen=c.getIsseen();
                        sender=c.getReceiver();}



                        }

                        }

                   if(fuser != null)
                       if(sender.matches(fuser.getUid()) && !seen)
                           last.setText("New Message:"+lastmessage);
                       else
                       {
                           last.setText(lastmessage);
                       }



                    }
                    @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public interface OnItemClickListener
    {
        void itemClick(int pos);
        void imageclick(int pos);



    }
    public void setOnItemClickListener(OnItemClickListener listener)
    {
        mListener=listener;
    }

}
