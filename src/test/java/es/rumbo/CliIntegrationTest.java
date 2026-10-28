package es.rumbo;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

class CliIntegrationTest {
    @TempDir Path temp;
    record Result(int code,String out,String err) { }
    private Result run(String... args) {
        StringWriter out=new StringWriter(),err=new StringWriter();
        List<String> all=new ArrayList<>(Arrays.asList(args));
        if(!(args.length==1 && args[0].equals("--help"))) {
            all.add("--archivo");all.add(temp.resolve("datos.csv").toString());
        }
        int code=Main.run(all.toArray(String[]::new),new PrintWriter(out),new PrintWriter(err));
        return new Result(code,out.toString(),err.toString());
    }
    @Test void completeLifecycleEndsAfterEachCommandAndPersistsBetweenInvocations() throws Exception {
        Path source=temp.resolve("origen.csv");
        Files.copy(Path.of("data/candidaturas-ejemplo.csv"),source);
        assertEquals(0,run("-importar",source.toString()).code());
        assertTrue(run("listar").out().contains("Total: 15"));
        assertTrue(run("-listar","--estado","pendiente","--empresa","INDRA").out().contains("Total: 2"));
        Result detail=run("ver","13");assertEquals(0,detail.code());assertTrue(detail.out().contains("Título: Dev Java"));
        assertEquals(0,run("anadir","--titulo","Dev JavaFX","--empresa","Diseño Ágil","--estado","pendiente","--fecha","28/10/2026").code());
        assertTrue(run("ver","16").out().contains("Diseño Ágil"));
        assertEquals(0,run("-borrar","13").code());assertEquals(3,run("ver","13").code());
        assertTrue(run("listar").out().contains("Total: 15"));
    }
    @Test void missingMandatoryOptionIsAValidationError() {
        Result result=run("añadir","--titulo","Java");
        assertEquals(2,result.code());assertTrue(result.err().contains("--empresa"));
        assertFalse(Files.exists(temp.resolve("datos.csv")));
    }
    @Test void invalidDateDoesNotCreateData() {
        assertEquals(2,run("añadir","--titulo","Java","--empresa","Indra","--estado","pendiente","--fecha","31/02/2026").code());
        assertFalse(Files.exists(temp.resolve("datos.csv")));
    }
    @Test void missingCsvHasClearErrorAndNotAStackTrace() {
        Result result=run("importar",temp.resolve("no-existe.csv").toString());
        assertEquals(3,result.code());assertTrue(result.err().contains("No se encuentra el archivo"));
        assertFalse(result.err().contains("Exception"));
    }
    @Test void rejectsUnknownOrRepeatedOptions() {
        assertEquals(2,run("listar","--estado","pendiente","--estado","aceptado").code());
        assertEquals(2,run("listar","--inventado","x").code());
        assertEquals(2,run("ver","13","--estado","pendiente").code());
        assertEquals(2,run("listar","13").code());
    }
    @Test void helpIncludesBothTeamMembers() {
        Result result=run("--help");assertEquals(0,result.code());
        assertTrue(result.out().contains("Oscar Ramírez Toro"));assertTrue(result.out().contains("Juan Antonio Moreno Hevilla"));
    }
    @Test void firstRunExplainsThatNoDataFileExists() {
        Result result=run("listar");assertEquals(0,result.code());
        assertTrue(result.out().contains("Archivo todavía no creado"));
    }
    @Test void zeroAndNonNumericIdsAreRejected() {
        assertEquals(2,run("ver","0").code());assertEquals(2,run("borrar","abc").code());
    }
}
