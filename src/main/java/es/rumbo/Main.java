package es.rumbo;

import es.rumbo.cli.Arguments;
import es.rumbo.model.Candidatura;
import es.rumbo.model.Estado;
import es.rumbo.persistence.CsvCodec;
import es.rumbo.persistence.FileCandidaturaRepository;
import es.rumbo.service.CandidaturaService;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Path;
import java.util.List;
import java.util.NoSuchElementException;

/** Entrada de Rumbo: una operación por proceso. Equipo: Oscar Ramírez Toro y Juan Antonio Moreno Hevilla. */
public final class Main {
    private Main() { }

    /** Ejecuta una operación con los argumentos recibidos y propaga el código de error al sistema. */
    public static void main(String[] args) {
        int code = run(args, new PrintWriter(System.out, true, StandardCharsets.UTF_8),
                new PrintWriter(System.err, true, StandardCharsets.UTF_8));
        if (code != 0) System.exit(code);
    }

    /** Ejecuta la operación y devuelve 0 (éxito), 2 (validación), 3 (no encontrado) o 4 (E/S). */
    public static int run(String[] raw, PrintWriter out, PrintWriter err) {
        try {
            Arguments args = Arguments.parse(raw);
            CandidaturaService service = new CandidaturaService(new FileCandidaturaRepository(args.file()));
            switch (args.command()) {
                case "ayuda" -> out.println(help());
                case "version" -> out.println("Rumbo 1.0 | Oscar Ramírez Toro y Juan Antonio Moreno Hevilla");
                case "listar" -> {
                    Estado state = args.options().containsKey("--estado") ? Estado.parse(args.required("--estado")) : null;
                    String company = args.options().containsKey("--empresa") ? args.required("--empresa") : null;
                    List<Candidatura> items = service.list(state, company);
                    out.println(CsvCodec.HEADER);
                    items.forEach(c -> out.println(CsvCodec.encode(c)));
                    out.println("Total: " + items.size() + " candidatura(s).");
                    if (!Files.exists(args.file())) out.println("Archivo todavía no creado: " + args.file() + ". Usa añadir o importar.");
                }
                case "añadir" -> {
                    Candidatura c = service.add(args.required("--titulo"), args.required("--empresa"),
                            Estado.parse(args.required("--estado")), CsvCodec.parseDate(args.required("--fecha")),
                            args.options().getOrDefault("--enlace", ""), args.options().getOrDefault("--notas", ""));
                    out.println("Candidatura añadida con ID " + c.id() + ".");
                }
                case "ver" -> detail(service.find(args.id()), out);
                case "borrar" -> { long id = args.id(); service.delete(id); out.println("Candidatura " + id + " eliminada."); }
                case "importar" -> {
                    int count = service.importCsv(Path.of(args.positional().getFirst()), args.file());
                    out.println("Importación completada: " + count + " candidatura(s). Archivo: " + args.file());
                }
                default -> throw new IllegalStateException("Operación no implementada.");
            }
            out.flush(); return 0;
        } catch (IllegalArgumentException ex) {
            return error(err, ex.getMessage(), 2);
        } catch (NoSuchElementException ex) {
            return error(err, ex.getMessage(), 3);
        } catch (NoSuchFileException ex) {
            return error(err, "No se encuentra el archivo: " + ex.getFile() + ".", 3);
        } catch (AccessDeniedException ex) {
            return error(err, "No tienes permiso para acceder al archivo: " + ex.getFile() + ".", 4);
        } catch (IOException ex) {
            return error(err, "No se pudo leer o guardar el archivo: " + ex.getMessage() + ".", 4);
        }
    }

    private static int error(PrintWriter err, String message, int code) {
        err.println("Error: " + message); err.flush(); return code;
    }

    private static void detail(Candidatura c, PrintWriter out) {
        out.println("ID: " + c.id()); out.println("Título: " + c.titulo());
        out.println("Empresa: " + c.empresa()); out.println("Estado: " + c.estado().text());
        out.println("Fecha de postulación: " + CsvCodec.DATE.format(c.fechaPostulacion()));
        out.println("Enlace: " + (c.enlace().isEmpty() ? "(sin enlace)" : c.enlace()));
        out.println("Notas: " + (c.notas().isEmpty() ? "(sin notas)" : c.notas()));
    }

    private static String help() {
        return """
                RUMBO 1.0 - Tracker de candidaturas laborales
                Oscar Ramírez Toro y Juan Antonio Moreno Hevilla

                java -jar target/rumbo-1.0.jar <operación> [opciones]
                  listar [--estado X] [--empresa Y]
                  añadir --titulo X --empresa Y --estado Z --fecha dd/MM/yyyy
                         [--enlace URL] [--notas TEXTO]
                  ver ID
                  borrar ID
                  importar fichero.csv
                  ayuda | --help | version

                --archivo RUTA selecciona el archivo de datos para cualquier operación.
                Por defecto: data/candidaturas.csv (UTF-8, separador ';').
                También se admite -listar, -añadir, -ver, -borrar y -importar.
                'anadir' es un alias sin tilde de 'añadir'.
                Los filtros se combinan con AND; empresa admite fragmentos y omite tildes.
                Estados: pendiente, entrevista, rechazado, aceptado.
                No hay menú interactivo: cada invocación ejecuta una operación y termina.
                """;
    }
}
