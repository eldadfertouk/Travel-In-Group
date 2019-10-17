package com.mytracker.gpstracker.travelingingroup;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static java.lang.Thread.MIN_PRIORITY;
import static java.lang.Thread.sleep;

public class SendHelpAlertsActivity extends AppCompatActivity {
    TextView t1_CounterTxt;
    int countValue = 5;
    Thread myThread;
    DatabaseReference circlereference, usersReference, joinedCirclesReference;
    FirebaseAuth auth;
    FirebaseUser user;
    String memberUserId;
    ArrayList<String> userIDsList;
    Button cancelButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_send_help_alerts );
        t1_CounterTxt = findViewById ( R.id.textView9 );
        cancelButton = findViewById ( R.id.ash_bt_cancel );
        cancelButton.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                setCancel ( view );
            }
        } );
        auth = FirebaseAuth.getInstance ();
        userIDsList = new ArrayList<> ();
        user = auth.getCurrentUser ();
        circlereference = FirebaseDatabase.getInstance ().getReference ().child ( "Users" ).child ( user.getUid () ).child ( "CircleMembers" );
        joinedCirclesReference = FirebaseDatabase.getInstance ().getReference ().child ( "Users" ).child ( user.getUid () ).child ( "JoinedCircles" );
        usersReference = FirebaseDatabase.getInstance ().getReference ().child ( "Users" );
        myThread = new Thread ( new ServerThread () );
        myThread.start ();
    }
    public void setCancel(View v) {
        Toast.makeText ( getApplicationContext (), "ההתראה בוטלה !!!", Toast.LENGTH_SHORT ).show ();
        Intent myIntent = new Intent ( SendHelpAlertsActivity.this, MyTour.class );
        startActivity ( myIntent );
        if (myThread.isAlive ()) {
            myThread.setPriority ( MIN_PRIORITY );
        }
        finish ();
    }
    private class ServerThread implements Runnable {
        @Override
        public void run() {
            try {//do some heavy task here on main separate thread like: Saving files in directory, any server operation or any heavy task
                ///Once this task done and if you want to update UI the you can update UI operation on runOnUiThread method like this:
                while (countValue != 0) {
                    sleep ( 1000 );
                    runOnUiThread ( new Runnable () {
                        @Override
                        public void run() {
                            t1_CounterTxt.setText ( String.valueOf ( countValue ) );
                            countValue = countValue - 1;
                        }
                    } );

                }
                runOnUiThread ( new Runnable () {
                    @Override
                    public void run() {
                        circlereference.addValueEventListener ( new ValueEventListener () {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                userIDsList.clear ();
                                for (DataSnapshot dss : dataSnapshot.getChildren ()) {
                                    memberUserId = dss.child ( "circlememberid" ).getValue ( String.class );
                                    userIDsList.add ( memberUserId );
                                }
                                if (userIDsList.isEmpty ()) {
                                    Toast.makeText ( getApplicationContext (), "אין מטיילים בקבוצה , הוסף מטיילים", Toast.LENGTH_SHORT ).show ();
                                } else {
                                    CircleJoin circleJoin = new CircleJoin ( user.getUid () );
                                    for (int i = 0; i < userIDsList.size (); i++) {
                                        usersReference.child ( userIDsList.get ( i ) ).child ( "HelpAlerts" ).child ( user.getUid () ).setValue ( circleJoin )
                                                .addOnCompleteListener ( new OnCompleteListener<Void> () {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful ()) {
                                                            Toast.makeText ( getApplicationContext (), "התראת חירום ואיתור נשלחה בהצלחה", Toast.LENGTH_SHORT ).show ();
                                                        } else {
                                                            Toast.makeText ( getApplicationContext (), "לא ניתן לשלוח התראה,בדוק חיבורים ונסה שוב", Toast.LENGTH_SHORT ).show ();
                                                        }
                                                    }
                                                } );
                                    }
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText ( getApplicationContext (), databaseError.getMessage (), Toast.LENGTH_SHORT ).show ();
                            }
                        } );
                    }
                } );
            } catch (Exception e) {
                Toast.makeText ( getApplicationContext (), "משהו השתבש בתהליך השליחה,לא נשלחו התראות,נסה ערוץ חירום אחר", Toast.LENGTH_SHORT ).show ();
            }
        }
    }

}
