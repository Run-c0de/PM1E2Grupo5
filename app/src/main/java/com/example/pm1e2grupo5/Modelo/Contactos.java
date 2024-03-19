package com.example.pm1e2grupo5.Modelo;

public class Contactos {
    private int contactoId;
    private String nombre;
    private String telefono;
    private String latitud;
    private String longitud;
    private String firma;

    public Contactos(int contactoId, String nombre, String telefono, String latitud, String longitud, String firma) {
        this.contactoId = contactoId;
        this.nombre = nombre;
        this.telefono = telefono;
        this.latitud = latitud;
        this.longitud = longitud;
        this.firma = firma;
    }
    public Contactos(){

    }

    public int getContactoId() {

        return contactoId;
    }

    public void setcontactoId(int id) {

        this.contactoId = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

    public String getFirma() {
        return firma;
    }

    public void setFirma(String firma) {
        this.firma = firma;
    }
}
