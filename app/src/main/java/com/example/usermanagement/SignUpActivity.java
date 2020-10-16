package com.example.usermanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthEmailException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener{

    String name, username, email, phone, password, confPassword;

    EditText editTextName, editTextUserName, editTextEmail, editTextPhone, editTextPassword, editTextConfPassword;
    ProgressBar progressBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        editTextName = findViewById(R.id.editTextName);
        editTextUserName = findViewById(R.id.editTextUserName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextConfPassword = findViewById(R.id.editTextConfPassword);
        progressBar = findViewById(R.id.progressBar);

        mAuth = FirebaseAuth.getInstance();
        findViewById(R.id.buttonSignUp).setOnClickListener(this);
        findViewById(R.id.textViewLogin).setOnClickListener(this);

    }
    private void registerUser() {
         name= editTextName.getText().toString().trim();
         username= editTextUserName.getText().toString().trim();
         email= editTextEmail.getText().toString().trim();
         phone= editTextPhone.getText().toString().trim();
         password= editTextPassword.getText().toString().trim();
         confPassword= editTextConfPassword.getText().toString().trim();

            if(!password.matches(confPassword))
            {
                editTextConfPassword.setError("passwords are not matching");
                editTextConfPassword.requestFocus();
                return;
            }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            editTextEmail.setError("Email is not valid. Please enter a valid email");
            editTextEmail.requestFocus();
            return;
        }

        if (password.length()<6)
        {
            editTextPassword.setError("At least 6 characters in the password");
            editTextPassword.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);

                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(getApplicationContext(),"User successfully registered", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);

                            storeNewUserData();
                        }
                        else if (task.getException() instanceof FirebaseAuthUserCollisionException)
                            {
                                editTextEmail.setError("This email is already exist. Please log in.");
                                editTextEmail.requestFocus();
                                return;                            }
                        }

                });
    }

    private void storeNewUserData() {
        //FirebaseDatabase rootNode = new FirebaseDatabase();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        //reference.setValue("Helloo");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        String id = user.getUid();

        UserBean addNewUser = new UserBean(name, username, email, phone);
        reference.child(id).setValue(addNewUser);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.buttonSignUp:
                registerUser();
                break;

            case R.id.textViewLogin:
                startActivity(new Intent(this, LoginActivity.class));
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + view.getId());
        }
    }
}