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

public class AlertCenterActivity extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView recyclerView;
    RecyclerView.Adapter recyclerAdapter;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<CreateUser> myList;
    FirebaseAuth auth;
    FirebaseUser user;
    String memberUserId;

    CreateUser createUser;
    DatabaseReference reference, userReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_alert_center );
        recyclerView = findViewById ( R.id.alertRecyclerView );
        layoutManager = new LinearLayoutManager ( this );
        auth = FirebaseAuth.getInstance ();
        user = auth.getCurrentUser ();
        toolbar = findViewById ( R.id.toolbar );
        toolbar.setTitle ( "מסך שירותי חירום והצלה" );
        setSupportActionBar ( toolbar );
        reference = FirebaseDatabase.getInstance ().getReference ().child ( "Users" ).child ( user.getUid () ).child ( "HelpAlerts" );
        userReference = FirebaseDatabase.getInstance ().getReference ().child ( "Users" );
        myList = new ArrayList<> ();
        if (getSupportActionBar () != null) {
            getSupportActionBar ().setDisplayHomeAsUpEnabled ( true );
            getSupportActionBar ().setDisplayShowHomeEnabled ( true );
        }
        recyclerView.setLayoutManager ( layoutManager );
        recyclerView.setHasFixedSize ( true );
        reference.addValueEventListener ( new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                myList.clear ();
                if (dataSnapshot.exists ()) {
                    for (DataSnapshot dss : dataSnapshot.getChildren ()) {
                        memberUserId = Objects.requireNonNull ( Objects.requireNonNull ( dss.child ( "circlememberid" ).getValue () ) ).toString ();
                        userReference.child ( memberUserId ).addListenerForSingleValueEvent ( new ValueEventListener () {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                createUser = dataSnapshot.getValue ( CreateUser.class );
                                myList.add ( createUser );
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText ( getApplicationContext (), databaseError.getCode (), Toast.LENGTH_SHORT ).show ();
                            }
                        } );
                    }
                    Toast.makeText ( getApplicationContext (), "מציג התראות חירום", Toast.LENGTH_LONG ).show ();
                    recyclerAdapter = new HelpAlertsAdapter ( myList, getApplicationContext () );
                    recyclerView.setAdapter ( recyclerAdapter );
                    recyclerAdapter.notifyDataSetChanged ();
                } else {
                    Toast.makeText ( getApplicationContext (), "רשימת התראות חירום ריקה", Toast.LENGTH_SHORT ).show ();
                    recyclerView.setAdapter ( null );
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText ( getApplicationContext (), databaseError.getMessage (), Toast.LENGTH_SHORT ).show ();
            }
        } );

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId () == android.R.id.home)
            finish ();
        return super.onOptionsItemSelected ( item );
    }
}
