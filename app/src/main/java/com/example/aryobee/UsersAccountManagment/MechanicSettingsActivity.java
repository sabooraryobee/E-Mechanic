package com.example.aryobee.UsersAccountManagment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.aryobee.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MechanicSettingsActivity extends AppCompatActivity {

    private EditText mNameField, mPhoneField, mCarPlateField;

    private Button mBack, mConfirm;

    private ImageView mProfileImage;

    private FirebaseAuth mAuth;
    private DatabaseReference mMechanicDatabase;

    private String userID;
    private String mName;
    private String mPhone;
    private String mCarPlate;
   // private String mService;
    private String mProfileImageUrl;

    private Uri resultUri;

    private RadioGroup mRadioGroup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mechanic_settings);


        mNameField = (EditText) findViewById(R.id.name);
        mPhoneField = (EditText) findViewById(R.id.phone);
        mCarPlateField = (EditText) findViewById(R.id.carplate);

        mProfileImage = (ImageView) findViewById(R.id.profileImage);

    //    mRadioGroup = (RadioGroup) findViewById(R.id.radioGroup);

        mBack = (Button) findViewById(R.id.back);
        mConfirm = (Button) findViewById(R.id.confirm);

        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        mMechanicDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Mechanics").child(userID);

        getUserInfo();

        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });

        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserInformation();
            }
        });

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                return;
            }
        });
    }
    private void getUserInfo(){
        mMechanicDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if(map.get("name")!=null){
                        mName = map.get("name").toString();
                        mNameField.setText(mName);
                    }
                    if(map.get("phone")!=null){
                        mPhone = map.get("phone").toString();
                        mPhoneField.setText(mPhone);
                    }
                    if(map.get("car")!=null){
                        mCarPlate = map.get("car").toString();
                        mCarPlateField.setText(mCarPlate);
                    }

                    if(map.get("profileImageUrl")!=null){
                        mProfileImageUrl = map.get("profileImageUrl").toString();
                        Glide.with(getApplication()).load(mProfileImageUrl).into(mProfileImage);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }



    private void saveUserInformation() {
        mName = mNameField.getText().toString();
        mPhone = mPhoneField.getText().toString();
        mCarPlate = mCarPlateField.getText().toString();

        Map userInfo = new HashMap();
        userInfo.put("name", mName);
        userInfo.put("phone", mPhone);
        userInfo.put("car", mCarPlate);
        mMechanicDatabase.updateChildren(userInfo);

        if(resultUri != null) {

            StorageReference filePath = FirebaseStorage.getInstance().getReference().child("profile_images").child(userID);
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), resultUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = filePath.putBytes(data);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUrl = taskSnapshot.getUploadSessionUri();
                    filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Map newImage = new HashMap();
                            Log.i("photo",downloadUrl.toString());
                            newImage.put("profileImageUrl", uri.toString());
                            mMechanicDatabase.updateChildren(newImage).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {

                                }
                            });
                        }
                    });
                }
            }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                }
            });
        }else{
            finish();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == Activity.RESULT_OK){
            final Uri imageUri = data.getData();
            resultUri = imageUri;
            mProfileImage.setImageURI(resultUri);
        }
    }
}
