package com.mytracker.gpstracker.familytracker;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mytracker.gpstracker.familytrackerfamilytracker.R;

public class LoginPasswordActivity extends AppCompatActivity {

    Toolbar toolbar;
    EditText e1_pass;
    Button b1_password;
    FirebaseAuth auth;
    String email;
    ProgressDialog dialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_login_password);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Password");
        auth = FirebaseAuth.getInstance();
        dialog = new ProgressDialog(this);


        setSupportActionBar(toolbar);
        e1_pass = findViewById(R.id.editTextPass);
        b1_password = findViewById(R.id.button);

        Intent intent = getIntent();
        if (intent!=null)
        {
            email = intent.getStringExtra("email_login");
        }


        b1_password.setEnabled(false);
        b1_password.setBackgroundColor(Color.parseColor("#faebd7"));

        e1_pass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()>=6)
                {
                    b1_password.setEnabled(true);
                    b1_password.setBackgroundColor(Color.parseColor("#9C27B0"));
                }
                else
                {
                    b1_password.setEnabled(false);
                    b1_password.setBackgroundColor(Color.parseColor("#faebd7"));
                }
            }
        });
    }

    public void Login(View v)
    {
        dialog.setMessage("Please wait. Logging in.");
        dialog.show();
        if(e1_pass.getText().toString().length()>=6)
        {
            auth.signInWithEmailAndPassword(email,e1_pass.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful())
                                    {
                                        FirebaseUser user = auth.getCurrentUser();

                                        if(user.isEmailVerified())
                                        {
                                            dialog.dismiss();
                                            finish();
                                            Intent myIntent = new Intent(LoginPasswordActivity.this, MyTour.class);
                                            startActivity(myIntent);
                                        }
                                        else
                                        {
                                            dialog.dismiss();
                                            finish();
                                            FirebaseAuth.getInstance().signOut();
                                            Toast.makeText(getApplicationContext(),"כתובת דואר לא אומתה, לא ניתן להמשיך ללא אימות",Toast.LENGTH_SHORT).show();
                                            Intent myIntent = new Intent(LoginPasswordActivity.this,MainActivity.class);
                                            startActivity(myIntent);
                                        }
                                    }
                                    else
                                    {
                                        dialog.dismiss();
                                       Toast.makeText(getApplicationContext(),"שם משתמש או ססמה שגויים",Toast.LENGTH_SHORT).show();
                                    }
                        }
                    });





        }
    }
}
