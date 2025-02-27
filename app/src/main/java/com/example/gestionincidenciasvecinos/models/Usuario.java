package com.example.gestionincidenciasvecinos.models;

public class Usuario {

    private String id, correo, pwd, nombreApellidos, pisoLetra, numTelefono;
    private boolean esAdmin;

//    public Usuario(String id, String correo, String pwd, boolean esAdmin) {
//        this.id = id;
//        this.correo = correo;
//        this.pwd = pwd;
//        this.esAdmin = esAdmin;
//    }

    public Usuario(String correo, String pwd, String nombreApellidos, String pisoLetra, String numTelefono) {
        this.correo = correo;
        this.pwd = pwd;
        this.nombreApellidos = nombreApellidos;
        this.pisoLetra = pisoLetra;
        this.numTelefono = numTelefono;
        this.esAdmin = false;
    }

    // Constructor vacío
    public Usuario() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getNombreApellidos() {
        return nombreApellidos;
    }

    public void setNombreApellidos(String nombreApellidos) {
        this.nombreApellidos = nombreApellidos;
    }

    public String getPisoLetra() {
        return pisoLetra;
    }

    public void setPisoLetra(String pisoLetra) {
        this.pisoLetra = pisoLetra;
    }

    public String getNumTelefono() {
        return numTelefono;
    }

    public void setNumTelefono(String numTelefono) {
        this.numTelefono = numTelefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public boolean getEsAdmin() {
        return esAdmin;
    }

    public void setEsAdmin(boolean esAdmin) {
        this.esAdmin = esAdmin;
    }
}
