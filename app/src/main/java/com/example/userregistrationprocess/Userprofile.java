package com.example.userregistrationprocess;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Userprofile extends AppCompatActivity {

    private static final String TAG ="ProfileActivity";


//    CircleImageView ProfileImageView;
//    TextInputEditeText DisplayNameEditText;
//    Button UpdateProfileButton;
//
//    String DISPLAY_NAME = null;
//    String PROFILE_IMAGE_URL =null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userprofile);
        setTextFields();
//        profileImageView = findViewById(R.id.profileImageView);
//        displayNameEditText =findViewById(R.id.displayNameEditText);
//        updateProfileButton = findViewById(R.id.updateProfileButton);
//
//        firebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//
//        if (user != null) {
//            Log.d(TAG, "onCreate:" + user.getDisplayName());



 //       }

    }

    private void setTextFields () {
        FirebaseFirestore.getInstance().collection("users")
                .whereEqualTo("email", FirebaseAuth.getInstance().getCurrentUser().getEmail())
                .get()
                .addOnCompleteListener(Userprofile.this,

                        new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                DocumentSnapshot documentSnapshot = task.getResult()
                                        .getDocuments()
                                        .get(0);


                                Map  <String,Object> data =  documentSnapshot.getData();

                                ((TextView) findViewById(R.id.DisplayNameEditText)).setText(data.get("name").toString());
                                ((TextView) findViewById(R.id.Uphone)).setText(data.get("phone").toString());
                                ((TextView) findViewById(R.id.Uaddress)).setText(data.get("address").toString());
                            }
                        }

                );
    }


    public void onUpdateButtonPress (View view) {
        FirebaseFirestore.getInstance().collection("users")
                .whereEqualTo("email", FirebaseAuth.getInstance().getCurrentUser().getEmail())
                .get()
                .addOnCompleteListener(Userprofile.this,

                        new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                String id = task.getResult()
                                        .getDocuments()
                                        .get(0)
                                        .getId();

                                Map<String, Object> map = new HashMap();
                                map.put("name", ((TextView) findViewById(R.id.DisplayNameEditText)).getText().toString());
                                map.put("phone", ((TextView) findViewById(R.id.Uphone)).getText().toString());
                                map.put("address", ((TextView) findViewById(R.id.Uaddress)).getText().toString());


                                FirebaseFirestore.getInstance().collection("users")
                                        .document(id)
                                        .update(map);

                            }
                        }

                );



    }
}