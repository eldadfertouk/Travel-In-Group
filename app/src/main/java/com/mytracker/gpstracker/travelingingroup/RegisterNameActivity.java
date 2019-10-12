package com.mytracker.gpstracker.travelingingroup;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.theartofdev.edmodo.cropper.CropImage;

import java.util.Date;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterNameActivity extends AppCompatActivity {

    Toolbar toolbar;
    EditText e1;
    CircleImageView circleImageView;
    Button b1;
    String email,password;

    Uri resultUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_register_name);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("פרופיל המשתמש שלך");
        setSupportActionBar(toolbar);
        e1 = findViewById(R.id.editTextPass);
        b1 = findViewById(R.id.button);




        b1.setEnabled(false);
        b1.setBackgroundColor(Color.parseColor("#faebd7"));
        circleImageView = findViewById(R.id.profile_image);

        Intent intent = getIntent();
        if (intent!=null) {
            email = intent.getStringExtra("email");
            password = intent.getStringExtra("password");

        }



        e1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                    if(s.length()>0)
                    {
                        b1.setEnabled(true);
                        b1.setBackgroundColor(Color.parseColor("#9C27B0"));
                    }
                    else
                    {
                        b1.setEnabled(false);
                        b1.setBackgroundColor(Color.parseColor("#faebd7"));
                    }
            }
        });


    }

    public void generateCode(View v) {

        if (e1.getText().toString().length() > 0) {
            Date curDate = new Date();


            Random rnd = new Random();
            int n = 100000 + rnd.nextInt(900000);


            final String code = String.valueOf(n);


            if(resultUri !=null)
            {
                Intent myIntent = new Intent(RegisterNameActivity.this, InviteCodeActivity.class);
                myIntent.putExtra("name", e1.getText().toString());
                myIntent.putExtra("email", email);
                myIntent.putExtra("password", password);
                myIntent.putExtra("date", "na");
                myIntent.putExtra("issharing", "false");
                myIntent.putExtra("code", code);

                myIntent.putExtra("imageUri",resultUri);
                startActivity(myIntent);
                Toast.makeText(getApplicationContext(),resultUri.toString(),Toast.LENGTH_SHORT).show();
                finish();


            }
            else
            {
               Toast.makeText(getApplicationContext(),"חובה לבחור תמונת פרופיל",Toast.LENGTH_SHORT).show();
            }

        }
    }



    public void openGallery(View v)
    {

        Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhotoIntent, 12);


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);



        if(requestCode == 12 && resultCode==RESULT_OK && data!=null)
        {
                Uri uri = data.getData();
                CropImage.activity(uri)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                 resultUri = result.getUri();
                circleImageView.setImageURI(resultUri);



            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();

            }
        }





    }
}
