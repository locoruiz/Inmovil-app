package com.roscosoft.inmovil;

import android.media.Image;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Hp on 22/8/2017.
 */

public class Vehiculo implements Serializable {
    public String descripcion;
    public String detalle;
    public ArrayList<String> fotos;
    public int puertas, tipo, ano, kilometraje;
    public double precio;// precio venta
    public String marca, modelo, color, traccion;
    public double cilindrada;
    public int caja; // 1 manual, 2 automatica, 3 secuencial
    public String combustible;
    public String nombreC, telefono, telefono1; // contacto
    public String unidadCil, moneda;
}