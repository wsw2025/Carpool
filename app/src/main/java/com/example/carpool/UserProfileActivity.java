package com.example.carpool;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserProfileActivity extends AppCompatActivity {

    private FirebaseFirestore firestore;

    private FirebaseAuth mAuth;
    private String documentId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userprofile);

        firestore = FirebaseFirestore.getInstance();

        mAuth = FirebaseAuth.getInstance();

        // Retrieve the document ID from the intent
        documentId = mAuth.getCurrentUser().getUid();

        // Retrieve the document from Firestore
        firestore.collection("user-collection").document(documentId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null && document.exists()) {
                                // Access the information from the retrieved document
                                String email = document.getString("email");
                                String name = document.getString("name");
                                String userType = document.getString("userType");



                                // display details
                                TextView nameTextView = findViewById(R.id.nameView);
                                nameTextView.setText(name);

                                TextView emailTextView = findViewById(R.id.emailField);
                                emailTextView.setText("Email: "+ email);

                                TextView userTypeView = findViewById(R.id.userTypeField);
                                userTypeView.setText("User Type: " + userType);

                            }
                        } else {
                            Log.d("firebase", "Error getting document: ", task.getException());
                        }
                    }
                });
    }


    public void back(View v){
        Intent intent = new Intent(this, MainActivity.class );
        startActivity(intent);
    }
}
