package com.erenakgz.instagramclonejava.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.erenakgz.instagramclonejava.R;
import com.erenakgz.instagramclonejava.adapter.PostAdapter;
import com.erenakgz.instagramclonejava.databinding.ActivityFeedBinding;
import com.erenakgz.instagramclonejava.model.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Map;

public class FeedActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    ArrayList<Post> postArrayList;
    private ActivityFeedBinding binding;
    PostAdapter postAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFeedBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        postArrayList = new ArrayList<>();
        getData();
        binding.receyclerView.setLayoutManager(new LinearLayoutManager(this));
        postAdapter=new PostAdapter(postArrayList);
        binding.receyclerView.setAdapter(postAdapter);
    }

    private void getData() {
        firebaseFirestore.collection("Post").orderBy("date", Query.Direction.DESCENDING).addSnapshotListener((value, error) -> {
            if (error != null) {
                Toast.makeText(FeedActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            } else {
                if (value != null) {
                    for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                        Map<String, Object> data = documentSnapshot.getData();
                        String userEmail = (String) data.get("userEmail");
                        String downloadUrl = (String) data.get("downloadUrl");
                        String comment = (String) data.get("comment");
                        Post post = new Post(userEmail, comment, downloadUrl);
                        postArrayList.add(post);

                    }
                    postAdapter.notifyDataSetChanged(); //s
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) { //menüye bağlama işlemini yapıyoruz

        MenuInflater menuInflater = getMenuInflater(); //xml ile burdaki kodu bir birine bağlamak için kullanırız
        menuInflater.inflate(R.menu.option_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) { //seçim zamanı ne işlem yapacağımızı seçeriz
        if (item.getItemId() == R.id.add_post) {
            Intent intent = new Intent(FeedActivity.this, UploadActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.signout) {
            auth.signOut();
            Intent intent = new Intent(FeedActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}