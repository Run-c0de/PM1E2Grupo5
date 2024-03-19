package com.example.pm1e2grupo5;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.pm1e2grupo5.Modelo.Contactos;
import com.example.pm1e2grupo5.Modelo.RestApiMethods;

import com.example.pm1e2grupo5.Modelo.RestApiMethodsL;
import com.example.pm1e2grupo5.Validations.GeolocationValidation;
import com.example.pm1e2grupo5.Validations.NameValidations;
import com.example.pm1e2grupo5.Validations.PhoneValidations;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private TextInputEditText txtNombre, txtTelefono, txtLatitud, txtLongitud;
    private RequestQueue requestQueue;
    private MaterialButton btnLimpiarfirma, btnguardar, btncontatosS;
    private Dibujar dibujar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtNombre = findViewById(R.id.txtNombre);
        txtTelefono = findViewById(R.id.txtTelefono);
        txtLatitud = findViewById(R.id.txtLatitud);
        txtLongitud = findViewById(R.id.txtLongitud);
        btnLimpiarfirma = findViewById(R.id.btnLimpiarfirma);
        btnguardar = findViewById(R.id.btnSalvarG);
        btncontatosS = findViewById(R.id.btncontatosS);
        dibujar = findViewById(R.id.lienzo);

        txtNombre.addTextChangedListener(new NameValidations(txtNombre));
        txtTelefono.addTextChangedListener(new PhoneValidations(txtTelefono));
        txtLatitud.addTextChangedListener(new GeolocationValidation(txtLatitud, txtLongitud));

        Intent intent = getIntent();
        if (intent.hasExtra("contactoId")) {
            txtNombre.setText(intent.getStringExtra("nombre"));
            txtTelefono.setText(intent.getStringExtra("telefono"));
            txtLatitud.setText(intent.getStringExtra("latitud"));
            txtLongitud.setText(intent.getStringExtra("longitud"));
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1001);

        } else {
            getLocation();
        }


        txtLatitud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                Localizacion localizacion = new Localizacion();
                localizacion.setMainActivity(MainActivity.this);

                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1000);
                }
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, localizacion);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, localizacion);
            }
        });

        btnguardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validarDatos();
            }
        });

        btncontatosS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ContactosActivity.class);
                startActivity(intent);
            }
        });
        btnLimpiarfirma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dibujar.limpiarFirma();
            }
        });
    }

    private void validarDatos() {

        /*
        if (txtNombre.getText().toString().isEmpty() ||
                txtTelefono.getText().toString().isEmpty() ||
                txtLatitud.getText().toString().isEmpty() ||
                txtLongitud.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Todos los campos son requeridos", Toast.LENGTH_LONG).show();
        } else {
            // Si hay un contactoId, actualiza el contacto, de lo contrario, crea uno nuevo
            int contactoId = getIntent().getIntExtra("contactoId", 0);
            String firmaBase64 = dibujar.convertirFirmaABase64();
            Contactos nuevoContacto = new Contactos(contactoId, txtNombre.getText().toString(),
            Integer.parseInt(txtTelefono.getText().toString()), txtLatitud.getText().toString(), txtLongitud.getText().toString(),firmaBase64);



        }

        */

        SendData();
    }

    private static boolean isValidBase64(String input) {
        return Pattern.matches("^[a-zA-Z0-9+/]*={0,2}$", input);
    }

    private void SendData() {
        requestQueue = Volley.newRequestQueue(this);

        Contactos contactos = new Contactos();

        int contactoId = getIntent().getIntExtra("contactoId", 0);
        String firmaBase64 = dibujar.convertirFirmaABase64();

        String nombre = Objects.requireNonNull(txtNombre.getText()).toString();
        String telefono = Objects.requireNonNull(txtTelefono.getText()).toString();
        String latitud = Objects.requireNonNull(txtLatitud.getText()).toString();
        String longitud = Objects.requireNonNull(txtLongitud.getText()).toString();

        Predicate<String> nombreValidator = input -> input.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+");
        Predicate<String> telefonoValidator = input -> input.matches("\\+504\\d{8}");
        Predicate<String> latitudValidator = input -> input.matches("-?\\d+(\\.\\d+)?");
        Predicate<String> longitudValidator = input -> input.matches("-?\\d+(\\.\\d+)?");
        Predicate<String> firmaBase64Validator = input -> isValidBase64(input);


        boolean isBase54 = firmaBase64.equals("");
        if (isBase54) {
            Toast.makeText(getApplicationContext(), "Firma Invalida, Debes firmar por huevos.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (!nombreValidator.test(nombre)) {
            txtNombre.setError("Nombre inválido. Debe contener solo letras y espacios.");
            return;
        }

        if (!telefonoValidator.test(telefono)) {
            txtTelefono.setError("Número de teléfono inválido. Debe estar en formato +504XXXXXXXX.");
            return;
        }

        if (!latitudValidator.test(latitud)) {
            txtLatitud.setError("Latitud inválida. Debe ser un número decimal.");
            return;
        }

        if (!longitudValidator.test(longitud)) {
            txtLongitud.setError("Longitud inválida. Debe ser un número decimal.");
            return;
        }

        Contactos nuevoContacto = new Contactos(contactoId, nombre,
                                                   telefono, latitud,
                                                longitud,firmaBase64);


        JSONObject jsonperson = new JSONObject();

        try {
            jsonperson.put("contactoId", nuevoContacto.getContactoId());
            jsonperson.put("nombre", nuevoContacto.getNombre());
            jsonperson.put("telefono", nuevoContacto.getTelefono());
            jsonperson.put("latitud", nuevoContacto.getLatitud());
            jsonperson.put("longitud", nuevoContacto.getLongitud());
            jsonperson.put("firma", nuevoContacto.getFirma());
        } catch (JSONException ex) {
            ex.printStackTrace();
        }

        String endpoint;
        if (nuevoContacto.getContactoId() != 0) {
            endpoint = RestApiMethods.EndpointUpdateContacto;
        } else {
            endpoint = RestApiMethods.EndpointPostContacto;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                endpoint, jsonperson, response -> {
                    try {
                        String mensaje = response.getString("messaje");
                        Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_LONG).show();
                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    }
                }, error -> Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show());

        requestQueue.add(request);
        limpiarCampos();
    }


    public class Localizacion implements LocationListener {
        MainActivity mainActivity;

        public void setMainActivity(MainActivity mainActivity) {
            this.mainActivity = mainActivity;
        }

        @Override
        public void onLocationChanged(Location loc) {
            txtLatitud.setText(String.valueOf(loc.getLatitude()));
            txtLongitud.setText(String.valueOf(loc.getLongitude()));
            this.mainActivity.setLocation(loc);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                case LocationProvider.AVAILABLE:
                    Log.d("debug", "LocationProvider.AVAILABLE");
                    break;
                case LocationProvider.OUT_OF_SERVICE:
                    Log.d("debug", "LocationProvider.OUT_OF_SERVICE");
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Log.d("debug", "LocationProvider.TEMPORARILY_UNAVAILABLE");
                    break;
            }
        }
    }

    public void setLocation(Location loc) {
        if (loc.getLatitude() != 0.0 && loc.getLongitude() != 0.0) {
            try {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> list = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void getLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Localizacion localizacion = new Localizacion();
        localizacion.setMainActivity(this);
        final boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled) {
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1000);
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, localizacion);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, localizacion);
    }

    private String ConvertImageBase64(String imagePath) {
        String encodedImage = "";
        try {
            InputStream inputStream = new FileInputStream(imagePath);
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            inputStream.close();
            encodedImage = Base64.encodeToString(buffer, Base64.DEFAULT);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return encodedImage;
    }


    private void limpiarCampos() {
        txtNombre.setText("");
        txtTelefono.setText("");
        txtLatitud.setText("");
        txtLongitud.setText("");
        txtNombre.requestFocus();
    }
}
