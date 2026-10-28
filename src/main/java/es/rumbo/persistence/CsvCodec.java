package es.rumbo.persistence;

import es.rumbo.model.Candidatura;
import es.rumbo.model.Estado;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/** CSV UTF-8 con separador punto y coma; un registro por línea física. */
public final class CsvCodec {
    public static final String HEADER = "id;titulo;empresa;estado;fechaPostulacion;enlace;notas";
    public static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd/MM/uuuu")
            .withResolverStyle(ResolverStyle.STRICT);

    private CsvCodec() { }

    /** Valida una fecha real, incluidos los años bisiestos. */
    public static LocalDate parseDate(String text) {
        try {
            return LocalDate.parse(text, DATE);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("Fecha no válida: '" + text + "'. Usa dd/MM/yyyy, por ejemplo 28/10/2026.");
        }
    }

    /** Lee siete campos y valida el modelo antes de devolverlo. */
    public static Candidatura decode(String line) {
        List<String> cells = split(line);
        if (cells.size() != 7) throw new IllegalArgumentException("Se esperaban 7 campos separados por ';', se encontraron " + cells.size() + ".");
        long id;
        try {
            id = Long.parseLong(cells.get(0).strip());
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("El ID del CSV no es un entero válido: '" + cells.get(0) + "'.");
        }
        return new Candidatura(id, cells.get(1), cells.get(2), Estado.parse(cells.get(3)),
                parseDate(cells.get(4).strip()), cells.get(5), cells.get(6));
    }

    /** Serializa delimitadores y comillas con el escape estándar de CSV. */
    public static String encode(Candidatura c) {
        return List.of(Long.toString(c.id()), c.titulo(), c.empresa(), c.estado().text(),
                DATE.format(c.fechaPostulacion()), c.enlace(), c.notas()).stream()
                .map(CsvCodec::escape).collect(Collectors.joining(";"));
    }

    private static String escape(String text) {
        return text.contains(";") || text.contains("\"")
                ? "\"" + text.replace("\"", "\"\"") + "\"" : text;
    }

    /** Analiza CSV sin perder campos vacíos y rechaza comillas mal colocadas. */
    static List<String> split(String line) {
        List<String> result = new ArrayList<>();
        StringBuilder cell = new StringBuilder();
        boolean quoted = false;
        boolean closedQuote = false;
        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);
            if (quoted) {
                if (ch == '"') {
                    if (i + 1 < line.length() && line.charAt(i + 1) == '"') {
                        cell.append('"'); i++;
                    } else {
                        quoted = false; closedQuote = true;
                    }
                } else cell.append(ch);
            } else if (ch == ';') {
                result.add(cell.toString()); cell.setLength(0); closedQuote = false;
            } else if (ch == '"' && cell.isEmpty() && !closedQuote) {
                quoted = true;
            } else {
                if (closedQuote || ch == '"') throw new IllegalArgumentException("Comillas CSV mal colocadas.");
                cell.append(ch);
            }
        }
        if (quoted) throw new IllegalArgumentException("Comillas CSV sin cerrar; cada registro debe ocupar una sola línea.");
        result.add(cell.toString());
        return result;
    }
}
