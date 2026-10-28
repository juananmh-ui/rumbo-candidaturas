package es.rumbo;

import es.rumbo.model.Candidatura;
import es.rumbo.model.Estado;
import es.rumbo.persistence.CsvCodec;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

class CsvCodecTest {
    @Test void preservesDelimitersQuotesAccentsAndEmptyFields() {
        Candidatura source = new Candidatura(13, "Java; backend", "Diseño \"Ágil\"", Estado.PENDIENTE,
                LocalDate.of(2026, 10, 17), "", "Notas; \"con comillas\"");
        assertEquals(source, CsvCodec.decode(CsvCodec.encode(source)));
    }
    @Test void acceptsLeapDay() {
        assertEquals(LocalDate.of(2024, 2, 29), CsvCodec.parseDate("29/02/2024"));
    }
    @ParameterizedTest @ValueSource(strings={"31/02/2026","29/02/2025","2026-10-28","28/13/2026","1/1/2026"})
    void rejectsInvalidDates(String input) {
        assertThrows(IllegalArgumentException.class, () -> CsvCodec.parseDate(input));
    }
    @ParameterizedTest @ValueSource(strings={"1;x;y;pendiente;28/10/2026", "1;\"sin cerrar;y;pendiente;28/10/2026;;", "1;\"x\"y;z;pendiente;28/10/2026;;", "0;x;y;pendiente;28/10/2026;;", "1;x;y;inventado;28/10/2026;;"})
    void rejectsMalformedRecords(String input) {
        assertThrows(IllegalArgumentException.class, () -> CsvCodec.decode(input));
    }
    @Test void rejectsPhysicalLineBreaksInNotes() {
        assertThrows(IllegalArgumentException.class, () -> new Candidatura(1,"Java","Indra",Estado.PENDIENTE,
                LocalDate.now(),"","Primera línea\nSegunda línea"));
    }
    @ParameterizedTest @ValueSource(strings={"ftp://example.com", "javascript:alert(1)","no es una URL"})
    void rejectsNonWebLinks(String link) {
        assertThrows(IllegalArgumentException.class, () -> new Candidatura(1,"Java","Indra",Estado.PENDIENTE,
                LocalDate.now(),link,""));
    }
}
