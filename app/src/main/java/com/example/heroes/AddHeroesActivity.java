package com.example.heroes;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ResultReceiver;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.net.URI;

import api.APIInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddHeroesActivity extends AppCompatActivity {

    ImageView chooseImageView;
    Button btnAdd;
    EditText txtName, txtDesc;
String imagePath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_heroes);
        txtDesc = findViewById(R.id.txtDesc);
        txtName = findViewById(R.id.txtHeroName);
        btnAdd = findViewById(R.id.btnAddHero);
        chooseImageView = findViewById(R.id.chooseImageView);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addHero(txtName.getText().toString(), txtDesc.getText().toString());
            }
        });
        chooseImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                browseImage();

            }
        });

    }

    private void browseImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            if (data == null) {

                Toast.makeText(this,"Please select image",Toast.LENGTH_LONG).show();
            }
        }
        Uri uri = data.getData();
        imagePath = getRealPathFromURI(uri);
        previewImage(imagePath);
        
    }

    private String getRealPathFromURI(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(getApplicationContext(),uri,projection,null,null,null);
        Cursor cursor = loader.loadInBackground();
        int colindex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToNext();
        String result = cursor.getString(colindex);
        cursor.close();
        return result;
    }

    private void addHero(String name, String desc) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(APIInterface.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()).build();
        APIInterface heroapi = retrofit.create(APIInterface.class);
        Call<Void> voidCall = heroapi.addHero(name, desc);

        voidCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Toast.makeText(AddHeroesActivity.this, "Hero Registered ", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(AddHeroesActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }
    private void previewImage(String imagePath){
        File imgFile = new File(imagePath);
        if(imgFile.exists()){
            Bitmap myBitMap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            chooseImageView.setImageBitmap(myBitMap);
        }
    }
}
