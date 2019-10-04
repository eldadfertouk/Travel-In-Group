package com.mytracker.gpstracker.familytracker;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.ProviderQueryResult;
import com.mytracker.gpstracker.familytrackerfamilytracker.R;

public class RegisterEmailActivity extends AppCompatActivity {

    Toolbar toolbar;
    EditText e1_email;
    Button b1_emailnext;
    ProgressDialog dialog;
    FirebaseAuth auth;

        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email);
        toolbar = findViewById( R.id.toolbar);
        e1_email = findViewById(R.id.editTextPass);
            auth = FirebaseAuth.getInstance();
        dialog = new ProgressDialog(this);

        b1_emailnext = findViewById(R.id.button);
        b1_emailnext.setEnabled(false);
        b1_emailnext.setBackgroundColor(Color.parseColor("#faebd7"));
        toolbar.setTitle("Email Address");
        setSupportActionBar(toolbar);

        final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        e1_email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                    if(e1_email.getText().toString().matches(emailPattern) && s.length() > 0)
                    {
                        b1_emailnext.setEnabled(true);
                        b1_emailnext.setBackgroundColor(Color.parseColor("#9C27B0"));

                    }
                    else
                    {
                        b1_emailnext.setEnabled(false);
                        b1_emailnext.setBackgroundColor(Color.parseColor("#faebd7"));
                    }

            }
        });


    }


    public void checkIfEmailPresent(View v)
    {
        dialog.setMessage("Please wait");
           dialog.show();

        auth.fetchProvidersForEmail(e1_email.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<ProviderQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<ProviderQueryResult> task) {
                        dialog.dismiss();
                        boolean check = !task.getResult().getProviders().isEmpty();

                        if(!check)
                        {
                            Intent myIntent = new Intent(RegisterEmailActivity.this,RegisterPasswordActivity.class);
                            myIntent.putExtra("email",e1_email.getText().toString());
                            startActivity(myIntent);
                            finish();

                        }
                        else
                        {
                          Toast.makeText(getApplicationContext(),"כבר יש חשבון כזה. נסה כניסה ישירה.",Toast.LENGTH_SHORT).show();
                            Intent myIntent = new Intent(RegisterEmailActivity.this,LoginEmailActivity.class);
                            startActivity(myIntent);
                            finish();




                        }
                    }
                });


    }



}
