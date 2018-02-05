package com.roscosoft.inmovil;

import android.media.Image;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Hp on 22/8/2017.
 */

public class Casa implements Serializable {
    public String descripcion;
    public String detalle;
    public ArrayList<String> fotos;
    public ArrayList<Image> fotosI;
    public Image fotoP;
    public int cuartos, banos, pisos, tipo;
    public double precio;// precio venta, alquiler, anticretico
    public double supT, supC; //superficio total y construida
    public String direccion, zona, barrio, provincia;
    public double latitud, longitud;
    public String nombreC, telefono, telefono1; // contacto
    public String unidadSup, unidadSupCon, moneda;
}