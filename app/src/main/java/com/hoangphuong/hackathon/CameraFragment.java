package com.hoangphuong.hackathon;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Random;


public class CameraFragment extends Fragment {
    Button btnOpenGalarey, btnUpload, btnOpenCamera;
    ImageView ivResult;
    Uri imageUri;


    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference mydata;

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference mStorageRef;


    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mydata = FirebaseDatabase.getInstance().getReference();
        getActivity().setTitle("Camera");
        init();
        manageClick();

    }

    private void manageClick() {
        btnOpenGalarey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent thuvien = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                thuvien.setType("image/*");
                startActivityForResult(thuvien, CONST.REQUEST_CODE_GALLERY);

            }
        });

        btnOpenCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CONST.REQUEST_CODE_CAM);
            }
        });
        pushPhotosToFirebase();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CONST.REQUEST_CODE_CAM) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            ivResult.setImageBitmap(bitmap);
        } else if (requestCode == CONST.REQUEST_CODE_GALLERY) {
            imageUri = data.getData();
            ivResult.setImageURI(imageUri);
        }


    }

    public void init() {
        btnOpenCamera = getView().findViewById(R.id.btnOpenCamera);
        btnOpenGalarey = getView().findViewById(R.id.btnSelectGalary);
        btnUpload = getView().findViewById(R.id.btnUpload);
        ivResult = getView().findViewById(R.id.ivResult);
        mStorageRef = FirebaseStorage.getInstance().getReference();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_camera, container, false);
    }

    public void pushPhotosToFirebase() {


        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Random r = new Random();
                int tmp = r.nextInt();
                mStorageRef.child("hinh" + tmp + ".jpg");
                ivResult.setDrawingCacheEnabled(true);
                ivResult.buildDrawingCache();
                Bitmap bitmap = ivResult.getDrawingCache();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data = baos.toByteArray();

                UploadTask uploadTask = mStorageRef.putBytes(data);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(getActivity(), "FAILT", Toast.LENGTH_LONG).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(getActivity(), "SUCCESS", Toast.LENGTH_LONG).show();
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();

                        mydata.child("Image").push().setValue(downloadUrl.toString(), new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if (databaseError == null) {
                                    Toast.makeText(getActivity(), "SUCCESS DATABASE", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(getActivity(), "FAIL DATABASE", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                });
            }
        });

    }

}
