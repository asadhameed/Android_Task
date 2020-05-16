package com.github.nene.acessinternalstrogeimages;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ImageView iViewShowImage;
    private static final int REQUEST_CODE=100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iViewShowImage= findViewById(R.id.iViewShowImage);
        Button btnClickImages = findViewById(R.id.btnClickImages);
        btnClickImages.setOnClickListener(new View.OnClickListener(){
            @Override
                public void onClick(View view){
                checkingReadPermission();
            }
        });
    }
    private void checkingReadPermission(){
        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this ,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE);
        }
        else{
            galleryImages();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] permissionsResult){
        if(requestCode==REQUEST_CODE){
            if(permissionsResult.length >0 && permissionsResult[0]==PackageManager.PERMISSION_GRANTED)
                galleryImages();
        }
    }

    private void galleryImages(){
        Intent intent= new Intent(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,false);
        intent.setType("image/*");
        startActivityForResult(intent,REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data){
        super.onActivityResult(reqCode,resultCode,data);
        if(resultCode != Activity.RESULT_OK){
            return;
        }
        if(reqCode==REQUEST_CODE){
            extractImageFromData(data);
        }
    }

    private Bitmap getImage(Uri fileUri) throws FileNotFoundException {
        Bitmap bitmap;
        try{
            InputStream stream= getApplication().getContentResolver().openInputStream(fileUri);
            bitmap= BitmapFactory.decodeStream(stream);
            return  bitmap;
        }catch (FileNotFoundException exp){
            Log.e("EXCEPTION", exp.getMessage());
            throw  exp;
        }
    }

    private void extractImageFromData(Intent data){
        ClipData clipData= data.getClipData();
        List<Bitmap> bitmaps= new ArrayList<>();
        if(clipData!=null){
             for(int i = 0 ; i<clipData.getItemCount(); i++){
                 Uri imageUri= clipData.getItemAt(i).getUri();
                 try {
                     bitmaps.add(getImage(imageUri));
                 }catch (FileNotFoundException exp){
                     Log.e("EXCEPTION", exp.getMessage());
                 }
             }
        }
        else
        {
            Uri imageUri= data.getData();
            try {
                bitmaps.add(getImage(imageUri));
            }catch (FileNotFoundException exp){
                Log.e("EXCEPTION", exp.getMessage());
            }

        }
        iViewShowImage.setImageBitmap(bitmaps.get(0));
    }
}
