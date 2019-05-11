package com.cyclicsoft.com.acitivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import dmax.dialog.SpotsDialog;
//import com.rengwuxian.materialedittext.MaterialEditText;

public class AdminActivity extends AppCompatActivity {

    Button btnSignIn;
    LinearLayout adminLayout;
    EditText   edtPassword, edtMail;
    FirebaseAuth auth;
    FirebaseDatabase db;
    DatabaseReference users;
    //public AlertDialog waitingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        auth = FirebaseAuth.getInstance();
        db   = FirebaseDatabase.getInstance();
        users= db.getReference("Users");

        adminLayout = (LinearLayout)findViewById(R.id.adminLayout);
        btnSignIn = (Button)findViewById(R.id.adminSignInID);
        edtMail = (EditText)findViewById(R.id.edtMail);
        edtPassword = (EditText)findViewById(R.id.edtPassword);

        btnSignIn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                showLoginDialog();
            }
        });


    }

    private void showLoginDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("SIGN IN");

        dialog.setMessage("Please use correct info to sign in");

//        LayoutInflater inflater = LayoutInflater.from(this);
//        View login_layout = inflater.inflate(R.layout.activity_admin, null);
//
//
//        dialog.setView(login_layout);
        dialog.setPositiveButton("SIGN IN", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i){
                dialogInterface.dismiss();


                //Disable signin button if processing
                btnSignIn.setEnabled(false);



                if(TextUtils.isEmpty(edtMail.getText().toString())){
                    Toast.makeText(getApplicationContext(),"Please Enter E-mail",Toast.LENGTH_SHORT).show();
//                    Snackbar.make(adminLayout, "Please Enter Phone Number", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(edtPassword.getText().toString())){
                    Toast.makeText(getApplicationContext(),"Please Enter Password",Toast.LENGTH_SHORT).show();
//                    Snackbar.make(adminLayout, "Please Enter Password", Snackbar.LENGTH_SHORT).show();
                    return;
                }
//                if(edtPassword.getText().toString().length()<6){
//                    Toast.makeText(getApplicationContext(),"Password too short",Toast.LENGTH_SHORT).show();
////                    Snackbar.make(adminLayout, "Password too short", Snackbar.LENGTH_SHORT).show();
//                    return;
//                }

                final android.app.AlertDialog writingDialog= new SpotsDialog.Builder().setContext(AdminActivity.this).build();//Changed------------------------
                writingDialog.show();


////                Login
//                auth.signInWithEmailAndPassword(edtMail.getText().toString(),edtPassword.getText().toString())
//                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
//                            @Override
//                            public void onSuccess(AuthResult authResult) {
//                                writingDialog.dismiss();
//                                Toast.makeText(getApplicationContext(),"LOGIN SUCCESSFUL!!",Toast.LENGTH_SHORT).show();
////                                Snackbar.make(adminLayout, "LOGIN SUCCESSFUL!!", Snackbar.LENGTH_SHORT).show();
////  ---------------------------------------------Share Location code have to write here----------------------------------------------------
//
//                                Intent successViewIntent = new Intent(AdminActivity.this,MapsActivity.class);
//                                startActivity(successViewIntent);
//                                finish();
//                            }
//                        }).addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                writingDialog.dismiss();
//                                Toast.makeText(getApplicationContext(),"LOGIN FAILED!!"+e.getMessage(),Toast.LENGTH_SHORT).show();
////                                Snackbar.make(adminLayout, "LOGIN FAILED!!"+e.getMessage(), Snackbar.LENGTH_SHORT).show();
//// Active button if failure
//                                btnSignIn.setEnabled(true);
//                            }
//                        });

// Login
                DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
                dbRef = dbRef.child("drivers2").child(edtMail.getText().toString());
                dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Log.d("pppp","Inside onData");
                        String data = (String)dataSnapshot.child("aID").getValue();
                        String data2 = edtPassword.getText().toString();
                        if (dataSnapshot.getValue() != null) {
                            Log.d("pppp","Inside onData tr"+dataSnapshot.toString());
                            Log.d("pppp","Inside onData tr"+(String)dataSnapshot.child("aID").getValue());

                            if( data.equalsIgnoreCase(data2)){

                                Log.d("pppp","Inside onData tt");

                                Toast.makeText(getApplicationContext(),"LOGIN Success!!",Toast.LENGTH_SHORT).show();

                                Intent successViewIntent = new Intent(AdminActivity.this,MapsActivity.class);
                                successViewIntent.putExtra("userid",data);
                                startActivity(successViewIntent);
                                finish();
                            }else {
                                writingDialog.dismiss();
                                Toast.makeText(getApplicationContext(),"LOGIN FAILED!!",Toast.LENGTH_SHORT).show();

                            }
                        }else {
                            writingDialog.dismiss();
                            Toast.makeText(getApplicationContext(),"LOGIN FAILED!!",Toast.LENGTH_SHORT).show();
                                //Snackbar.make(adminLayout, "LOGIN FAILED!!"+e.getMessage(), Snackbar.LENGTH_SHORT).show();
// Active button if failure
                                btnSignIn.setEnabled(true);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(AdminActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                    }
                });




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
