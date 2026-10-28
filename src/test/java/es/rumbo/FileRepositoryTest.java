package es.rumbo;

import es.rumbo.model.Candidatura;
import es.rumbo.model.Estado;
import es.rumbo.persistence.CsvCodec;
import es.rumbo.persistence.CsvFiles;
import es.rumbo.persistence.FileCandidaturaRepository;
import es.rumbo.service.CandidaturaService;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

class FileRepositoryTest {
    @TempDir Path temp;
    private Candidatura sample(long id) {
        return new Candidatura(id,"Desarrollador Java","Diseño Ágil",Estado.PENDIENTE,
                LocalDate.of(2026,10,17),"","Notas; con delimitador y \"comillas\"");
    }
    @Test void persistsUtf8AndCreatesParentDirectory() throws Exception {
        Path target=temp.resolve("datos del equipo/candidaturas.csv");
        var repo=new FileCandidaturaRepository(target);
        repo.saveAll(List.of(sample(13)));
        assertEquals(List.of(sample(13)),repo.findAll());
        assertTrue(Files.readString(target).contains("Diseño Ágil"));
        try(var paths=Files.list(target.getParent())) {
            assertEquals(1,paths.count(),"No debe quedar un archivo temporal tras guardar.");
        }
    }
    @Test void readsBomHeaderAndBlankLines() throws Exception {
        Path source=temp.resolve("import.csv");
        Files.writeString(source,"\uFEFF"+CsvCodec.HEADER+"\n\n"+CsvCodec.encode(sample(13))+"\n",StandardCharsets.UTF_8);
        assertEquals(List.of(sample(13)),CsvFiles.read(source));
    }
    @Test void duplicateImportNeverChangesTheOriginalFile() throws Exception {
        Path target=temp.resolve("data.csv"),source=temp.resolve("import.csv");
        var repo=new FileCandidaturaRepository(target);repo.saveAll(List.of(sample(13)));
        byte[] before=Files.readAllBytes(target);
        Files.writeString(source,CsvCodec.HEADER+"\n"+CsvCodec.encode(sample(2))+"\n"+CsvCodec.encode(sample(13))+"\n");
        assertThrows(IllegalArgumentException.class,()->new CandidaturaService(repo).importCsv(source,target));
        assertArrayEquals(before,Files.readAllBytes(target));
    }
    @Test void malformedLastRowPreventsPartialImport() throws Exception {
        Path target=temp.resolve("data.csv"),source=temp.resolve("import.csv");
        Files.writeString(source,CsvCodec.HEADER+"\n"+CsvCodec.encode(sample(13))+"\n2;registro;roto\n");
        var repo=new FileCandidaturaRepository(target);
        assertThrows(IllegalArgumentException.class,()->new CandidaturaService(repo).importCsv(source,target));
        assertFalse(Files.exists(target));
    }
    @Test void refusesToImportDataFileIntoItself() throws Exception {
        Path target=temp.resolve("data.csv");var repo=new FileCandidaturaRepository(target);repo.saveAll(List.of(sample(13)));
        assertThrows(IllegalArgumentException.class,()->new CandidaturaService(repo).importCsv(target,target));
        assertEquals(1,repo.findAll().size());
    }
    @Test void refusesDuplicateIdsInsideSourceWithLineNumber() throws Exception {
        Path source=temp.resolve("duplicados.csv");
        Files.writeString(source,CsvCodec.HEADER+"\n"+CsvCodec.encode(sample(13))+"\n"+CsvCodec.encode(sample(13))+"\n");
        var error=assertThrows(IllegalArgumentException.class,()->CsvFiles.read(source));
        assertTrue(error.getMessage().contains("línea 3"));
    }
}
