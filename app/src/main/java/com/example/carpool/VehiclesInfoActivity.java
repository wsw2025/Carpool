package com.example.carpool;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class VehiclesInfoActivity extends AppCompatActivity {
    private ArrayList<String> vehicleIDList = new ArrayList<>();
    private ArrayAdapter<String> vehicleAdapter;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vinfo);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Find the filter button by its ID
        Spinner filterButton = findViewById(R.id.filterButton);

// Create an ArrayAdapter to populate the filter options
        ArrayAdapter<String> filterAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        filterAdapter.add("Filter: All");
        filterAdapter.add("Car");
        filterAdapter.add("Bicycle");
        filterAdapter.add("Helicopter");
        filterAdapter.add("Segway");

// Set the adapter for the filter button
        filterButton.setAdapter(filterAdapter);

        ListView vehicleListView = findViewById(R.id.vehicleListView2);
        ArrayList<String> vehicleInfoList = new ArrayList<>();
        ArrayList<String> subItemsList = new ArrayList<>(); // Create a list for the sub-items

        ArrayList<String> vehicleIDList = new ArrayList<>();
        ArrayAdapter<String> vehicleAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_2, android.R.id.text1, vehicleInfoList) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);
                }

                String item = getItem(position);
                String subItem = subItemsList.get(position);

                TextView mainTextView = convertView.findViewById(android.R.id.text1);
                TextView subTextView = convertView.findViewById(android.R.id.text2);

                mainTextView.setText(item);
                subTextView.setText(subItem);

                return convertView;
            }
        };
        vehicleListView.setAdapter(vehicleAdapter);

        //clicking into it to see the details of a vehicle
        vehicleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = (String) vehicleIDList.get(position);

                firestore.collection("vehicle-collection")
                        .whereEqualTo("vehicleID", selectedItem)
                        .limit(1)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                    // Retrieve the document ID from the query result
                                    String documentId = task.getResult().getDocuments().get(0).getId();

                                    // You can pass the document ID to the new activity
                                    Intent intent = new Intent(VehiclesInfoActivity.this, VehicleProfileActivity.class);
                                    intent.putExtra("documentId", documentId);
                                    startActivity(intent);
                                } else {
                                    Log.d("firebase", "Error retrieving document ID: ", task.getException());
                                }
                            }
                        });
            }
        });

        // filter + set vehicles
        filterButton.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedFilter = (String) parent.getItemAtPosition(position);

                firestore.collection("vehicle-collection").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            vehicleInfoList.clear();
                            subItemsList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("firebase", document.getId() + " => " + document.getData());
                                String vehicleInfo = document.getString("owner");
                                String vehicleType = document.getString("vehicleType");

//                        String vehicleID = document.getString("vehicleID");
                                boolean open = document.getBoolean("open");
//                        Log.d("??", Objects.requireNonNull(document.getString("seatsLeft")));
                                String subItem = String.valueOf(document.getLong("seatsLeft")) + " Seat(s) Left";
                                if (vehicleInfo != null && subItem != null && open && (vehicleType.equals(selectedFilter) || selectedFilter.equals("Filter: All"))) {
                                    vehicleInfoList.add(vehicleInfo);
//                            subItemsList.add(subItem); // Add sub-item to the list
                                    subItemsList.add(subItem);
                                    vehicleIDList.add(document.getString("vehicleID"));
                                }
                            }
                            vehicleAdapter.notifyDataSetChanged(); // Notify the adapter of data changes
                        } else {
                            Log.d("firebase", "Error getting documents: ", task.getException());
                        }
                    }


                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });
    }



    public void Back(View v){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
