package com.example.carpool;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.UUID;

/**
 * sign up for new users
 */

public class SignUpActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private EditText emailField;
    private EditText passwordField;
    private EditText nameField;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        emailField = findViewById(R.id.editTextEmail2);
        passwordField = findViewById(R.id.editTextPassword2);
        nameField = findViewById(R.id.editTextName);
    }
    public void signUp(View v){
        System.out.println("signup");
        String emailString = emailField.getText().toString();
        String passwordString = passwordField.getText().toString();
        String nameString = nameField.getText().toString();
        RadioGroup getUserType = (RadioGroup) findViewById(R.id.userType);

        mAuth.createUserWithEmailAndPassword(emailString, passwordString).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.d("SIGN UP", "User successfully signed up.");
                    //make user object
                    int selectedId = getUserType.getCheckedRadioButtonId();
                    RadioButton usertype = (RadioButton) findViewById(selectedId);
                    String type = (String) usertype.getText();

                    User newUser;
                    switch (type) {
                        case "Student":
                            newUser = new Student();
                            break;
                        case "Teacher":
                            newUser = new Teacher();
                            break;
                        case "Parent":
                            newUser = new Parent();
                            break;
                        case "Alumni":
                            newUser = new Alumni();
                            break;
                        default:
                            // Handle the case when an unknown user type is selected
                            newUser = null;
                            break;
                    }
                    newUser.setEmail(emailString);
                    newUser.setUserType(type);
                    newUser.setName(nameString);
                    newUser.setUid(mAuth.getUid().toString());

                    firestore.collection("user-collection").document(newUser.getUid()).set(newUser);


                    Toast.makeText(SignUpActivity.this, "Success!", Toast.LENGTH_SHORT).show();
                    FirebaseUser user = mAuth.getCurrentUser();
                    updateUI(user);
                }else{
                    Log.d("SIGN UP", "Failed to sign up.", task.getException());
                    Toast.makeText(SignUpActivity.this, "Sign Up Failed ):", Toast.LENGTH_SHORT).show();
                    updateUI(null);
                }
            }
        });
    }

    public void updateUI(FirebaseUser currentUser){
        if(currentUser!=null){
            Intent intent = new Intent(this, MainActivity.class );
            startActivity(intent);
        }
    }
}