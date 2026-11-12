package com.rica.api.consultas_service.consultas;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.MongoTemplate;

public class RegistroEventosProcesadosTest {

    private final MongoTemplate mongoTemplate = mock(MongoTemplate.class);
    private final RegistroEventosProcesados registro = new RegistroEventosProcesados(mongoTemplate);

    @Test
    void laPrimeraVezDevuelveTrue() {
        assertThat(registro.primeraVez("investigadores.registrados-0-5")).isTrue();
    }

    @Test
    void laSegundaVezDevuelveFalseCuandoLaBaseRechazaElIdDuplicado() {
        doThrow(new DuplicateKeyException("duplicado")).when(mongoTemplate).insert(any(EventoProcesado.class));

        assertThat(registro.primeraVez("investigadores.registrados-0-5")).isFalse();
    }

}
