package com.example.pm1e2grupo5;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class MostrarFirmaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostrar_firma);

        String firmaBase64 = getIntent().getStringExtra("firma");

        byte[] firmaBytes = Base64.decode(firmaBase64, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(firmaBytes, 0, firmaBytes.length);

        ImageView imageView = findViewById(R.id.imageView);
        imageView.setImageBitmap(bitmap);

        MaterialButton btnAtras =findViewById(R.id.btnAtras);
        btnAtras.setOnClickListener(v -> {
            Intent intent = new Intent(MostrarFirmaActivity.this, ContactosActivity.class);
            startActivity(intent);
        });

    }
}



