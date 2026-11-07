package com.agrovalle.agrovalle_connect.catalogo;

public enum Categoria {

    FRUTAS("Frutas"),
    VERDURAS("Verduras"),
    GRANOS("Granos"),
    CAFE("Café"),
    TUBERCULOS("Tubérculos"),
    LACTEOS("Lácteos"),
    OTROS("Otros");

    private final String etiqueta;

    Categoria(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
