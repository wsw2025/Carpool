package com.example.carpool;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.core.view.WindowCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.Objects;
import java.util.UUID;


public class AddVehicleActivity extends AppCompatActivity {

    private Spinner vehicle;
    private EditText model;
    private EditText priceField;
    private EditText capacity;
    private Spinner time;
    private EditText locationField;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vehicle);

        vehicle = findViewById(R.id.vehicle_type_spinner);
        model = findViewById(R.id.modelEditText);
        priceField = findViewById(R.id.price_edit_text);
        capacity = findViewById(R.id.capacity_edit_text);
        time = findViewById(R.id.pickup_time_spinner);
        locationField = findViewById(R.id.addressEditText);
    }

    public void addVehicle(View v){
        if(vehicle!=null && priceField!=null && time!=null && locationField!=null){
            String vehicleType = (String) vehicle.getSelectedItem();

            Vehicle newVehicle;
            switch (vehicleType) {
                case "Car":
                    newVehicle = new Car();
                    break;
                case "Helicopter":
                    newVehicle = new Helicopter();
                    break;
                case "Segway":
                    newVehicle = new Segway();
                    break;
                case "Bicycle":
                    newVehicle = new Bicycle();
                    break;
                default:
                    newVehicle = null;
                    break;
            }

            Log.e("WORK PLEASE", mAuth.getCurrentUser().getUid());
            firestore.collection("user-collection").document(mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (!task.isSuccessful()) {
                        Log.e("firebase", "Error getting data", task.getException());
                    }
                    else {
                        Log.e("firebase", "idk man");
                        DocumentSnapshot ds = task.getResult();
                        User myUser = ds.toObject(User.class);
                        Log.d("firebase", myUser.getName());
                        newVehicle.setOwner(myUser.getName());
                        newVehicle.setVehicleType(vehicleType);
                        newVehicle.setVehicleID(String.valueOf(UUID.randomUUID()));
                        newVehicle.setBasePrice(Double.parseDouble(priceField.getText().toString()));
                        int capacityValue = Integer.parseInt(String.valueOf(capacity.getText()));
                        newVehicle.setCapacity(capacityValue);
                        newVehicle.setOpen(true);
                        newVehicle.setModel(model.getText().toString());
                        newVehicle.setSeatsLeft(capacityValue);
                        firestore.collection("vehicle-collection").document(newVehicle.getVehicleID()).set(newVehicle);
                    }
                }
            });
            Intent intent = new Intent(this, MainActivity.class );
            startActivity(intent);
//            firestore.collection("vehicle-collection").document(newVehicle.getVehicleType()).set(newVehicle);

//            collection(newVehicle.getVehicleType()).document(newVehicle.getVehicleID())
        }
    }

    public void Back(View v){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}