package com.erenakgz.instagramclonejava.view;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.erenakgz.instagramclonejava.databinding.ActivityUploadBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.UUID;

public class UploadActivity extends AppCompatActivity {

    private ActivityUploadBinding binding;
    private FirebaseStorage firebaseStorage;
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;
    Uri imageData;
    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityResultLauncher<String> permissionLauncher;
    Bitmap bitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUploadBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        registerLauncher();

        firebaseStorage = FirebaseStorage.getInstance();
        auth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();
        storageReference=firebaseStorage.getReference();
    }

    public void UploadButton(View view) {
        if (imageData!=null){
            UUID uuid=UUID.randomUUID();
            String imageName="images/"+uuid+".jpg";
        storageReference.child(imageName).putFile(imageData).addOnSuccessListener(taskSnapshot -> {
            StorageReference newReferance=firebaseStorage.getReference(imageName);
            newReferance.getDownloadUrl().addOnSuccessListener(uri -> {
                String downloadUrl=uri.toString();
                String comment=binding.CommetText.getText().toString();
                FirebaseUser user=auth.getCurrentUser();
                String email=user.getEmail();
                HashMap<String,Object> postData=new HashMap<>();
                postData.put("userEmail",email);
                postData.put("downloadUrl",downloadUrl);
                postData.put("comment",comment);
                postData.put("date", FieldValue.serverTimestamp());
                firebaseFirestore.collection("Post").add(postData).addOnSuccessListener(documentReference -> {
                    finish();
                }).addOnFailureListener(e -> {
                    Toast.makeText(UploadActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                });

            });
        }).addOnFailureListener(e ->
                Toast.makeText(UploadActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show());
        }

    }

    public void selectImage(View view) {
        //izin kontrolünü sağladık
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Snackbar.make(view, "Permisson Needed For Gallery", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission", view1 -> {
                    //izin istenicek
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);

                }).show();
            } else {
            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        } else {
            Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            activityResultLauncher.launch(intentToGallery);

        }
    }

    private void registerLauncher() {
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK) {
                    Intent intentFromResult = result.getData();
                    if (intentFromResult != null) {
                        imageData = intentFromResult.getData();
                        binding.imageView.setImageURI(imageData);
                       /*
                       try {
                            if (Build.VERSION.SDK_INT>=28){
                                ImageDecoder.Source source=ImageDecoder.createSource(UploadActivity.this.getContentResolver(),imageData);
                                bitmap=ImageDecoder.decodeBitmap(source);
                                binding.imageView.setImageBitmap(bitmap);
                            }
                            else
                            {
                            bitmap=MediaStore.Images.Media.getBitmap(UploadActivity.this.getContentResolver(),imageData);

                            }
                       }
                       catch (Exception exception){

                       }
                       */

                    }
                }
            }
        });

        permissionLauncher=registerForActivityResult(new ActivityResultContracts.RequestPermission(), result -> {
            if (result){
                Intent intentToGallery=new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activityResultLauncher.launch(intentToGallery);
            }
            else
            {
                Toast.makeText(UploadActivity.this, "Permission needed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}