package com.example.signup;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.ktx.Firebase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    CircleImageView userImage;
    TextInputEditText nameTV;
    TextInputEditText courseTV;
    TextInputEditText contactNoTV;
    TextInputEditText emailTV;
    Button btnSave;
    Uri URI=null;
    Bitmap bitmapImage;

    ActivityResultLauncher<String> readPhotosFromStorage =
            registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri result) {
                    if(result!=null){
                        URI=result;
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(result);
                            bitmapImage= BitmapFactory.decodeStream(inputStream);
                            userImage.setImageBitmap(bitmapImage);

                        } catch (FileNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialize();

        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                readPhotosFromStorage.launch("image/*");
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name= Objects.requireNonNull(nameTV.getText()).toString();
                String course= Objects.requireNonNull(courseTV.getText()).toString();
                String contactNo= Objects.requireNonNull(contactNoTV.getText()).toString();
                String email= Objects.requireNonNull(emailTV.getText()).toString();
                if(!name.equals("") && !course.equals("") && !contactNo.equals("") && !email.equals("") && !Objects.equals(URI, null)){
                    saveInFirebase();
                }
                else if (name.equals("")){
                    Toast.makeText(MainActivity.this, "Enter your name", Toast.LENGTH_SHORT).show();
                }
                else if (course.equals("")){
                    Toast.makeText(MainActivity.this, "Enter your course", Toast.LENGTH_SHORT).show();
                }
                else if (contactNo.equals("")){
                    Toast.makeText(MainActivity.this, "Enter contact no", Toast.LENGTH_SHORT).show();
                }
                else if (email.equals("")){
                    Toast.makeText(MainActivity.this, "Enter your email", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(MainActivity.this, "Select any picture", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void initialize(){
        userImage=findViewById(R.id.userImage);
        nameTV=findViewById(R.id.name);
        courseTV=findViewById(R.id.course);
        contactNoTV=findViewById(R.id.contactNo);
        emailTV=findViewById(R.id.email);
        btnSave=findViewById(R.id.btnSave);
    }
    private void saveInFirebase(){
        ProgressDialog progressDialog =new ProgressDialog(this);
        progressDialog.setTitle("Uploader");
        progressDialog.setCancelable(false);
        progressDialog.show();


        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference firebaseReference = firebaseStorage.getReference().child(Objects.requireNonNull(emailTV.getText()).toString());
        firebaseReference.putFile(URI)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.dismiss();
                        firebaseReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String name= Objects.requireNonNull(nameTV.getText()).toString();
                                String course= Objects.requireNonNull(courseTV.getText()).toString();
                                String contactNo= Objects.requireNonNull(contactNoTV.getText()).toString();
                                String email= Objects.requireNonNull(emailTV.getText()).toString();

                                DataHolder dataHolder = new DataHolder(name,course,contactNo,email,uri.toString());
                                FirebaseDatabase db= FirebaseDatabase.getInstance();
                                DatabaseReference users=db.getReference("users");
                                users.child(email).setValue(dataHolder);

                                nameTV.setText("");
                                courseTV.setText("");
                                contactNoTV.setText("");
                                emailTV.setText("");
                                URI=null;
                                userImage.setImageResource(R.drawable.ic_launcher_background);
                                Toast.makeText(MainActivity.this, "Data successfully uploaded", Toast.LENGTH_SHORT).show();



                            }
                        });
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        float percent = (float)((100*snapshot.getBytesTransferred())/snapshot.getTotalByteCount());
                        progressDialog.setMessage("Uploaded: "+(int)percent+"%");

                    }
                });
    }
}