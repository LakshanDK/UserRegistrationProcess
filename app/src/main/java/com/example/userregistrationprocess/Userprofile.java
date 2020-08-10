package com.example.userregistrationprocess;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Userprofile extends AppCompatActivity {

    private static final String TAG ="ProfileActivity";


    private Uri selectedImage;
    ImageView imageView1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userprofile);
        setTextFields();

        imageView1 = findViewById(R.id.ProfileImageView);

    }

    private void setImageField(String name) {
        FirebaseStorage.getInstance().getReference().child("UserPics/" + name).getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener() {
                    @Override
                    public void onSuccess(Object o) {
                        Uri uri = (Uri) o;
                        Glide.with(getApplicationContext()).load(uri).into(imageView1);
                    }
                });
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

                                try {
                                    String imgUri = data.get("image_name").toString();
                                    setImageField(imgUri);
                                }
                                catch(Exception ex) {

                                }


                            }
                        }

                );
    }

//update db when button pressed
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
// select image from gallery
    public void selectImage(View view) {
        final CharSequence[] options = {   "Choose from Gallery","Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(Userprofile.this);
        builder.setTitle("Choose your picture");

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Choose from Gallery")) {
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto , 1);

                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
                    if (resultCode == RESULT_OK && data != null) {
                            selectedImage = data.getData();

                        if (selectedImage != null) {
                            Uri imageUri = data.getData();
                            try {
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);

                                    imageView1.setImageBitmap(bitmap);
                                imageView1.setScaleType(ImageView.ScaleType.FIT_XY);
                                imageView1.setAdjustViewBounds(true);

                                    try {
                                        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                                        StorageReference reference;

                                        String timeString = new Timestamp(System.currentTimeMillis()).toString();

                                        reference = storageReference.child("UserPics/" + timeString +".jpg");
                                        reference.putFile(selectedImage);
                                        updateImgName(timeString + ".jpg");

                                        Toast.makeText( Userprofile.this, "Photo Updated", Toast.LENGTH_SHORT);
                                    }
                                    catch (Exception ex) {

                                    }

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
            }
        }
    }

    private void updateImgName (String iname) {
        final String name = iname;
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


                                FirebaseFirestore.getInstance().collection("users")
                                        .document(id)
                                        .update("image_name", name);

                            }
                        }

                );
    }
}