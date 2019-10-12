package com.mytracker.gpstracker.travelingingroup;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyTour extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks
        , GoogleApiClient.OnConnectionFailedListener, LocationListener {

    GoogleMap mMap;
    GoogleApiClient client;
    LocationRequest request;
    FirebaseAuth auth;
    FirebaseUser user;
    LatLng latLngCurrent;
    DatabaseReference reference;
    TextView textName, textEmail;
    Marker marker;
    CircleImageView circleImageView;
    String myName, myEmail, myDate, mySharing,myProfileImage;



    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_my_tour );
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Travel In Group");
        setSupportActionBar(toolbar);

        auth = FirebaseAuth.getInstance();


        user = auth.getCurrentUser();
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);

        textName = header.findViewById(R.id.nameTxt);
        textEmail = header.findViewById(R.id.emailTxt);
        circleImageView = header.findViewById(R.id.imageView2);

           //aSwitch.setOnCheckedChangeListener(getApplicationContext());

        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager ().findFragmentById(R.id.frag_map);
        Objects.requireNonNull ( mapFragment ).getMapAsync ( this );



        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions( MyTour.this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        1000);
            }

        }




        reference = FirebaseDatabase.getInstance().getReference().child("Users");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                try {
                    myDate = Objects.requireNonNull ( dataSnapshot.child ( user.getUid () ).child ( "date" ).getValue () ).toString ();
                    mySharing = Objects.requireNonNull ( dataSnapshot.child ( user.getUid () ).child ( "issharing" ).getValue () ).toString ();
                    myEmail = Objects.requireNonNull ( dataSnapshot.child ( user.getUid () ).child ( "email" ).getValue () ).toString ();
                    myName = Objects.requireNonNull ( dataSnapshot.child ( user.getUid () ).child ( "name" ).getValue () ).toString ();
                    myProfileImage = Objects.requireNonNull ( dataSnapshot.child ( user.getUid () ).child ( "profile_image" ).getValue () ).toString ();

                    textName.setText(myName);
                    textEmail.setText(myEmail);
                    Picasso.get().load(myProfileImage).placeholder(R.drawable.defaultprofile).into(circleImageView);
                }catch(NullPointerException e)
                {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),"אין חיבור תקין לאינטרנט,בדוק חיבורים או נסה מאוחר יותר",Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            Toast.makeText(getApplicationContext(),databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });



    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            finish();

        }
    }




    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.signout) {
            if (user != null) {
                auth.signOut();
                finish();

                Intent myIntent2 = new Intent( MyTour.this,LocationShareService.class);
                stopService(myIntent2);

                Intent i = new Intent( MyTour.this, MainActivity.class);
                startActivity(i);
            }
        } else if (id == R.id.joinCircle)
        {
            Intent myIntent = new Intent( MyTour.this, JoinCircleActivity.class);
            startActivity(myIntent);
            }




         if (id == R.id.myCircle) {
             Intent intent = new Intent ( MyTour.this, MyCircleActivity.class );
             startActivity ( intent );

         }

         if (id == R.id.inviteFriends)
        {
            Intent myIntent = new Intent( MyTour.this, InviteCodeActivity.class);
            startActivity(myIntent);
            }

        if (id == R.id.joinedCircle)
        {
            Intent myIntent = new Intent( MyTour.this, JoinedCirclesActivity.class);
            startActivity(myIntent);
        }




         if(id == R.id.sendHelpAlert) {
            Intent myIntent = new Intent( MyTour.this, SendHelpAlertsActivity.class);
            startActivity(myIntent);
        }

        else if(id == R.id.alertCenter)
        {
            Intent myIntent = new Intent( MyTour.this,AlertCenterActivity.class);
            startActivity(myIntent);


        }



        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        client = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        client.connect();

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        request = new LocationRequest().create();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setInterval(7000);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(client, request, this);


    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        LocationServices.FusedLocationApi.removeLocationUpdates(client, this);


        latLngCurrent = new LatLng(location.getLatitude(), location.getLongitude());
        if (marker == null) {
            marker = mMap.addMarker(new MarkerOptions().position(latLngCurrent).title("Current Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngCurrent, 15));


        } else {
            marker.setPosition(latLngCurrent);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngCurrent, 15));
        }


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_send:

                if(isServiceRunning(getApplicationContext(),LocationShareService.class))
                {
                    Toast.makeText(getApplicationContext(),"You are already sharing your location.",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Intent myIntent = new Intent( MyTour.this,LocationShareService.class);
                    startService(myIntent);
                }


                break;
            case R.id.action_stop:
                Intent myIntent2 = new Intent( MyTour.this,LocationShareService.class);
                stopService(myIntent2);
                reference.child(user.getUid()).child("issharing").setValue("false")
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful())
                                {
                                    Toast.makeText(getApplicationContext(),"Location sharing is now stopped",Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    Toast.makeText(getApplicationContext(),"Location sharing could not be stopped",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_tour,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode==1000)
        {
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(getApplicationContext(),"Location permission granted. Thankyou.",Toast.LENGTH_SHORT).show();
                onConnected(null);

            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void inviteMembers(View v)
    {
        Intent myIntent = new Intent( MyTour.this,InviteCodeActivity.class);
        startActivity(myIntent);

    }


    public boolean isServiceRunning(Context c,Class<?> serviceClass)
    {
        ActivityManager activityManager = (ActivityManager)c.getSystemService(Context.ACTIVITY_SERVICE);


        List<ActivityManager.RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);



        for(ActivityManager.RunningServiceInfo runningServiceInfo : services)
        {
            if(runningServiceInfo.service.getClassName().equals(serviceClass.getName()))
            {
                return true;
            }
        }

        return false;


    }






}
