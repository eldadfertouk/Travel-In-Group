package com.mytracker.gpstracker.travelingingroup;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_main);
        auth = FirebaseAuth.getInstance();

        user = auth.getCurrentUser();


        if(user == null)
        {
            setContentView(R.layout.activity_main);


        }
        else
        {
            Intent myIntent = new Intent(MainActivity.this, MyTour.class);
            startActivity(myIntent);
            finish();

        }
    }


    public void getStarted_click(View v)
    {

        Intent myintent = new Intent(MainActivity.this,RegisterEmailActivity.class);
        startActivity(myintent);
        finish();

    }

    public void LoginUser(View v)
    {

        Intent myIntent = new Intent(MainActivity.this,LoginEmailActivity.class);
        startActivity(myIntent);
        finish();

    }




}
