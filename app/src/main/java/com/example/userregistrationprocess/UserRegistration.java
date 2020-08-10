package com.example.userregistrationprocess;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.util.HashMap;
import java.util.Map;

public class UserRegistration extends AppCompatActivity {

    EditText mFullName,mEmail,mPassword,mPhone;
    Button mRegisterBtn;
    TextView mLoginBtn;
    FirebaseAuth fAuth;
    //ProgressBar progressBar;
    //last version
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registration);

        mFullName    = findViewById(R.id.FullName);
        mEmail       = findViewById(R.id.Email);
        mPassword    =findViewById(R.id.Password);
        mPhone       =findViewById(R.id.Phone);
        mRegisterBtn =findViewById(R.id.loginBtn);
        mLoginBtn    =findViewById(R.id.createText);

        fAuth = FirebaseAuth.getInstance();
        //progressBar = FindViewByID();
        // Check if user is already logged in
        if(fAuth.getCurrentUser() !=null){
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        }

        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)){
                    mEmail.setError("Email is Required.");
                }

                if (TextUtils.isEmpty(password)){
                    mPassword.setError("Password is required.");
                }

                if(password.length() < 6){
                    mPassword.setError("Password Must be >= 6 Characters");
                    return;
                }

                //progressBar.setVisibility(View.VISIBLE);

                //register the user in Firebase.
                fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(UserRegistration.this, "User Created ", Toast.LENGTH_SHORT).show();

                            createUserRecord(email);

                            startActivity(new Intent(getApplicationContext(),MainActivity.class));

                        }else {
                            Toast.makeText(UserRegistration.this, "Error ! "+ task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    }
                });


            }
        });
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Login.class));
            }
        });


    }

    private void createUserRecord(String email ) {

        Map<String,Object> docData = new HashMap();

        docData.put("name", mFullName.getText().toString());
        docData.put("email", email);
        docData.put("image_name", "");
        docData.put("phone", mPhone.getText().toString());
        docData.put("address", "");

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);

        db.collection("users").document()
                .set(docData);


    }
}