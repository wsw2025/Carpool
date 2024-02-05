package com.example.carpool;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FieldValue;

/**
 * When user clicks on a specific vehicle in VehiclesInfoActivity, redirects to this page with all its details
 * User is also able to book the vehicle here.
 */

public class VehicleProfileActivity extends AppCompatActivity {
    private FirebaseFirestore firestore;
    private String documentId;

    private Long seatsLeft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_details);

        firestore = FirebaseFirestore.getInstance();

        // Retrieve the document ID from the intent
        documentId = getIntent().getStringExtra("documentId");

        // Retrieve the document from Firestore
        firestore.collection("vehicle-collection").document(documentId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null && document.exists()) {
                                // Access the information from the retrieved document
                                String vehicleInfo = document.getString("owner");
                                seatsLeft = document.getLong("seatsLeft");
                                Long basePrice = document.getLong("basePrice");
                                Long capacity = document.getLong("capacity");
                                String model = document.getString("model");
                                String vehicleType = document.getString("vehicleType");

                                // display details
                                TextView ownerTextView = findViewById(R.id.ownerTextView);
                                ownerTextView.setText(vehicleInfo);

                                TextView seatsLeftTextView = findViewById(R.id.seatsLeftTextView);
                                seatsLeftTextView.setText(String.valueOf(seatsLeft));

                                TextView basePriceTextView = findViewById(R.id.basePriceTextView);
                                basePriceTextView.setText(String.valueOf(basePrice));

                                TextView capacityTextView = findViewById(R.id.capacityTextView);
                                capacityTextView.setText(String.valueOf(capacity));

                                TextView modelTextView = findViewById(R.id.modelTextView);
                                modelTextView.setText(model);

                                TextView vehicleTypeTextView = findViewById(R.id.vehicleTypeTextView);
                                vehicleTypeTextView.setText(vehicleType);
                            }
                        } else {
                            Log.d("firebase", "Error getting document: ", task.getException());
                        }
                    }
                });
    }

    public void Back(View v){
        Intent intent = new Intent(this, VehiclesInfoActivity.class);
        startActivity(intent);
    }

    public void bookRide(View v) {


        // Update the seatsLeft field
        firestore.collection("vehicle-collection").document(documentId)
                .update("seatsLeft", FieldValue.increment(-1))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(
                                VehicleProfileActivity.this,
                                "Ride Booked!",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(
                        VehicleProfileActivity.this,
                        "Booking failed ): Try again Later",
                        Toast.LENGTH_SHORT
                        ).show();
                    }
                });

        if(seatsLeft==1){ //after updating it it will b 0 --> close the vehicle
            firestore.collection("vehicle-collection").document(documentId)
                    .update("open", false);
        }

        Intent intent = new Intent(this, VehiclesInfoActivity.class);
        startActivity(intent);
    }
}
