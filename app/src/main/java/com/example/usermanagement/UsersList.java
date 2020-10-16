package com.example.usermanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UsersList extends AppCompatActivity  {

    String  name, id;

    TextView a;
    DatabaseReference ref;
    UserBean user;
    RecyclerView recyclerView;
    ArrayList<UserBean> userList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_list);


        recyclerView= (RecyclerView) findViewById(R.id.recylerView);
        recyclerView.setLayoutManager( new LinearLayoutManager(this));
        ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList= new ArrayList<UserBean>();
                for ( DataSnapshot userSnapshot: snapshot.getChildren())
                {
                    user = userSnapshot.getValue(UserBean.class);
                    userList.add(user);
                }
                UsersListClass adapter = new UsersListClass(UsersList.this, userList);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UsersList.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}