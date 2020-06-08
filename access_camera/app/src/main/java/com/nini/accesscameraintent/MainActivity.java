package com.nini.accesscameraintent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private ImageButton imageBtnCamera;
    private ImageView imageShow;
    public static final int REQUEST_PHOTO = 10;
    String pathToFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageShow = findViewById(R.id.imageShow);
        imageBtnCamera = findViewById(R.id.imageBtnCamera);
        imageBtnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkingPermission();

            }
        });
    }

    private void checkingPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PHOTO);
        } else
            getStartActivity();
    }

    @Override
    public void onRequestPermissionsResult(int req_code, String[] permissions, int[] permissionResult) {
        if (req_code == REQUEST_PHOTO) {
            if (permissionResult.length > 0 && permissionResult[0] == PackageManager.PERMISSION_GRANTED && permissionResult[1] == PackageManager.PERMISSION_GRANTED)
                getStartActivity();
        }

    }

    private void getStartActivity() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intent.resolveActivity(getPackageManager())!=null) {
            File phtoFile =createPhotoFile();
            if(phtoFile!=null) {
                pathToFile = phtoFile.getAbsolutePath();
                Uri potoUri= FileProvider.getUriForFile(MainActivity.this, "com.asd.fileprovider", phtoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT,potoUri);
            }
            startActivityForResult(intent, REQUEST_PHOTO);
        }
    }

    private File createPhotoFile() {
        String name = new SimpleDateFormat("yyyyMMDD_HHmmss").format(new Date());
      //  File stroageDir=getFilesDir();
        File stroageDir =getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image=null;
        try {
            image=File.createTempFile(name ,".jpg",stroageDir);
        } catch (IOException e) {
            Log.d("MY_ERROR", "Exception" +e.getMessage());
        }
        return  image;
    }


    @Override
    protected void onActivityResult(int r_code, int resultCode, Intent data) {
        super.onActivityResult(r_code, resultCode, data);
        if (r_code == REQUEST_PHOTO && resultCode == Activity.RESULT_OK) {
          //  Bitmap bitmap=(Bitmap) data.getExtras().get("data");
            Bitmap bitmap= BitmapFactory.decodeFile(pathToFile);
            imageShow.setImageBitmap(bitmap);
           // Toast.makeText(this, "Take the pic from camera", Toast.LENGTH_LONG).show();
        }
    }

}
