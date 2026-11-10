package com.rica.api.publicaciones_service.publicaciones.aplicacion;

import org.springframework.stereotype.Component;

import com.rica.api.publicaciones_service.grpc.InvestigadorGrpcGrpc.InvestigadorGrpcBlockingStub;
import com.rica.api.publicaciones_service.grpc.InvestigadorRequest;

@Component
public class VerificadorInvestigadorGrpc implements VerificadorInvestigador {

    private final InvestigadorGrpcBlockingStub stub;

    public VerificadorInvestigadorGrpc(InvestigadorGrpcBlockingStub stub) {
        this.stub = stub;
    }

    @Override
    public boolean existe(String correoInstitucional) {
        var request = InvestigadorRequest.newBuilder()
                .setCorreoInstitucional(correoInstitucional)
                .build();
        return stub.existeInvestigador(request).getExiste();
    }
}
