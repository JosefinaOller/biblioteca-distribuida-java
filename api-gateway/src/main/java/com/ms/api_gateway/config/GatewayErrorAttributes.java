package com.ms.api_gateway.config;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import java.util.Map;

@Component
public class GatewayErrorAttributes extends DefaultErrorAttributes {

    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {
        Map<String, Object> errorAttributes = super.getErrorAttributes(request, options);

        errorAttributes.put("mensaje", "El microservicio solicitado no está disponible.");
        errorAttributes.put("api", "API Gateway Biblioteca");
        errorAttributes.remove("requestId");

        return errorAttributes;
    }

}
