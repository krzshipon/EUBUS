package com.cyclicsoft.com.acitivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.cyclicsoft.com.R;
import com.cyclicsoft.com.model.Admin;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import dmax.dialog.SpotsDialog;
//import com.rengwuxian.materialedittext.MaterialEditText;

public class AdminLoginActivity extends AppCompatActivity {

    //button for admin login
    Button btnSignIn;

    // Field editText
    EditText   edtPassword, edtID;

    // Firebase connect
    DatabaseReference admins;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        // Call for initialization
        initFields();

        btnSignIn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                showLoginDialog();
            }
        });
    }

    /**
     * Ui i nitialize
     */
    private void initFields() {
        try {
            // Firebase database ref
            admins = FirebaseDatabase.getInstance().getReference().child("Admin");
        }catch (Exception e){
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        // button for sign in
        btnSignIn = (Button)findViewById(R.id.bt_admin_login);
        // Fields
        edtID = (EditText)findViewById(R.id.edt_admin_login_id);
        edtPassword = (EditText)findViewById(R.id.edt_admin_login_pass);

    }

    private void showLoginDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("SIGN IN");
        dialog.setMessage("Please use correct info to sign in");

        dialog.setPositiveButton("SIGN IN", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i){
                dialogInterface.dismiss();

                //Disable signin button if processing
                btnSignIn.setEnabled(false);

                if(TextUtils.isEmpty(edtID.getText().toString())){
                    Toast.makeText(getApplicationContext(),"Please Enter E-mail",Toast.LENGTH_SHORT).show();
//                    Snackbar.make(adminLayout, "Please Enter Phone Number", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(edtPassword.getText().toString())){
                    Toast.makeText(getApplicationContext(),"Please Enter Password",Toast.LENGTH_SHORT).show();
//                    Snackbar.make(adminLayout, "Please Enter Password", Snackbar.LENGTH_SHORT).show();
                    return;
                }


                final android.app.AlertDialog writingDialog= new SpotsDialog.Builder().setContext(AdminLoginActivity.this).build();//Changed------------------------
                writingDialog.show();


// Login
                try{
                admins = admins.child(edtID.getText().toString());
                admins.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {

                            Admin admin = dataSnapshot.getValue(Admin.class);

                            if( admin.getaID().equals(edtID.getText().toString()) && admin.getPassword().equals(edtPassword.getText().toString())){

                                Toast.makeText(getApplicationContext(),"LOGIN Success!!",Toast.LENGTH_SHORT).show();
                                Intent successViewIntent = new Intent(AdminLoginActivity.this,MapsActivity.class);
                                successViewIntent.putExtra("userid",admin.getaID());
                                startActivity(successViewIntent);
                                finish();
                            }else {
                                writingDialog.dismiss();
                                Toast.makeText(getApplicationContext(),"LOGIN FAILED!! Please check id & pass.",Toast.LENGTH_SHORT).show();

                            }
                        }else {
                            writingDialog.dismiss();
                            Toast.makeText(getApplicationContext(),"LOGIN FAILED!! ",Toast.LENGTH_SHORT).show();
                             // Active button if failure
                            btnSignIn.setEnabled(true);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(AdminLoginActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                    }
                });

                }catch (Exception e){
                    Toast.makeText(AdminLoginActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }



            }
        });

        dialog.setNegativeButton("CANCEL", new  DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialoginterface, int i){
                dialoginterface.dismiss();
            }
        });
        dialog.show();


    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu3,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id3 = item.getItemId();

        if(id3==R.id.adminActivityId){
            return true;
        }

        if(id3==R.id.developerInfoId){
            Intent adminViewIntent = new Intent(this,DeveloperInfoActivity.class);
            this.startActivity(adminViewIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
