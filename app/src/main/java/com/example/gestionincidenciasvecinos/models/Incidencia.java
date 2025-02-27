package com.example.gestionincidenciasvecinos.models;

import android.net.Uri;

import java.io.Serializable;
import java.util.List;

public class Incidencia implements Serializable {

    private static final long serialVersionUID = 1L;

    private String titulo, descripcion, creador, imageURL, estado;
    private List<String> comentarios;
    private Uri image;

    public Incidencia() {
    }

    // Constructor para subir a la base de datos, sin estado ni comentarios (al crear la incidencia)
    public Incidencia(String titulo, String descripcion, String creador, String imageURL) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.creador = creador;
        this.imageURL = imageURL;
    }

    // Constructor para subir a la base de datos (al actualizar la incidencia si eres admin)
    public Incidencia(String titulo, String descripcion, String creador, String imageURL, String estado, List<String> comentarios) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.creador = creador;
        this.imageURL = imageURL;
        this.estado = estado;
        this.comentarios = comentarios;
    }

    // Constructor para crear la incidencia en local
    public Incidencia(String titulo, String descripcion, String creador, Uri image) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.creador = creador;
        this.image = image;
    }

    public List<String> getComentarios() {
        return comentarios;
    }

    public void setComentarios(List<String> comentarios) {
        this.comentarios = comentarios;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getCreador() {
        return creador;
    }

    public void setCreador(String creador) {
        this.creador = creador;
    }

    public Uri getImage() {
        return image;
    }

    public void setImage(Uri image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return  "¡Mira esta incidencia!" +
                "\nTitulo: " + titulo +
                "\nDescripcion: " + descripcion +
                "\nCreador: " + creador;
    }
}
