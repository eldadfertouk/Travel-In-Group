package com.mytracker.gpstracker.travelingingroup;

import android.content.Context;
import android.content.Intent;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class HelpAlertsAdapter extends RecyclerView.Adapter<HelpAlertsAdapter.HelpAlertViewHolder> {
    private ArrayList<CreateUser> nameList;
    private Context c;

    HelpAlertsAdapter(ArrayList<CreateUser> nameList, Context c) {
        this.nameList = nameList;
        this.c = c;
    }

    @Override
    public int getItemCount() {
        return nameList.size ();
    }


    @NonNull
    @Override
    public HelpAlertViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from ( parent.getContext () ).inflate ( R.layout.alert_layout, parent, false );
        return new HelpAlertViewHolder ( view, c, nameList );

    }

    @Override
    public void onBindViewHolder(HelpAlertViewHolder holder, int position) {
        CreateUser addCircle = nameList.get ( position );

        holder.alertNameTxt.setText ( addCircle.name );
        holder.alertDateTxt.setText ( addCircle.date );
        Picasso.get ().load ( addCircle.profile_image ).placeholder ( R.drawable.defaultprofile ).into ( holder.alertImageView );


    }

    public static class HelpAlertViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
            , View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {
        View v;
        Context ctx;
        ArrayList<CreateUser> nameArrayList;
        CircleImageView alertImageView;
        TextView alertNameTxt, alertDateTxt;
        DatabaseReference myReference;
        FirebaseAuth auth;
        FirebaseUser user;

        HelpAlertViewHolder(View itemView, Context ctx, ArrayList<CreateUser> nameArrayList) {
            super ( itemView );
            this.v = itemView;
            itemView.setOnClickListener ( this );
            itemView.setOnCreateContextMenuListener ( this );
            this.nameArrayList = nameArrayList;
            this.ctx = ctx;
            auth = FirebaseAuth.getInstance ();
            user = auth.getCurrentUser ();
            myReference = FirebaseDatabase.getInstance ().getReference ().child ( "Users" ).child ( user.getUid () ).child ( "HelpAlerts" );
            alertImageView = itemView.findViewById ( R.id.alertImage );
            alertNameTxt = itemView.findViewById ( R.id.alertName );
            alertDateTxt = itemView.findViewById ( R.id.alertDate );
        }


        @Override
        public void onClick(View v) {
            int position = getAdapterPosition ();
            CreateUser addCircle = this.nameArrayList.get ( position );
            String latitude_user = addCircle.lat;
            String longitude_user = addCircle.lng;

            if (latitude_user.equals ( "na" ) && longitude_user.equals ( "na" )) {
                Toast.makeText ( ctx, "לא ניתן לקבל איתור בעת הזו, נסה שוב מאוחר יותר", Toast.LENGTH_SHORT ).show ();
            } else {
                Intent mYIntent = new Intent ( ctx, LiveMapActivity.class );
                // mYIntent.putExtra("createuserobject",addCircle);
                mYIntent.putExtra ( "latitude", latitude_user );
                mYIntent.putExtra ( "longitude", longitude_user );
                mYIntent.putExtra ( "name", addCircle.name );
                mYIntent.putExtra ( "userid", addCircle.userid );
                mYIntent.putExtra ( "date", addCircle.date );
                mYIntent.putExtra ( "image", addCircle.profile_image );
                mYIntent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK );
                ctx.startActivity ( mYIntent );
            }
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            int position = getAdapterPosition ();
            final CreateUser addCircle = this.nameArrayList.get ( position );
            myReference.child ( addCircle.userid ).removeValue ()
                    .addOnCompleteListener ( new OnCompleteListener<Void> () {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful ()) {
                                Toast.makeText ( ctx, "התראה הוסרה", Toast.LENGTH_SHORT ).show ();
                            } else {
                                Toast.makeText ( ctx, "לא ניתן להסיר התראה", Toast.LENGTH_SHORT ).show ();
                            }
                        }
                    } );
            return false;
        }
        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            MenuItem myActionItem = menu.add ( "REMOVE" );
            myActionItem.setOnMenuItemClickListener ( this );
        }
    }

}






