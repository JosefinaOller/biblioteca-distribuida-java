package com.ms.prestamos.comunication;

import com.ms.prestamos.dto.PrestamoDTO;
import com.ms.prestamos.exception.RecursoInvalidoException;
import com.ms.prestamos.service.IPrestamoService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.Header;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.DynamicPropertyRegistry;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.mockserver.model.JsonBody.json;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "eureka.client.enabled=false",
                "spring.cloud.discovery.enabled=false",
                "spring.cloud.loadbalancer.enabled=false"
        }
)
class PrestamoComunicacionTest {

    private static ClientAndServer mockServer;

    @Autowired
    private IPrestamoService service;

    @BeforeAll
    static void startServer() {
        mockServer = startClientAndServer(1080);
    }

    @AfterAll
    static void stopServer() {
        mockServer.stop();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("usuarios.api.url", () -> "http://localhost:1080");
        registry.add("libros.api.url", () -> "http://localhost:1080");
    }

    @Test
    @DisplayName("MockServer: Validar creación de préstamo con respuesta exitosa (200 OK)")
    void testCommunicationSuccess() {
        mockServer.when(
                request()
                        .withMethod("GET")
                        .withPath("/api/usuarios/1")
        ).respond(
                response()
                        .withStatusCode(200)
                        .withHeader(new Header("Content-Type", "application/json"))
                        .withBody(json("{\"id\": 1, \"nombreCompleto\": \"Juan Perez\", \"email\": \"juan@mail.com\", \"isActivo\": true}"))
        );

        mockServer.when(
                request()
                        .withMethod("GET")
                        .withPath("/api/libros/10")
        ).respond(
                response()
                        .withStatusCode(200)
                        .withBody(json("{\"id\": 10, \"titulo\": \"Libro Test\", \"autor\": \"Autor\", \"isbn\": \"123456789\", \"ejemplaresDisponibles\": 5}"))
        );

        mockServer.when(
                request()
                        .withMethod("PUT")
                        .withPath("/api/libros/10")
        ).respond(
                response().withStatusCode(200)
        );

        PrestamoDTO dto = new PrestamoDTO();
        dto.setIdUsuario(1L);
        dto.setIdLibro(10L);

        assertDoesNotThrow(() -> service.save(dto));
    }

    @Test
    @DisplayName("MockServer: Validar fallo cuando el usuario no está activo (409 Conflict)")
    void testUserInactiveCommunication() {
        mockServer.when(
                request()
                        .withMethod("GET")
                        .withPath("/api/usuarios/2")
        ).respond(
                response()
                        .withStatusCode(200)
                        .withBody(json("{\"id\": 2, \"nombreCompleto\": \"Usuario Inactivo\", \"isActivo\": false}"))
                        .withDelay(TimeUnit.MILLISECONDS, 100)
        );

        PrestamoDTO dto = new PrestamoDTO();
        dto.setIdUsuario(2L);
        dto.setIdLibro(10L);

        assertThrows(RecursoInvalidoException.class, () -> service.save(dto));
    }
}