package com.mytracker.gpstracker.familytracker;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mytracker.gpstracker.familytrackerfamilytracker.R;

public class InviteCodeActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextView t4_code;
    String name,email,password,date,issharing;
    String code = null;
    DatabaseReference reference;
    FirebaseAuth auth;
    FirebaseUser user;
    ProgressDialog dialog;
    TextView t6_done;
    StorageReference firebaseStorageReference;
    Uri resultUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_invite_code);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("קוד הצטרפות");
        dialog = new ProgressDialog(this);
        t6_done = findViewById(R.id.textView6);
        setSupportActionBar(toolbar);

        if(getSupportActionBar()!=null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }


        auth = FirebaseAuth.getInstance();
        reference= FirebaseDatabase.getInstance().getReference().child("Users");
        firebaseStorageReference = FirebaseStorage.getInstance().getReference().child("Profile_images");
        t4_code = findViewById(R.id.textView4);

        Intent intent = getIntent();
        if (intent!=null)
        {

            name = intent.getStringExtra("name");
            email = intent.getStringExtra("email");
            password = intent.getStringExtra("password");
            date = intent.getStringExtra("date");
            issharing = intent.getStringExtra("issharing");
            code = intent.getStringExtra("code");
             resultUri = intent.getParcelableExtra("imageUri");



        }

        if(code == null)
        {
            // check for code in firebase
            t6_done.setVisibility(View.GONE);

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    user = auth.getCurrentUser();
                    String user_code = dataSnapshot.child(user.getUid()).child("circlecode").getValue().toString();
                    t4_code.setText(user_code);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }
        else
        {
            t4_code.setText(code);
        }

    }

    public void sendCode(View v)
    {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_TEXT,"שלום, קוד ההצטרפות לטיול הוא:"+t4_code.getText().toString()+".אנא הירשם לטיול");
        startActivity( Intent.createChooser (i,"Share using:"));
    }

    public void Register(View v)
    {

        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("יוצר פרופיל משתמש חדש,אנא המתן");
        dialog.setCancelable(false);
        dialog.show();

        auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                   if(task.isSuccessful())
                   {
                       user = auth.getCurrentUser();
                       CreateUser createUser = new CreateUser(name,email,password,date,code,user.getUid(),"false","na","na","defaultimage");

                       reference.child(user.getUid()).setValue(createUser)
                               .addOnCompleteListener(new OnCompleteListener<Void>() {
                                   @Override
                                   public void onComplete(@NonNull Task<Void> task)
                                   {
                                        if(task.isSuccessful())
                                        {
                                            StorageReference filePath = firebaseStorageReference.child(user.getUid() + ".jpg");
                                            filePath.putFile(resultUri)
                                                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                                                            if(task.isSuccessful())
                                                            {
                                                                String downloadPath = task.getResult().getUploadSessionUri ().toString();
                                                                reference.child(user.getUid()).child("profile_image").setValue(downloadPath)
                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                if(task.isSuccessful())
                                                                                {
                                                                                    dialog.dismiss();
                                                                                // send email.
                                                                                    sendVerificationEmail();
                                                                                }
                                                                            }
                                                                        });

                                                            }
                                                            else
                                                            {
                                                                Toast.makeText(getApplicationContext(),"לא ניתן להעלות תמונה כעת",Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });







                                        }
                                   }
                               });
                   }
                   else
                   {
                      Toast.makeText(getApplicationContext(),"לא ניתן ליצור חשבון עתה, אין חיבור לאינטרנט , נסה מאוחר יותר",Toast.LENGTH_SHORT).show();
                   }

                    }
                });



    }

    public void sendVerificationEmail()
    {
        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                Toast.makeText(getApplicationContext(),"נשלח דואר לאימות חשבונך , אנא בדוק את הדואר ואמת החשבון",Toast.LENGTH_SHORT).show();
                                finish();
                                auth.signOut();

                                Intent myIntent = new Intent(InviteCodeActivity.this,MainActivity.class);
                                startActivity(myIntent);
                            }
                            else
                            {
                                overridePendingTransition(0, 0);
                                finish();
                                overridePendingTransition(0, 0);
                                startActivity(getIntent());

                            }
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
