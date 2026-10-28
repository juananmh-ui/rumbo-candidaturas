package es.rumbo.model;

import java.util.Locale;

/** Estados admitidos para una candidatura en la versión 1.0. */
public enum Estado {
    PENDIENTE, ENTREVISTA, RECHAZADO, ACEPTADO;

    /** Convierte el texto de la CLI o del CSV en un estado validado. */
    public static Estado parse(String value) {
        try {
            return valueOf(value.strip().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Estado no válido: '" + value
                    + "'. Usa pendiente, entrevista, rechazado o aceptado.");
        }
    }

    /** Devuelve el valor persistido en el archivo de texto. */
    public String text() {
        return name().toLowerCase(Locale.ROOT);
    }
}
