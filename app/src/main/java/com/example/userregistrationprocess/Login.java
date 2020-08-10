package com.example.userregistrationprocess;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    EditText mEmail,mPassword;
    Button mLoginBtn;
    TextView mCreateBtn,forgotTextLink;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmail       = findViewById(R.id.Email);
        mPassword    = findViewById(R.id.Password);
        mCreateBtn   = findViewById(R.id.createText); //deleted it design= android:onClick="createAccount"
        mLoginBtn    = findViewById(R.id.loginBtn);
        fAuth        = FirebaseAuth.getInstance();
        forgotTextLink = findViewById(R.id.fogetPassword);


//    public void createAccount(View v){
//        startActivity(new Intent(this, Register.class));


        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email    = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)){
                    mEmail.setError("Email is Required.");
                }

                if (TextUtils.isEmpty(password)){
                    mPassword.setError("Password is required.");
                }
//
//                if(password.length() < 6){
//                    mPassword.setError("Password Must be >= 6 Characters");
//                    return;
//                }
                // Authenticate the user
                fAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(Login.this, "Logged in Successfully ", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));

                        }else {
                            Toast.makeText(Login.this, "Error ! "+ task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            }
        });

        mCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();//logout
                startActivity(new Intent(Login.this, UserRegistration.class));
            }
        });

        forgotTextLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText resentMail = new EditText(view.getContext());
                AlertDialog.Builder passwordRestDialog = new AlertDialog.Builder(view.getContext());
                passwordRestDialog.setTitle("Reset Password");
                passwordRestDialog.setMessage("Enter Your Email To Received Reset Link");
                passwordRestDialog.setView(resentMail);

                passwordRestDialog.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override //i=which
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //extract the email and send rest link

                  String mail= resentMail.getText().toString();
                  fAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                      @Override
                      public void onSuccess(Void aVoid) {

                          Toast.makeText(getApplicationContext(), "Password was reset successfully",Toast.LENGTH_SHORT).show();

                      }
                  }).addOnFailureListener(new OnFailureListener() {
                      @Override
                      public void onFailure(@NonNull Exception e) {
                          Toast.makeText(Login.this,"Reset Link Sent Your Email." +e.getMessage(),Toast.LENGTH_SHORT).show();
                      }
                  });

                    }
                });

                passwordRestDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                          //close the dialog
                    }
                });
              passwordRestDialog.create().show();
            }
        });
    }
}