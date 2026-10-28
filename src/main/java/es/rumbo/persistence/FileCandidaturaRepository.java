package es.rumbo.persistence;

import es.rumbo.model.Candidatura;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

/** Repositorio UTF-8 con escritura temporal y reemplazo del archivo. */
public final class FileCandidaturaRepository implements CandidaturaRepository {
    private final Path path;

    /** Selecciona el archivo de trabajo; no lo crea hasta que se guarda una operación válida. */
    public FileCandidaturaRepository(Path path) {
        this.path = path.toAbsolutePath().normalize();
    }

    @Override
    public List<Candidatura> findAll() throws IOException {
        if (!Files.exists(path, LinkOption.NOFOLLOW_LINKS)) return List.of();
        return CsvFiles.read(path);
    }

    @Override
    public void saveAll(List<Candidatura> candidaturas) throws IOException {
        Path parent = path.getParent();
        Files.createDirectories(parent);
        Path temporary = Files.createTempFile(parent, ".rumbo-", ".tmp");
        try {
            try (BufferedWriter writer = Files.newBufferedWriter(temporary, StandardCharsets.UTF_8)) {
                writer.write(CsvCodec.HEADER); writer.newLine();
                for (Candidatura c : candidaturas) {
                    writer.write(CsvCodec.encode(c)); writer.newLine();
                }
            }
            try {
                Files.move(temporary, path, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException ex) {
                Files.move(temporary, path, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(temporary);
        }
    }
}
