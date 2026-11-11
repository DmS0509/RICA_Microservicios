package com.rica.api.publicaciones_service.aplicacion;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
 
import com.rica.api.publicaciones_service.compartido.RecursoNoEncontradoException;
import com.rica.api.publicaciones_service.publicaciones.aplicacion.PublicacionService;
import com.rica.api.publicaciones_service.publicaciones.aplicacion.PublicadorEventosPublicaciones;
import com.rica.api.publicaciones_service.publicaciones.aplicacion.RepositorioPublicaciones;
import com.rica.api.publicaciones_service.publicaciones.aplicacion.VerificadorInvestigador;
import com.rica.api.publicaciones_service.publicaciones.dominio.Publicacion;

@ExtendWith(MockitoExtension.class)
public class PublicacionServiceTest {

   private static final String CORREO = "ana.torres@uptc.edu.co";
 
    @Mock
    private RepositorioPublicaciones repositorioPublicaciones;
 
    @Mock
    private VerificadorInvestigador verificadorInvestigador;
 
    @Mock
    private PublicadorEventosPublicaciones publicadorEventos;
 
    @InjectMocks
    private PublicacionService publicacionService;
 
    private static Publicacion publicacion(String id, String correo) {
        Publicacion p = new Publicacion();
        p.setId(id);
        p.setInvestigadorCorreo(correo);
        p.setTitulo("Arquitecturas evolutivas");
        p.setTipo("articulo");
        p.setAnio(2026);
        p.setDetalles(Map.of());
        return p;
    }
 
    @Test
    void registrarPublicaElEventoDespuesDeGuardar() {
        Publicacion entrada = publicacion(null, CORREO);
        Publicacion guardada = publicacion("abc123", CORREO);
        when(verificadorInvestigador.existe(CORREO)).thenReturn(true);
        when(repositorioPublicaciones.guardar(entrada)).thenReturn(guardada);
 
        Publicacion resultado = publicacionService.registrar(entrada);
 
        assertThat(resultado.getId()).isEqualTo("abc123");
        verify(publicadorEventos).publicacionRegistrada(guardada);
    }
 
    @Test
    void registrarNoPublicaEventoSiElInvestigadorNoExiste() {
        when(verificadorInvestigador.existe("nadie@uptc.edu.co")).thenReturn(false);
        Publicacion entrada = publicacion(null, "nadie@uptc.edu.co");
 
        assertThatThrownBy(() -> publicacionService.registrar(entrada))
                .isInstanceOf(RecursoNoEncontradoException.class);
 
        verify(repositorioPublicaciones, never()).guardar(any());
        verify(publicadorEventos, never()).publicacionRegistrada(any());
    }
}
