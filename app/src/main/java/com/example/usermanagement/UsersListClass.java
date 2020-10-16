package com.example.usermanagement;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class UsersListClass extends RecyclerView.Adapter<UsersListClass.UserViewHolder> {

    private Context context;
    private ArrayList<UserBean> usersList;
    public String name;

    public UsersListClass(Context context, ArrayList<UserBean> usersList)
    {
        //super(context, R.layout.activity_users_list, usersList);
        this.context= context;
        this.usersList= usersList;
    }


    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UserViewHolder(LayoutInflater.from(context).inflate(R.layout.listview_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {

        holder.textViewName.setText(usersList.get(position).getName());
        holder.textViewUserName.setText(usersList.get(position).getUsername());
        holder.textViewEmail.setText(usersList.get(position).getEmail());
        holder.textViewPhone.setText(usersList.get(position).getPhone());
        name= usersList.get(position).getName();

    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public class UserViewHolder extends  RecyclerView.ViewHolder{
      //  ImageView profilePicture;
        TextView textViewName, textViewUserName, textViewEmail, textViewPhone;
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
          //  profilePicture = itemView.findViewById(R.id.profPic);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewUserName = itemView.findViewById(R.id.textViewUsername);
            textViewEmail= itemView.findViewById(R.id.textViewEmail);
            textViewPhone = itemView.findViewById(R.id.textViewPhone);
        }
    }

}


/* @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.listview_layout, null, true);
        TextView textViewName = listViewItem.findViewById(R.id.textViewName);
        TextView textViewUserName = listViewItem.findViewById(R.id.textViewUsername);
        TextView textViewEmail= listViewItem.findViewById(R.id.textViewEmail);
        TextView textViewPhone = listViewItem.findViewById(R.id.textViewPhone);

        //userN = usersList.get(position);

        textViewName.setText(userN.getName());
        textViewUserName.setText(userN.getUsername());
        textViewEmail.setText(userN.getEmail());
        textViewPhone.setText(userN.getPhone());

        return listViewItem;
    }*/