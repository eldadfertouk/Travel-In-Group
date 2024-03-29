package com.mytracker.gpstracker.travelingingroup;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;

import java.util.Objects;

public class LoginEmailActivity extends AppCompatActivity {

    EditText e1_email;
    Toolbar toolbar;
    Button b1_emailnext;
    ProgressDialog dialog;
    FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_login_email);
        e1_email = findViewById(R.id.editTextPass);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Sign In");
        setSupportActionBar(toolbar);
        dialog = new ProgressDialog(this);




        auth = FirebaseAuth.getInstance();
        b1_emailnext = findViewById(R.id.button);
        b1_emailnext.setEnabled(false);
        b1_emailnext.setBackgroundColor(Color.parseColor("#faebd7"));
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

    public void checkEmail(View v)
    {
        dialog.setMessage("Please wait!");
        dialog.show();
       auth.fetchSignInMethodsForEmail (e1_email.getText().toString())
               .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                   @Override
                   public void onComplete(@androidx.annotation.NonNull Task<SignInMethodQueryResult> task) {
                       boolean check = task.isSuccessful ();
                       if(!check){
                           dialog.dismiss();
                           Toast.makeText(getApplicationContext(),"This email does not exist. Please create an account first",Toast.LENGTH_SHORT).show();

                       }
                       else {
                           // go to password login
                           dialog.dismiss();
                           Intent myIntent = new Intent(LoginEmailActivity.this,LoginPasswordActivity.class);
                           myIntent.putExtra("email_login",e1_email.getText().toString());
                           startActivity(myIntent);
                           finish();
                       }
                   }
/*
                   @Override
                   public void onComplete(@NonNull Task<ProviderQueryResult> task) {
                       boolean check = !task.getResult().getProviders().isEmpty();
                       if(!check)
                       {
                           dialog.dismiss();
                         Toast.makeText(getApplicationContext(),"This email does not exist. Please create an account first",Toast.LENGTH_SHORT).show();

                       }
                       else
                       {
                           // go to password login
                           dialog.dismiss();
                           Intent myIntent = new Intent(LoginEmailActivity.this,LoginPasswordActivity.class);
                           myIntent.putExtra("email_login",e1_email.getText().toString());
                           startActivity(myIntent);
                           finish();


                       }
                   }*/
               });

    }

    public void forgotPassword(View v)
    {
        final EditText taskEditText = new EditText(this);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Add a new task")
                .setMessage("What do you want to do next?")
                .setView(taskEditText)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String task = String.valueOf(taskEditText.getText());
                        FirebaseAuth.getInstance().sendPasswordResetEmail(task)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(LoginEmailActivity.this,"Please check your email",Toast.LENGTH_LONG).show();
                                        }
                                        else {
                                            Toast.makeText ( LoginEmailActivity.this, Objects.requireNonNull ( task.getException () ).getMessage (), Toast.LENGTH_LONG ).show ();
                                        }
                                    }
                                });

                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
    }


}
