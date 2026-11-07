package com.agrovalle.agrovalle_connect.catalogo;

public enum Municipio {

    BUGA("Buga"),
    CAICEDONIA("Caicedonia"),
    CALI("Cali"),
    CARTAGO("Cartago"),
    DAGUA("Dagua"),
    EL_CERRITO("El Cerrito"),
    JAMUNDI("Jamundí"),
    PALMIRA("Palmira"),
    SEVILLA("Sevilla"),
    TULUA("Tuluá"),
    YUMBO("Yumbo"),
    ZARZAL("Zarzal");

    private final String etiqueta;

    Municipio(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
