package com.example.usermanagement;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import static android.view.View.VISIBLE;

public class UserProfile extends AppCompatActivity {

    String name, username, email, phone;
    String UserID;
    EditText nameText, usernameText, emailText, phoneText;
    TextView textviewName;
    //Button resetPassword;

    ImageView profilePicture;
    DatabaseReference ref;
    StorageReference storageReference;
    FirebaseUser user;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        user = FirebaseAuth.getInstance().getCurrentUser();
        UserID= user.getUid();
        email = user.getEmail();        //retrieve email address from firebase authentication

        ref = FirebaseDatabase.getInstance().getReference("Users");
        storageReference = FirebaseStorage.getInstance().getReference();

         StorageReference profPicRef = storageReference.child("Users/" +UserID+ "/profile.jpg");
         profPicRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profilePicture);
            }
         });

        UserID= user.getUid();

         profilePicture = findViewById(R.id.profPic);
         nameText = findViewById(R.id.editTextPersonName);
         usernameText = findViewById(R.id.editTextPersonUserName);
         emailText = findViewById(R.id.editTextEmail);
         phoneText = findViewById(R.id.editTextPersonPhone);
         textviewName = findViewById(R.id.textViewName);

        //ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(UserID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserBean user1 = snapshot.getValue(UserBean.class);

                if(user1 != null)
                {
                     name = user1.name;
                     username = user1.username;
                     phone = user1.phone;

                    String[] firstName = name.split(" ");
                    textviewName.setText("Hello "+firstName[0]);
                    nameText.setText(name);
                    usernameText.setText(username);
                    emailText.setText(email);   //firebase auth email address
                    phoneText.setText(phone);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(),"SOMething went wrong!", Toast.LENGTH_SHORT).show();
            }
        });

        //resetPassword = findViewById(R.id.buttonResetPW);

    }

    public void updateUser(View view)
    {
        final String newName= nameText.getText().toString();
        final String newUsername = usernameText.getText().toString();
        final String newEmail = emailText.getText().toString();
        final String newPhone = phoneText.getText().toString();
        final AlertDialog.Builder myAlert = new AlertDialog.Builder(this);
        myAlert.setTitle("Update");
        myAlert.setMessage("Are you want to update?");
        myAlert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                   if (isNameChanged() || isUsernameChanged() || isPhoneChanged()) {
                       Toast.makeText(getApplicationContext(), "Data has been updated!", Toast.LENGTH_SHORT).show();
                   }
                       if ( isEmailChanged())
                       {
                           Toast.makeText(getApplicationContext(), "Data has been updated!", Toast.LENGTH_SHORT).show();
                           UserBean updateUser = new UserBean(newName, newUsername, newEmail, newPhone);
                           ref.child(UserID).setValue(updateUser);
                       }

                   }
        });
        myAlert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        myAlert.setCancelable(false);
        myAlert.show();
    }


    private boolean isNameChanged() {
        if ( !name.equals(nameText.getText().toString()))
        {
            ref.child(UserID).child("name").setValue(nameText.getText().toString());
            return true;
        }
        else
        {
            return false;
        }
    }

    private boolean isUsernameChanged() {
        if ( !username.equals(usernameText.getText().toString()))
        {
            ref.child(UserID).child("username").setValue(usernameText.getText().toString());
            return true;
        }
        else
            return false;
    }

    private boolean isEmailChanged() {
        if ( !email.equals(emailText.getText().toString())) {
            final String newEmail = emailText.getText().toString();

            if (Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {

                user.updateEmail(newEmail).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }
                });
            } else {
                emailText.setError("Email is not valid. Please enter a valid email");
                emailText.requestFocus();
                return false;
            }
        }
        return true;
        }

    private boolean isPhoneChanged() {
        if ( !phone.equals(phoneText.getText().toString()))
        {
            ref.child(UserID).child("phone").setValue(phoneText.getText().toString());
            return true;
        }
        else
            return false;
    }

    public void deleteUser(View view)
    {
        AlertDialog.Builder myAlert = new AlertDialog.Builder(this);
        myAlert.setTitle("Delete");
        myAlert.setMessage("Do you really want to delete?");
        myAlert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ref = FirebaseDatabase.getInstance().getReference("Users").child(UserID);

                ref.removeValue();

                Toast.makeText(getApplicationContext(),"Succesfully deleted!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(UserProfile.this, UserProfile.class));

            }
        });
        myAlert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        myAlert.setCancelable(false);
        myAlert.show();
    }

    public void viewUsers(View view) {

            startActivity(new Intent(this, UsersList.class));
    }

    public void changeProfilePic(View view) {

        Intent openGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(openGallery, 1);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==1)
        {
            if(resultCode== Activity.RESULT_OK)
            {
                Uri imageUri = data.getData();
                //profilePicture.setImageURI(imageUri);

                uploadImgToFirebase(imageUri);
            }
        }

    }

    private void uploadImgToFirebase(Uri imageUri) {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Uploading image...");
        pd.show();

        final StorageReference fileRef = storageReference.child("Users/" +UserID+ "/profile.jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                pd.dismiss();
                Toast.makeText(getApplicationContext(),"Image uploaded!", Toast.LENGTH_SHORT).show();
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(profilePicture);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(getApplicationContext(),"Failed to upload!", Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    pd.setMessage("Please wait!");
            }
        });
    }

    public void resetPassword(View view) {

        final EditText resetPasswordField = new EditText(view.getContext());
        final AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(view.getContext());
        passwordResetDialog.setTitle("Reset Password");
        passwordResetDialog.setMessage("Please enter your new password");
        passwordResetDialog.setView(resetPasswordField);

        passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String newPassword = resetPasswordField.getText().toString();
               // user = mAuth.getCurrentUser();
                user.updatePassword(newPassword).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(),"Password reset successfully", Toast.LENGTH_SHORT).show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),"Password didn't reset successfully." + e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });
        passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                    //close
            }
        });
        passwordResetDialog.create().show();
    }

    public void signOut(View view) {
        AlertDialog.Builder myAlert = new AlertDialog.Builder(this);
        myAlert.setTitle("Sign out");
        myAlert.setMessage("Do you want to sign out?");
        myAlert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity(new Intent(UserProfile.this, LoginActivity.class));
            }
        });
        myAlert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        myAlert.setCancelable(false);
        myAlert.show();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder myAlert = new AlertDialog.Builder(this);
        myAlert.setTitle("Sign out");
        myAlert.setMessage("Do you want to sign out?");
        myAlert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity(new Intent(UserProfile.this, LoginActivity.class));
            }
        });
        myAlert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        myAlert.setCancelable(false);
        myAlert.show();
    }
}