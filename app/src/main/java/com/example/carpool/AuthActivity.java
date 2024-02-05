package com.example.carpool;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Sign in for pre-existing users.
 */

public class AuthActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private EditText emailField;
    private EditText passwordField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auth_activity);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        emailField = findViewById(R.id.editTextEmail);
        passwordField = findViewById(R.id.editTextPassword);
    }

    public void signIn(View v){
        System.out.println("signin");
        String emailString = emailField.getText().toString();
        String passwordString = passwordField.getText().toString();
        System.out.println(String.format("email: %s \t password: %s", emailString, passwordString));
        mAuth.signInWithEmailAndPassword(emailString, passwordString).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d("SIGN IN", "signInWithEmail:success");
                    FirebaseUser user = mAuth.getCurrentUser();
                    Toast.makeText(
                            AuthActivity.this,
                            String.format("Signed in as %s", emailString),
                            Toast.LENGTH_SHORT
                    ).show();


                    //test
//                    firestore.collection("user-collection").document(mAuth.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                        @Override
//                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                            if (!task.isSuccessful()) {
//                                Log.e("firebase", "Error getting data", task.getException());
//                            }
//                            else {
//                                DocumentSnapshot ds = task.getResult();
//                                User myUser = ds.toObject(User.class);
//                                Log.d("firebase", myUser.getName());
//                            }
//                        }
//                    });

                    updateUI(user);
                } else {
                    Log.w("SIGN IN", "signInWithEmail:failure", task.getException());
                    Toast.makeText(
                            AuthActivity.this,
                            "Sign in failed.",
                            Toast.LENGTH_SHORT
                            ).show();
                    updateUI(null);
                }
            }
        });
    }

//    public void signUp(View v){
//        System.out.println("signup");
//        String emailString = emailField.getText().toString();
//        String passwordString = passwordField.getText().toString();
//
//        mAuth.createUserWithEmailAndPassword(emailString, passwordString).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//            @Override
//            public void onComplete(@NonNull Task<AuthResult> task) {
//                if(task.isSuccessful()){
//                    Log.d("SIGN UP", "User successfully signed up.");
//                    Toast.makeText(AuthActivity.this, "Success!", Toast.LENGTH_SHORT).show();
//                    FirebaseUser user = mAuth.getCurrentUser();
//                    updateUI(user);
//                }else{
//                    Log.d("SIGN UP", "Failed to sign up.", task.getException());
//                    Toast.makeText(AuthActivity.this, "Sign Up Failed ):", Toast.LENGTH_SHORT).show();
//                    updateUI(null);
//                }
//            }
//        });
//    }

    public void updateUI(FirebaseUser currentUser){
        if(currentUser!=null){
            Intent intent = new Intent(this, MainActivity.class );
            startActivity(intent);
        }
    }

    public void signUp(View v){
        Intent intent = new Intent(this, SignUpActivity.class );
        startActivity(intent);
    }
}