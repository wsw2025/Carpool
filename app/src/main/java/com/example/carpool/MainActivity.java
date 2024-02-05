package com.example.carpool;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * This is the main page, the user is directed here from signing in or signing up
 * , or when clicking back from other pages
 */

public class MainActivity extends AppCompatActivity {
    private ArrayList<String> vehicleInfoList = new ArrayList<>();
    private ArrayAdapter<String> vehicleAdapter;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;

    private String name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();


        ListView vehicleListView = findViewById(R.id.vehicleListView2);
        ArrayList<String> vehicleInfoList = new ArrayList<>();
        ArrayList<String> subItemsList = new ArrayList<>(); // Create a list for the sub-items

        ArrayList<String> vehicleIDList = new ArrayList<>();

        ArrayList<Boolean> openList = new ArrayList<>();
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


        // get user name
        firestore.collection("user-collection").document(mAuth.getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null && document.exists()) {
                                name = document.getString("name");



                                //populate the list view
                                firestore.collection("vehicle-collection").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            vehicleInfoList.clear();
                                            subItemsList.clear();
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                System.out.println( document.getId() + " => " + document.getData());
                                                String owner = document.getString("owner");
                                                String vehicleInfo = document.getString("vehicleType");
//                        String vehicleID = document.getString("vehicleID");
                                                boolean open = Boolean.TRUE.equals(document.getBoolean("open"));
//                        Log.d("??", Objects.requireNonNull(document.getString("seatsLeft")));
                                                String text;
                                                if(open){
                                                    text = "OPEN / ";
                                                }else{
                                                    text = "CLOSED / ";
                                                }
                                                String subItem =  text + "Model: " + document.getString("model") + " / " + String.valueOf(document.getLong("seatsLeft")) + " Seat(s) Left " + "/ Price: " + String.valueOf(document.getLong("basePrice"));
                                                if (vehicleInfo != null && subItem != null && owner.equals(name)) {
                                                    vehicleInfoList.add(vehicleInfo);
                                                    subItemsList.add(subItem);
                                                    vehicleIDList.add(document.getString("vehicleID"));
                                                    openList.add(open);
                                                }
                                            }
                                            vehicleAdapter.notifyDataSetChanged(); // Notify the adapter of data changes
                                        } else {
                                            Log.d("firebase", "Error getting documents: ", task.getException());
                                        }
                                    }


                                });
                            }
                        } else {
                            Log.d("firebase", "Error getting document: ", task.getException());
                        }
                    }
                });




        //click to close/open
        vehicleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Retrieve the selected item (which is a String)
                String selectedItem = vehicleIDList.get(position);

                boolean newVal = !openList.get(position);



                // Query the Firestore collection to find the document with the matching "vehicleID" field
                firestore.collection("vehicle-collection")
                        .document(selectedItem)
                        .update("open", newVal)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(
                                        MainActivity.this,
                                        "Status changed!",
                                        Toast.LENGTH_SHORT
                                ).show();


                                //populate the list view again
                                firestore.collection("vehicle-collection").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            vehicleInfoList.clear();
                                            subItemsList.clear();
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                System.out.println( document.getId() + " => " + document.getData());
                                                String owner = document.getString("owner");
                                                String vehicleInfo = document.getString("vehicleType");
//                        String vehicleID = document.getString("vehicleID");
                                                boolean open = Boolean.TRUE.equals(document.getBoolean("open"));
//                        Log.d("??", Objects.requireNonNull(document.getString("seatsLeft")));
                                                String text;
                                                if(open){
                                                    text = "OPEN / ";
                                                }else{
                                                    text = "CLOSED / ";
                                                }
                                                String subItem =  text + "Model: " + document.getString("model") + " / " + String.valueOf(document.getLong("seatsLeft")) + " Seat(s) Left " + "/ Price: " + String.valueOf(document.getLong("basePrice"));
                                                if (vehicleInfo != null && subItem != null && owner.equals(name)) {
                                                    vehicleInfoList.add(vehicleInfo);
                                                    subItemsList.add(subItem);
                                                    vehicleIDList.add(document.getString("vehicleID"));
                                                    openList.add(open);
                                                }
                                            }
                                            vehicleAdapter.notifyDataSetChanged(); // Notify the adapter of data changes
                                        } else {
                                            Log.d("firebase", "Error getting documents: ", task.getException());
                                        }
                                    }


                                });
                            }


                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(
                                        MainActivity.this,
                                        "Status Change failed ): Try again Later",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        });
            }
        });

    }




    public void logOut(View v){
        Intent intent = new Intent(this, AuthActivity.class );
        startActivity(intent);
    }

    public void profile(View v){
        Intent intent = new Intent(this, UserProfileActivity.class );
        startActivity(intent);
    }

    public void vehicle(View v){
        Intent intent = new Intent(this, VehiclesInfoActivity.class );
        startActivity(intent);
    }

    public void addVehicle(View v){
        Intent intent = new Intent(this, AddVehicleActivity.class );
        startActivity(intent);
    }
}