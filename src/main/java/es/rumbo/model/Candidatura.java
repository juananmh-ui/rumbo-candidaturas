package es.rumbo.model;

import java.net.URI;
import java.time.LocalDate;
import java.util.Objects;

/** Registro inmutable de una candidatura. Empresa es texto en v1.0. */
public record Candidatura(long id, String titulo, String empresa, Estado estado,
                          LocalDate fechaPostulacion, String enlace, String notas) {
    public Candidatura {
        if (id < 1) throw new IllegalArgumentException("El ID debe ser un entero positivo.");
        titulo = clean(titulo, "título", true);
        empresa = clean(empresa, "empresa", true);
        estado = Objects.requireNonNull(estado, "El estado es obligatorio.");
        fechaPostulacion = Objects.requireNonNull(fechaPostulacion, "La fecha es obligatoria.");
        enlace = clean(enlace, "enlace", false);
        notas = clean(notas, "notas", false);
        if (!enlace.isEmpty()) {
            try {
                URI uri = URI.create(enlace);
                if (!("http".equalsIgnoreCase(uri.getScheme())
                        || "https".equalsIgnoreCase(uri.getScheme())) || uri.getHost() == null) {
                    throw new IllegalArgumentException();
                }
            } catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException("El enlace debe ser una URL http o https válida.");
            }
        }
    }

    private static String clean(String value, String name, boolean required) {
        String result = value == null ? "" : value.strip();
        if (required && result.isEmpty()) throw new IllegalArgumentException("El campo " + name + " es obligatorio.");
        if (result.contains("\n") || result.contains("\r") || result.indexOf('\0') >= 0) {
            throw new IllegalArgumentException("El campo " + name + " no puede contener saltos de línea ni caracteres nulos.");
        }
        return result;
    }
}
