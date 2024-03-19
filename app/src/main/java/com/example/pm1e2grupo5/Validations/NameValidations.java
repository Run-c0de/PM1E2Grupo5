package com.example.pm1e2grupo5.Validations;

import android.text.Editable;
import android.text.TextWatcher;

import com.google.android.material.textfield.TextInputEditText;

public class NameValidations implements TextWatcher {
    //
    TextInputEditText txtNombre;
    final int LongName = "José Salvador Alvarenga Espinoza".length();
    public NameValidations(TextInputEditText nombre){
        txtNombre = nombre;
    }
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        String input = txtNombre.getText().toString().trim();

        boolean isLongName = input.length() > LongName;

        if (!input.matches("^[a-zA-ZáéíóúÁÉÍÓÚ\\s]+$")) {
            txtNombre.setError("Debe escribir un nombre válido.");
        } else if(isLongName){
            txtNombre.setText(input.substring(0, LongName));
            txtNombre.setError("El nombre es demasiado largo, que el nombre mas largo registrado en Honduras.");
            txtNombre.setSelection(LongName);
        }else {
            txtNombre.setError(null);
        }
    }
}
