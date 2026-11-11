package com.rica.api.investigadores_service.aplicacion;

import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.rica.api.investigadores_service.investigadores.aplicacion.InvestigadorFactory;
import com.rica.api.investigadores_service.investigadores.aplicacion.InvestigadorService;
import com.rica.api.investigadores_service.investigadores.aplicacion.PublicadorEventos;
import com.rica.api.investigadores_service.investigadores.aplicacion.RepositorioInvestigadores;
import com.rica.api.investigadores_service.investigadores.dominio.CorreoInstitucional;
import com.rica.api.investigadores_service.investigadores.dominio.Investigador;

@ExtendWith(MockitoExtension.class)
public class InvestigadorServiceTest {

    private static final String CORREO = "ana.torres@uptc.edu.co";

    @Mock
    private RepositorioInvestigadores repositorioInvestigadores;

    @Mock
    private InvestigadorFactory investigadorFactory;

    @Mock
    private PublicadorEventos publicadorEventos;

    @InjectMocks
    private InvestigadorService investigadorService;

    private static Investigador ana(Long id) {
        return new Investigador(id, "Ana Torres", new CorreoInstitucional(CORREO), "GIT-UPTC");
    }

    // ---- Lado de escritura: eventos que alimentan el modelo de lectura ----
    @Test
    void registrarPublicaElEventoConLosCamposQueNecesitaElProyector() {
        Investigador sinId = ana(null);
        Investigador guardado = ana(1L);
        when(investigadorFactory.crear("Ana Torres", CORREO, "GIT-UPTC")).thenReturn(sinId);
        when(repositorioInvestigadores.guardar(sinId)).thenReturn(guardado);

        investigadorService.registrar("Ana Torres", CORREO, "GIT-UPTC");

        verify(publicadorEventos).publicar(
                "investigadores.registrados",
                CORREO,
                Map.of("id", 1L, "correoInstitucional", CORREO, "nombreCompleto", "Ana Torres"));
    }

    @Test
    void iniciarEliminacionMarcaAlInvestigadorYPublicaElEventoEnEliminacion() {
        Investigador investigador = ana(1L);
        when(repositorioInvestigadores.buscarPorId(1L)).thenReturn(Optional.of(investigador));

        investigadorService.iniciarEliminacion(1L);

        verify(repositorioInvestigadores).guardar(investigador);
        verify(publicadorEventos).publicar(
                "investigadores.en-eliminacion",
                CORREO,
                Map.of("correoInstitucional", CORREO));
    }

    @Test
    void iniciarEliminacionEsIdempotenteSiLaSagaYaEstaEnCurso() {
        Investigador investigador = ana(1L);
        investigador.marcarEnEliminacion();
        when(repositorioInvestigadores.buscarPorId(1L)).thenReturn(Optional.of(investigador));

        investigadorService.iniciarEliminacion(1L);

        verify(repositorioInvestigadores, never()).guardar(any());
        verify(publicadorEventos, never()).publicar(anyString(), anyString(), anyMap());
    }

    @Test
    void confirmarEliminacionBorraAlInvestigadorYPublicaElEventoEliminado() {
        Investigador enEliminacion = ana(1L);
        enEliminacion.marcarEnEliminacion();
        when(repositorioInvestigadores.buscarPorCorreo(CORREO)).thenReturn(Optional.of(enEliminacion));

        investigadorService.confirmarEliminacion(CORREO);

        verify(repositorioInvestigadores).eliminar(enEliminacion);
        verify(publicadorEventos).publicar(
                "investigadores.eliminados",
                CORREO,
                Map.of("correoInstitucional", CORREO));
    }

    @Test
    void confirmarEliminacionNoHaceNadaSiElInvestigadorNoEstaEnEliminacion() {
        Investigador activo = ana(1L);
        when(repositorioInvestigadores.buscarPorCorreo(CORREO)).thenReturn(Optional.of(activo));

        investigadorService.confirmarEliminacion(CORREO);

        verify(repositorioInvestigadores, never()).eliminar(any());
        verify(publicadorEventos, never()).publicar(anyString(), anyString(), anyMap());
    }

    @Test
    void confirmarEliminacionNoPublicaNadaSiElInvestigadorYaNoExiste() {
        when(repositorioInvestigadores.buscarPorCorreo(CORREO)).thenReturn(Optional.empty());

        investigadorService.confirmarEliminacion(CORREO);

        verify(repositorioInvestigadores, never()).eliminar(any());
        verify(publicadorEventos, never()).publicar(anyString(), anyString(), anyMap());
    }
}
