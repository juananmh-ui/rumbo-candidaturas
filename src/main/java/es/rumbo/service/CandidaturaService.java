package es.rumbo.service;

import es.rumbo.model.Candidatura;
import es.rumbo.model.Estado;
import es.rumbo.persistence.CandidaturaRepository;
import es.rumbo.persistence.CsvFiles;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.Normalizer;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Locale;

/** Reglas de negocio independientes de la consola y de la implementación del repositorio. */
public final class CandidaturaService {
    private final CandidaturaRepository repository;

    /** Recibe la persistencia para poder reutilizar las reglas de negocio en entregas posteriores. */
    public CandidaturaService(CandidaturaRepository repository) {
        this.repository = repository;
    }

    /** Combina filtros con AND; empresa se busca por fragmento sin distinguir tildes o mayúsculas. */
    public List<Candidatura> list(Estado estado, String empresa) throws IOException {
        String query = normalize(empresa == null ? "" : empresa);
        return repository.findAll().stream()
                .filter(c -> estado == null || c.estado() == estado)
                .filter(c -> normalize(c.empresa()).contains(query))
                .sorted(Comparator.comparingLong(Candidatura::id)).toList();
    }

    /** Localiza un ID y produce un mensaje específico si no existe. */
    public Candidatura find(long id) throws IOException {
        return repository.findAll().stream().filter(c -> c.id() == id).findFirst()
                .orElseThrow(() -> new NoSuchElementException("No existe la candidatura con ID " + id + "."));
    }

    /** Asigna un ID libre y persiste una nueva candidatura validada. */
    public Candidatura add(String titulo, String empresa, Estado estado, LocalDate fecha, String enlace, String notas) throws IOException {
        List<Candidatura> items = new ArrayList<>(repository.findAll());
        long max = items.stream().mapToLong(Candidatura::id).max().orElse(0);
        if (max == Long.MAX_VALUE) throw new IllegalArgumentException("No se pueden asignar más identificadores.");
        Candidatura created = new Candidatura(max + 1, titulo, empresa, estado, fecha, enlace, notas);
        items.add(created); repository.saveAll(items);
        return created;
    }

    /** Borra solo el registro indicado; no reescribe el archivo si el ID no existe. */
    public void delete(long id) throws IOException {
        List<Candidatura> items = new ArrayList<>(repository.findAll());
        if (!items.removeIf(c -> c.id() == id)) throw new NoSuchElementException("No existe la candidatura con ID " + id + ".");
        repository.saveAll(items);
    }

    /** Importación completa o nada: valida todos los registros y colisiones antes de guardar. */
    public int importCsv(Path source, Path destination) throws IOException {
        Path sourcePath = source.toAbsolutePath().normalize();
        Path destinationPath = destination.toAbsolutePath().normalize();
        if (sourcePath.equals(destinationPath) || (Files.exists(sourcePath) && Files.exists(destinationPath)
                && Files.isSameFile(sourcePath, destinationPath))) {
            throw new IllegalArgumentException("El CSV de importación debe ser distinto del archivo de datos.");
        }
        List<Candidatura> incoming = CsvFiles.read(sourcePath);
        if (incoming.isEmpty()) throw new IllegalArgumentException("El CSV de importación no contiene candidaturas.");
        List<Candidatura> items = new ArrayList<>(repository.findAll());
        Set<Long> used = new HashSet<>();
        items.forEach(c -> used.add(c.id()));
        for (Candidatura c : incoming) {
            if (!used.add(c.id())) throw new IllegalArgumentException("El ID " + c.id() + " ya existe. No se ha importado ningún registro.");
        }
        items.addAll(incoming); repository.saveAll(items);
        return incoming.size();
    }

    private static String normalize(String text) {
        return Normalizer.normalize(text.strip(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "").toLowerCase(Locale.ROOT);
    }
}
