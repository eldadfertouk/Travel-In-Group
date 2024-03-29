package com.mytracker.gpstracker.travelingingroup;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class JoinedCirclesActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    Toolbar toolbar;

    RecyclerView.Adapter recycleradapter;
    RecyclerView.LayoutManager layoutManager;

    FirebaseAuth auth;
    DatabaseReference joinedReference;
    ArrayList<CreateUser> myList;
    FirebaseUser user;
    DatabaseReference usersReference;
    CreateUser createUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_joined_circles);
        recyclerView = findViewById(R.id.recyclerviewJoined);
        layoutManager = new LinearLayoutManager(this);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("מקושר לטיולים:");
        setSupportActionBar(toolbar);

        if(getSupportActionBar()!=null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        myList = new ArrayList<>();
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        joinedReference = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).child("JoinedCircles");
        usersReference = FirebaseDatabase.getInstance().getReference().child("Users");


        joinedReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                myList.clear();

                if(dataSnapshot.exists())
                {
                    for(DataSnapshot dss: dataSnapshot.getChildren())
                    {

                        String memberUserid = dss.child("circlememberid").getValue(String.class);

                        usersReference.child ( Objects.requireNonNull ( memberUserid ) ).addListenerForSingleValueEvent ( new ValueEventListener () {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                createUser = dataSnapshot.getValue(CreateUser.class);
                                //  Toast.makeText(getApplicationContext(),createUser.name,Toast.LENGTH_SHORT).show();
                                myList.add(createUser);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                               Toast.makeText(getApplicationContext(),databaseError.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        });


                    }

                   Toast.makeText(getApplicationContext(),"מקושר לטיולים אלה:",Toast.LENGTH_SHORT).show();
                    recycleradapter = new JoinedMembersAdapter(myList,getApplicationContext());
                    recyclerView.setAdapter(recycleradapter);
                    recycleradapter.notifyDataSetChanged();


                }
                else
                {
                   Toast.makeText(getApplicationContext(),"טרם הצטרפת לטיול",Toast.LENGTH_SHORT).show();
                    recyclerView.setAdapter(null);
                }




            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),databaseError.toString(),Toast.LENGTH_LONG).show();

            }
        });

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}
