package es.rumbo;

import es.rumbo.model.Candidatura;
import es.rumbo.model.Estado;
import es.rumbo.persistence.CandidaturaRepository;
import es.rumbo.service.CandidaturaService;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CandidaturaServiceTest {
    private CandidaturaRepository repository;
    private CandidaturaService service;
    private Candidatura item(long id, String company, Estado state) {
        return new Candidatura(id,"Dev Java",company,state,LocalDate.of(2026,10,17),"","");
    }
    @BeforeEach void setup() {
        repository=mock(CandidaturaRepository.class);
        service=new CandidaturaService(repository);
    }
    @Test void combinesStatusAndCompanyFiltersIgnoringCaseAndAccents() throws Exception {
        when(repository.findAll()).thenReturn(List.of(item(3,"Tecnología Indra",Estado.PENDIENTE),
                item(1,"Indra",Estado.ENTREVISTA),item(2,"Otra empresa",Estado.PENDIENTE)));
        assertEquals(List.of(3L),service.list(Estado.PENDIENTE,"TECNOLOGIA").stream().map(Candidatura::id).toList());
    }
    @Test void noFiltersReturnsEveryRecordSortedById() throws Exception {
        when(repository.findAll()).thenReturn(List.of(item(13,"Indra",Estado.PENDIENTE),item(2,"NTT DATA",Estado.ACEPTADO)));
        assertEquals(List.of(2L,13L),service.list(null,null).stream().map(Candidatura::id).toList());
    }
    @Test void missingIdNeverWrites() throws Exception {
        when(repository.findAll()).thenReturn(List.of(item(13,"Indra",Estado.PENDIENTE)));
        assertThrows(NoSuchElementException.class,()->service.delete(99));
        verify(repository,never()).saveAll(anyList());
    }
    @Test void addUsesMaximumIdRatherThanListSize() throws Exception {
        when(repository.findAll()).thenReturn(List.of(item(13,"Indra",Estado.PENDIENTE)));
        Candidatura added=service.add("JavaFX","Indra",Estado.ENTREVISTA,LocalDate.of(2026,10,28),"","");
        assertEquals(14,added.id());
        @SuppressWarnings("unchecked") ArgumentCaptor<List<Candidatura>> saved=ArgumentCaptor.forClass(List.class);
        verify(repository).saveAll(saved.capture());
        assertEquals(2,saved.getValue().size());
        assertTrue(saved.getValue().contains(added));
    }
    @Test void deletePreservesOtherRecordsAndIds() throws Exception {
        Candidatura retained=item(21,"NTT DATA",Estado.ACEPTADO);
        when(repository.findAll()).thenReturn(List.of(item(13,"Indra",Estado.PENDIENTE),retained));
        service.delete(13);
        verify(repository).saveAll(List.of(retained));
    }
}
