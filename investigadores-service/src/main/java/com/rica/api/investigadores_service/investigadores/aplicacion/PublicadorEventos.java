package com.rica.api.investigadores_service.investigadores.aplicacion;

import java.util.Map;

public interface PublicadorEventos {

    void publicar(String topico, String clave, Map<String, Object> contenido);
}
