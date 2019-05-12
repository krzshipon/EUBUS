package com.cyclicsoft.com.acitivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.cyclicsoft.com.model.Admin;
import com.cyclicsoft.com.model.User;
import com.cyclicsoft.com.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminRegActivity extends AppCompatActivity {
    //LinearLayout rootLayout;
    LinearLayout adminRegLayout;
    EditText edtName, edtPhone, edtPassword,edtMail, edtID;
    FirebaseDatabase db;
    DatabaseReference admins;
    Button adminRegBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_reg);

        adminRegLayout = (LinearLayout)findViewById(R.id.adminRegLayout);
        db   = FirebaseDatabase.getInstance();
        admins= db.getReference("Admin");



        edtName = (EditText)findViewById(R.id.edtName);
        edtMail = (EditText)findViewById(R.id.edtMail);
        edtPhone = (EditText)findViewById(R.id.edtPhone);
        edtPassword = (EditText)findViewById(R.id.edtPassword);
        edtID =(EditText) findViewById(R.id.edtId);

        adminRegBtn = (Button)findViewById(R.id.adminRegBtn);

        adminRegBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                showRegisterDialog();
            }
        });


    }



    private void showRegisterDialog(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("REGISTER");
        dialog.setMessage("Please fill up all the fields to register");

//        LayoutInflater inflater = LayoutInflater.from(this);
//        View register_layout = inflater.inflate(R.layout.activity_admin_reg, null);
//
//        dialog.setView(register_layout);
        dialog.setPositiveButton("REGISTER", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i){
                dialogInterface.dismiss();

                if(TextUtils.isEmpty(edtName.getText().toString())){
                    Toast.makeText(getApplicationContext(),"Please Enter Name",Toast.LENGTH_SHORT).show();
//                    Snackbar.make(adminRegLayout, "Please Enter Name", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(edtMail.getText().toString())){
                    Toast.makeText(getApplicationContext(),"Please Enter Email",Toast.LENGTH_SHORT).show();
//                    Snackbar.make(adminRegLayout, "Please Enter Name", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(edtPhone.getText().toString())){
                    Toast.makeText(getApplicationContext(),"Please Enter Phone Number",Toast.LENGTH_SHORT).show();
//                    Snackbar.make(adminRegLayout, "Please Enter Phone Number", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(edtPassword.getText().toString())){
                    Toast.makeText(getApplicationContext(),"Please Enter Password",Toast.LENGTH_SHORT).show();
//                    Snackbar.make(adminRegLayout, "Please Enter Password", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if(edtPassword.getText().toString().length()<6){
                    Toast.makeText(getApplicationContext(),"password too short",Toast.LENGTH_SHORT).show();
//                    Snackbar.make(adminRegLayout, "Password too short", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                Admin admin = new Admin();
                admin.setaID(edtID.getText().toString());
                admin.setPassword(edtPassword.getText().toString());
                admin.setaEmail(edtMail.getText().toString());
                admin.setaName(edtName.getText().toString());
                admin.setPhone(edtPhone.getText().toString());
                admin.setLat("");
                admin.setLng("");


                admins.child(admin.getaID()).setValue(admin)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getApplicationContext(),"REGISTRATION SUCCESSFUL!!",Toast.LENGTH_SHORT).show();
//                                               Snackbar.make(adminRegLayout, "REGISTRATION SUCCESSFUL!!", Snackbar.LENGTH_SHORT).show();

                                 Intent successViewIntent = new Intent(AdminRegActivity.this,MainActivity.class);
                                               startActivity(successViewIntent);
                                           finish();

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(),"FAILED!!    "+e.getMessage(),Toast.LENGTH_SHORT).show();
//                                               Snackbar.make(adminRegLayout, "FAILED"+e.getMessage(), Snackbar.LENGTH_SHORT).show();

                            }
                        });







//                auth.createUserWithEmailAndPassword(edtMail.getText().toString(),edtPassword.getText().toString())
//                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
//                            @Override
//                            public void onSuccess(AuthResult authResult) {
//                                User user = new User();
//                                user.setName(edtName.getText().toString());
//                                user.setPhone(edtMail.getText().toString());
//                                user.setPhone(edtPhone.getText().toString());
//                                user.setPassword(edtPassword.getText().toString());
//
//                                users.child(user.getName()).setValue(user)
//                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                            @Override
//                                            public void onSuccess(Void aVoid) {
//                                                Toast.makeText(getApplicationContext(),"REGISTRATION SUCCESSFUL!!",Toast.LENGTH_SHORT).show();
////                                                Snackbar.make(adminRegLayout, "REGISTRATION SUCCESSFUL!!", Snackbar.LENGTH_SHORT).show();
//
//
//                                                Intent successViewIntent = new Intent(AdminRegActivity.this,MainActivity.class);
//                                                startActivity(successViewIntent);
//                                                finish();
//                                            }
//                                        })
//                                        .addOnFailureListener(new OnFailureListener() {
//                                            @Override
//                                            public void onFailure(@NonNull Exception e) {
//                                                Toast.makeText(getApplicationContext(),"FAILED!!    "+e.getMessage(),Toast.LENGTH_SHORT).show();
////                                                Snackbar.make(adminRegLayout, "FAILED"+e.getMessage(), Snackbar.LENGTH_SHORT).show();
//
//                                            }
//                                        });
//                            }
//                        })
//                        .addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                Toast.makeText(getApplicationContext(),"FAILED!!    "+e.getMessage(),Toast.LENGTH_SHORT).show();
////                                Snackbar.make(adminRegLayout, "FAILED"+e.getMessage(), Snackbar.LENGTH_SHORT).show();

  //                          }
    //                    });
            }
        });
        dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i){
                dialogInterface.dismiss();
            }
        });

        dialog.show();
    }





}
