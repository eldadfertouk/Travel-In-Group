package com.mytracker.gpstracker.familytracker;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.mytracker.gpstracker.familytrackerfamilytracker.R;

public class RegisterPasswordActivity extends AppCompatActivity {

    EditText e1_password;
    Toolbar toolbar;
    Button b1_password;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_register_password);
        e1_password = (EditText)findViewById(R.id.editTextPassword);
        toolbar = (Toolbar)findViewById(R.id.toolbarPassword);
        b1_password = (Button)findViewById(R.id.buttonPassword);

        Intent intent = getIntent();
        if (intent!=null) {
             email = intent.getStringExtra("email");
        }




        b1_password.setEnabled(false);
        b1_password.setBackgroundColor(Color.parseColor("#faebd7"));
        toolbar.setTitle("Password");
        setSupportActionBar(toolbar);


        e1_password.addTextChangedListener(new TextWatcher() {
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

    public void goToNameActivity(View v)
    {
        if(e1_password.getText().toString().length()>=6)
        {
            // go to Name Activity
            Intent myIntent = new Intent(RegisterPasswordActivity.this,RegisterNameActivity.class);
            myIntent.putExtra("email",email);
            myIntent.putExtra("password",e1_password.getText().toString());
            startActivity(myIntent);
            finish();

        }
    }


}
