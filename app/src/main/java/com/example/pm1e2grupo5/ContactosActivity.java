package com.example.pm1e2grupo5;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pm1e2grupo5.Modelo.Contactos;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ContactosActivity extends AppCompatActivity {

    private ArrayList<Contactos> contactosList;
    private ArrayList<Contactos> contactosListFiltered;
    private ContactosAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contactos);

        ListView listView = findViewById(R.id.listView);
        contactosList = new ArrayList<>();
        contactosListFiltered = new ArrayList<>();
        adapter = new ContactosAdapter(this, contactosListFiltered);
        listView.setAdapter(adapter);

        MaterialButton btnAtras =findViewById(R.id.btnAtras);
        btnAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ContactosActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });


        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }
        });

        obtenerContactosDesdeAPI();
    }

    private void filter(String query) {
        contactosListFiltered.clear();
        if (query.isEmpty()) {
            contactosListFiltered.addAll(contactosList);
        } else {
            query = query.toLowerCase();
            for (Contactos contacto : contactosList) {
                if (contacto.getNombre().toLowerCase().contains(query)) {
                    contactosListFiltered.add(contacto);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void obtenerContactosDesdeAPI() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();
        //String baseUrl = "http://192.168.0.14/PM1E2Grupo5Api/";
        String baseUrl = "http://172.30.0.2/";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        Call<ArrayList<Contactos>> call = apiInterface.getContactos();

        call.enqueue(new Callback<ArrayList<Contactos>>() {
            @Override
            public void onResponse(Call<ArrayList<Contactos>> call, Response<ArrayList<Contactos>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    contactosList.addAll(response.body());
                    contactosListFiltered.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(ContactosActivity.this, "Error al obtener los contactos desde la API", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Contactos>> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(ContactosActivity.this, "Error de conexión" + baseUrl, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
