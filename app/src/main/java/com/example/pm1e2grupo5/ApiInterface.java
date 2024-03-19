package com.example.pm1e2grupo5;

import com.example.pm1e2grupo5.Modelo.Contactos;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiInterface {
    @GET("GetContactos.php")
    Call<ArrayList<Contactos>> getContactos();
}
