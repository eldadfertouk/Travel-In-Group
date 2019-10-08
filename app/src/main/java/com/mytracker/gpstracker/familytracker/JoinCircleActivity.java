package com.mytracker.gpstracker.familytracker;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.goodiebag.pinview.Pinview;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mytracker.gpstracker.familytrackerfamilytracker.R;

public class JoinCircleActivity extends AppCompatActivity {

    Toolbar toolbar;
    Pinview pinView;
    DatabaseReference reference,currentReference;
    FirebaseAuth auth;
    FirebaseUser user;
    String currentUserId;
    DatabaseReference circleReference,joinedReference;
    String joinUserId;
    String current_userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_circle);
        toolbar = findViewById( R.id.toolbar);
        toolbar.setTitle("Join a Circle");
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();


        pinView = findViewById(R.id.mypinview);

        reference = FirebaseDatabase.getInstance().getReference().child("Users");
        currentReference = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());


        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }


        currentReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                current_userid = dataSnapshot.child("userid").getValue().toString();



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });



    }// end of onCreate

    public void getCode(View v)
    {

        // find the circle code owner name and email through his uid
        currentUserId = user.getUid();

        Query query = reference.orderByChild("circlecode").equalTo(pinView.getValue());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {

                    CreateUser createUser = null;
                    for(DataSnapshot childDss : dataSnapshot.getChildren())
                    {
                        createUser = childDss.getValue(CreateUser.class);
                    }
                    joinUserId = createUser.userid;

                    circleReference = FirebaseDatabase.getInstance().getReference().child("Users").child(joinUserId).child("CircleMembers");
                    joinedReference = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).child("JoinedCircles");


                    // get the correct values from the user



                    CircleJoin circleJoin = new CircleJoin(current_userid);
                    final CircleJoin circleJoin1 = new CircleJoin(joinUserId);

                    circleReference.child(user.getUid()).setValue(circleJoin)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {
                                        joinedReference.child(joinUserId).setValue(circleJoin1)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                       Toast.makeText(getApplicationContext(),"הצטרפת לטיול הזה בהצלחה ,טיול נעים!",Toast.LENGTH_SHORT).show();
                                                        finish();
                                                        Intent myIntent = new Intent(JoinCircleActivity.this, MyTour.class);
                                                        startActivity(myIntent);
                                                    }
                                                });




                                    }
                                    else
                                    {
                                        Toast.makeText(getApplicationContext(),"לא ניתו להצטרף לטיול,נסה מאוחר יותר",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });


                }
                else
                {
                  Toast.makeText(getApplicationContext(),"הקוד שהוזן אינו תקף או לא תקין",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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
