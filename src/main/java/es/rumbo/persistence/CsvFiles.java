package es.rumbo.persistence;

import es.rumbo.model.Candidatura;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Lectura compartida por la persistencia y la importación. */
public final class CsvFiles {
    private CsvFiles() { }

    /** Lee y valida el archivo completo sin efectuar escrituras. Cabecera opcional. */
    public static List<Candidatura> read(Path path) throws IOException {
        List<Candidatura> result = new ArrayList<>();
        Set<Long> seen = new HashSet<>();
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line; int lineNumber = 0; boolean firstRecord = true;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (lineNumber == 1 && line.startsWith("\uFEFF")) line = line.substring(1);
                if (line.isBlank()) continue;
                if (firstRecord && CsvCodec.HEADER.equalsIgnoreCase(line.strip())) {
                    firstRecord = false; continue;
                }
                firstRecord = false;
                try {
                    Candidatura c = CsvCodec.decode(line);
                    if (!seen.add(c.id())) throw new IllegalArgumentException("ID repetido: " + c.id() + ".");
                    result.add(c);
                } catch (IllegalArgumentException ex) {
                    throw new IllegalArgumentException("Archivo '" + path.getFileName() + "', línea " + lineNumber + ": " + ex.getMessage());
                }
            }
        }
        return List.copyOf(result);
    }
}
