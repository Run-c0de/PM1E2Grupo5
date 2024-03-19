package com.example.pm1e2grupo5;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.pm1e2grupo5.Modelo.Contactos;
import com.example.pm1e2grupo5.Modelo.RestApiMethods;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ContactosAdapter extends ArrayAdapter<Contactos> {

    private ArrayList<Contactos> contactosList;
    private LayoutInflater inflater;
    private Context context;
    private int clicksCount = 0;
    private final int DOUBLE_CLICK_DELAY = 200;

    public ContactosAdapter(Context context, ArrayList<Contactos> contactosList) {
        super(context, 0, contactosList);
        this.context = context;
        this.contactosList = contactosList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_contacto, parent, false);
            holder = new ViewHolder();
            holder.idTextView = convertView.findViewById(R.id.idTextView);
            holder.nombreTextView = convertView.findViewById(R.id.nombreTextView);
            holder.telefonoTextView = convertView.findViewById(R.id.telefonoTextView);
            holder.latitudTextView = convertView.findViewById(R.id.latitudTextView);
            holder.longitudTextView = convertView.findViewById(R.id.longitudTextView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Contactos contacto = contactosList.get(position);
        holder.idTextView.setText("ID: " + contacto.getContactoId());
        holder.nombreTextView.setText(contacto.getNombre());
        holder.telefonoTextView.setText(String.valueOf(contacto.getTelefono()));
        holder.latitudTextView.setText(contacto.getLatitud());
        holder.longitudTextView.setText(contacto.getLongitud());

        convertView.setOnClickListener(v -> {
            clicksCount++;
            if (clicksCount == 2) {
                verUbicacion(contacto);
                clicksCount = 0;
            } else {
                new Handler().postDelayed(() -> {
                    if (clicksCount == 1) {
                        mostrarOpciones(contacto);
                    }
                    clicksCount = 0;
                }, DOUBLE_CLICK_DELAY);
            }
        });

        return convertView;
    }

    private void mostrarOpciones(final Contactos contacto) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Seleccione una acción")
                .setItems(new CharSequence[]{"Eliminar", "Actualizar", "Ver Ubicacion","Ver firma"},
                        (dialog, which) -> {
                    switch (which) {
                        case 0:
                            eliminarContacto(contacto);
                            break;
                        case 1:
                            actualizarContacto(contacto);
                            break;
                        case 2:
                            verUbicacion(contacto);
                            break;
                        case 3:
                            mostrarFirma(contacto);
                            break;
                    }
                })
                .setNegativeButton("Cancelar", null)
                .create()
                .show();
    }

    private void eliminarContacto(final Contactos contacto) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Confirmar eliminación")
                .setMessage("¿Está seguro de que desea eliminar este contacto?")
                .setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        JSONObject jsonBody = new JSONObject();
                        try {
                            jsonBody.put("contactoId", contacto.getContactoId());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        RequestQueue requestQueue = Volley.newRequestQueue(context);
                        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                                RestApiMethods.EndpointDeleteContacto,
                                jsonBody,
                                response -> {
                                    try {
                                        String mensaje = response.getString("messaje");
                                        Toast.makeText(context, mensaje, Toast.LENGTH_LONG).show();

                                        contactosList.remove(contacto);
                                        notifyDataSetChanged();
                                    } catch (JSONException ex) {
                                        ex.printStackTrace();
                                    }
                                }, error -> {
                                    Toast.makeText(context, "Error al eliminar el contacto", Toast.LENGTH_LONG).show();
                                    error.printStackTrace();
                                });

                        requestQueue.add(request);
                    }
                })
                .setNegativeButton("Cancelar", null)
                .create()
                .show();
    }




    private void actualizarContacto(final Contactos contacto) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("contactoId", contacto.getContactoId());
        intent.putExtra("nombre", contacto.getNombre());
        intent.putExtra("telefono", contacto.getTelefono());
        intent.putExtra("latitud", contacto.getLatitud());
        intent.putExtra("longitud", contacto.getLongitud());

        context.startActivity(intent);
    }

    private void verUbicacion(final Contactos contacto) {
        Intent intent = new Intent(context, Ubicacion.class);
        intent.putExtra("contactoId", contacto.getContactoId());
        intent.putExtra("nombre", contacto.getNombre());
        intent.putExtra("telefono", contacto.getTelefono());
        intent.putExtra("latitud", contacto.getLatitud());
        intent.putExtra("longitud", contacto.getLongitud());

        context.startActivity(intent);
    }
    private void mostrarFirma(final Contactos contacto) {
        Intent intent = new Intent(context, MostrarFirmaActivity.class);
        intent.putExtra("firma", contacto.getFirma());
        context.startActivity(intent);
    }


    static class ViewHolder {
        TextView idTextView;
        TextView nombreTextView;
        TextView telefonoTextView;
        TextView latitudTextView;
        TextView longitudTextView;
    }
}
