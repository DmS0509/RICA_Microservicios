package com.rica.api.investigadores_service.investigadores.infraestructura.salida.mensajeria;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long>{

     List<OutboxEvent> findTop50ByPublicadoFalseOrderByIdAsc();
}
