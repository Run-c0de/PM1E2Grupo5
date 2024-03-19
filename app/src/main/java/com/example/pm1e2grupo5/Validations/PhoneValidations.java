package com.example.pm1e2grupo5.Validations;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Timer;
import java.util.TimerTask;
//
public class PhoneValidations implements TextWatcher {
    int MAX_LENGTH = 12;
    TextInputEditText txtTelefono;
    public PhoneValidations(TextInputEditText phone){
        txtTelefono = phone;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        String input = txtTelefono.getText().toString().trim();
        String code = "+504";
        if (input.equals(code)) {
            txtTelefono.setError("Debe escribir un número de teléfono válido en formato +504XXXXXXXX.");
        } else if (input.isEmpty() || !input.matches("\\+504\\d{0,11}")) {
            txtTelefono.setError("Debe escribir un número de teléfono válido en formato +504XXXXXXXX.");
            txtTelefono.setText(code);
            txtTelefono.setSelection(code.length());


        } else {
            if (input.length() > MAX_LENGTH) {
                txtTelefono.setText(input.substring(0, MAX_LENGTH));
                txtTelefono.setSelection(MAX_LENGTH);
                txtTelefono.setError("El número de teléfono no puede exceder " + MAX_LENGTH + " caracteres.");

                new Handler().postDelayed(() -> txtTelefono.post(() -> txtTelefono.setError(null)), 2000);
            } else {
                txtTelefono.setError(null);
            }
        }
    }
}
