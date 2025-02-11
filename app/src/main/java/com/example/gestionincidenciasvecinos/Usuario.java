package com.example.gestionincidenciasvecinos;

public class Usuario {

    private String id;
    private String correo;
    private boolean esAdmin;

    public Usuario(String id, String correo, boolean esAdmin) {
        this.id = id;
        this.correo = correo;
        this.esAdmin = esAdmin;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public boolean isEsAdmin() {
        return esAdmin;
    }

    public void setEsAdmin(boolean esAdmin) {
        this.esAdmin = esAdmin;
    }
}
