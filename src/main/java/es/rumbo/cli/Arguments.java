package es.rumbo.cli;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Analizador explícito de argumentos, sin lecturas interactivas de entrada estándar. */
public record Arguments(String command, Map<String, String> options, List<String> positional, Path file) {
    /** Admite los comandos con o sin el prefijo '-' que aparece en el enunciado. */
    public static Arguments parse(String[] args) {
        Map<String, String> options = new HashMap<>();
        List<String> positional = new ArrayList<>();
        String command = null;
        for (int i = 0; i < args.length; i++) {
            String token = args[i];
            if (token.equals("--help") || token.equals("-h")) {
                if (args.length != 1) throw new IllegalArgumentException("Usa --help sin otros argumentos.");
                return new Arguments("ayuda", Map.of(), List.of(), Path.of("data/candidaturas.csv"));
            }
            if (token.startsWith("--")) {
                if (!Set.of("--archivo", "--titulo", "--empresa", "--estado", "--fecha", "--enlace", "--notas").contains(token)) {
                    throw new IllegalArgumentException("Opción desconocida: " + token + ". Consulta --help.");
                }
                if (options.containsKey(token)) throw new IllegalArgumentException("La opción " + token + " está repetida.");
                if (i + 1 == args.length || args[i + 1].startsWith("--")) throw new IllegalArgumentException("Falta el valor de " + token + ".");
                options.put(token, args[++i]);
            } else if (command == null) {
                command = token.startsWith("-") ? token.substring(1) : token;
                if (command.equals("anadir")) command = "añadir";
            } else positional.add(token);
        }
        if (command == null) throw new IllegalArgumentException("Indica una operación. Consulta --help.");
        Set<String> allowed = switch (command) {
            case "listar" -> Set.of("--archivo", "--estado", "--empresa");
            case "añadir" -> Set.of("--archivo", "--titulo", "--empresa", "--estado", "--fecha", "--enlace", "--notas");
            case "ver", "borrar", "importar" -> Set.of("--archivo");
            case "ayuda", "version" -> Set.of();
            default -> throw new IllegalArgumentException("Operación desconocida: '" + command + "'. Consulta --help.");
        };
        for (String key : options.keySet()) {
            if (!allowed.contains(key)) throw new IllegalArgumentException("La operación " + command + " no admite " + key + ".");
        }
        int expected = Set.of("ver", "borrar", "importar").contains(command) ? 1 : 0;
        if (positional.size() != expected) {
            throw new IllegalArgumentException("La operación " + command + " requiere " + expected + " argumento(s) posicional(es). Consulta --help.");
        }
        return new Arguments(command, Map.copyOf(options), List.copyOf(positional),
                Path.of(options.getOrDefault("--archivo", "data/candidaturas.csv")));
    }

    /** Recupera una opción obligatoria con un error legible. */
    public String required(String option) {
        String value = options.get(option);
        if (value == null || value.isBlank()) throw new IllegalArgumentException("Falta la opción obligatoria " + option + ".");
        return value;
    }

    /** Recupera y valida el ID para ver o borrar. */
    public long id() {
        try {
            long value = Long.parseLong(positional.getFirst());
            if (value < 1) throw new NumberFormatException();
            return value;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("El ID debe ser un entero positivo.");
        }
    }
}
