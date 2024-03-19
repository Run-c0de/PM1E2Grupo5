package com.example.pm1e2grupo5.Validations;
import android.text.Editable;
import android.text.TextWatcher;

import com.google.android.material.textfield.TextInputEditText;

public class GeolocationValidation implements TextWatcher {

    //
    TextInputEditText txtLongitud;

    TextInputEditText txtLatitud;

    public GeolocationValidation(TextInputEditText lat,TextInputEditText lon ){
        txtLatitud = lat;
        txtLongitud = lon;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        String inputLatitud = txtLatitud.getText().toString().trim();
        String inputLongitud = txtLongitud.getText().toString().trim();

        if (!inputLatitud.matches("-?\\d+(\\.\\d+)?") || !inputLatitud.matches("-?\\d+(\\.\\d+)?")) {
            txtLatitud.setError("Debe escribir una coordenada válida.");
        } else {
            txtLatitud.setError(null);
        }



        if (!inputLongitud.matches("-?\\d+(\\.\\d+)?") || !inputLongitud.matches("-?\\d+(\\.\\d+)?")) {
            txtLongitud.setError("Debe escribir una coordenada válida.");
        } else {
            txtLongitud.setError(null);
        }
    }
}
