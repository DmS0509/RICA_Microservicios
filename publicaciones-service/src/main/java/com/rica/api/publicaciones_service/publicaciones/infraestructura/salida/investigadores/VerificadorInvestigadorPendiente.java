package com.rica.api.publicaciones_service.publicaciones.infraestructura.salida.investigadores;

import org.springframework.stereotype.Component;

import com.rica.api.publicaciones_service.publicaciones.aplicacion.VerificadorInvestigador;

@Component
public class VerificadorInvestigadorPendiente implements VerificadorInvestigador {

    @Override
    public boolean existe(String correoInstitucional) {
        return true;
    }
}
