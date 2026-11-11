package com.rica.api.publicaciones_service.publicaciones.aplicacion;

import java.util.List;

import org.springframework.stereotype.Service;

import com.rica.api.publicaciones_service.compartido.RecursoNoEncontradoException;
import com.rica.api.publicaciones_service.publicaciones.dominio.Publicacion;

@Service
public class PublicacionService implements PublicacionUseCase {

    private final RepositorioPublicaciones repositorioPublicaciones;
    private final VerificadorInvestigador verificadorInvestigador;
    private final PublicadorEventosPublicaciones publicadorEventos;

    public PublicacionService(RepositorioPublicaciones repositorioPublicaciones,
            VerificadorInvestigador verificadorInvestigador,
            PublicadorEventosPublicaciones publicadorEventos) {
        this.repositorioPublicaciones = repositorioPublicaciones;
        this.verificadorInvestigador = verificadorInvestigador;
        this.publicadorEventos = publicadorEventos;
    }

    @Override
    public Publicacion registrar(Publicacion publicacion) {
        if (!verificadorInvestigador.existe(publicacion.getInvestigadorCorreo())) {
            throw new RecursoNoEncontradoException(
                    "No existe un investigador con correo " + publicacion.getInvestigadorCorreo());
        }
        Publicacion guardada = repositorioPublicaciones.guardar(publicacion);
        // Solo después de guardar con éxito: si el comando falla, no se anuncia nada.
        publicadorEventos.publicacionRegistrada(guardada);
        return guardada;
    }

    @Override
    public List<Publicacion> listarPorInvestigador(String investigadorCorreo) {
        return repositorioPublicaciones.listarPorInvestigador(investigadorCorreo);
    }

    @Override
    public Publicacion buscarPorId(String id) {
        return repositorioPublicaciones.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontró la publicación con id: " + id));
    }
}
