package com.example.a46406163y.proyectouf2;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

public class POJO implements Serializable {

    private String archivo;
    private double lat;
    private double lon;

    public POJO(String archivo, double lon, double lat) {
        this.lat = lat;
        this.lon = lon;
        this.archivo = archivo;
    }

    public POJO() {

    }

    public String getArchivo() {
        return archivo;
    }

    @JsonProperty("archivo")
    public void setArchivo(String archivo) {
        this.archivo = archivo;
    }

    public double getLat() {
        return lat;
    }
    @JsonProperty("lat")
    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }
    @JsonProperty("lon")
    public void setLon(double lon) {
        this.lon = lon;
    }

    @Override
    public String toString() {
        return "POJO{" +
                "archivo='" + archivo + '\'' +
                ", lat=" + lat +
                ", lon=" + lon +
                '}';
    }
}
