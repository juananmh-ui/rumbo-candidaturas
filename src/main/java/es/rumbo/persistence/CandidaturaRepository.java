package es.rumbo.persistence;

import es.rumbo.model.Candidatura;
import java.io.IOException;
import java.util.List;

/** Contrato de persistencia que permite sustituir los archivos en versiones futuras. */
public interface CandidaturaRepository {
    /** Devuelve los registros existentes; una primera ejecución sin archivo es una lista vacía. */
    List<Candidatura> findAll() throws IOException;
    /** Reemplaza el contenido completo, conservándolo si falla la escritura temporal. */
    void saveAll(List<Candidatura> candidaturas) throws IOException;
}
