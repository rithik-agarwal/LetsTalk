package com.example.letstalk;

import android.content.Context;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder>
{
    public Context mContext;
    public static final int msg_left=0;
    public static final int msg_right=1;
    public OnItemClickListener mlistener;
    public List<Chat> chats;
    public String imageurl;

    FirebaseUser firebaseUser;


    public MessageAdapter(Context con,List<Chat> chats,String imageurl)
    {
        this.mContext=con;
        this.chats=chats;
        this.imageurl=imageurl;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==msg_right)
        {
            View view=LayoutInflater.from(mContext).inflate(R.layout.chat_right,parent,false);
            return new MessageAdapter.ViewHolder(view);
        }
        else
        {
            View view=LayoutInflater.from(mContext).inflate(R.layout.chat_left,parent,false);
            return new MessageAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Chat chat=chats.get(position);
        holder.t.setText(chat.getMessage());
        if(imageurl.equals("default"))
        {
            holder.img.setImageResource(R.mipmap.ic_launcher);
        }
        else
        {
            Picasso.get().load(imageurl).resize(40,40).into(holder.img);
        }
        if(position==chats.size()-1)
        {
            if(chat.getIsseen())
                holder.state.setText("seen");
            else
                holder.state.setText("delivered");
        }
        else
        {
            holder.state.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener,View.OnClickListener
   {
       public TextView t;
       public ImageView img;
       public TextView state;

       public ViewHolder(@NonNull View itemView) {
           super(itemView);
           t=itemView.findViewById(R.id.msg1);
           img=itemView.findViewById(R.id.chat_image);
           state=itemView.findViewById(R.id.seen);
           itemView.setOnClickListener(this);
           itemView.setOnCreateContextMenuListener(this);
       }

       @Override
       public boolean onMenuItemClick(MenuItem menuItem) {
           if(mlistener != null)
               if(mlistener != null)
               {
                   int position=getAdapterPosition();
                   if(position != RecyclerView.NO_POSITION)
                   {
                       switch (menuItem.getItemId())
                       {

                           case 1:mlistener.onDelete(position);
                               return true;
                       }
                   }
               }



           return false;
       }

       @Override
       public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
           contextMenu.setHeaderTitle("Select Action");

           MenuItem delete=contextMenu.add(Menu.NONE,1,1,"Delete");

           delete.setOnMenuItemClickListener(this);

       }

       @Override
       public void onClick(View view) {
           int position=getAdapterPosition();
           mlistener.click(position);


       }
   }

    @Override
    public int getItemViewType(int position) {
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        if(chats.get(position).getSender().equals(firebaseUser.getUid()))
            return msg_right;
        else
            return msg_left;

    }
    public interface OnItemClickListener
    {


        void onDelete(int pos);
        void click(int pos);



    }
    public void setOnItemClickListener(OnItemClickListener listener)
    {
        mlistener=listener;
    }

}