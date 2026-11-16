package org.acme.service;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class ChatServiceTest {
    private HttpServer server;
    private ChatService service;
    private int status = 200;
    private String body = "{\"choices\":[{\"message\":{\"content\":\"Hello from Jonah\"}}]}";

    @BeforeEach
    void start() throws Exception {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/chat", exchange -> {
            assertEquals("Bearer test-key", exchange.getRequestHeaders().getFirst("Authorization"));
            String request = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            assertTrue(request.contains("test-model"));
            byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(status, bytes.length);
            try (var output = exchange.getResponseBody()) { output.write(bytes); }
        });
        server.start();
        service = new ChatService();
        service.openAiApiKey = "test-key";
        service.openAiModel = "test-model";
        service.openAiApiUrl = "http://127.0.0.1:" + server.getAddress().getPort() + "/chat";
    }

    @AfterEach
    void stop() {
        if (service != null) service.close();
        if (server != null) server.stop(0);
    }

    @Test
    void returnsChatResponse() {
        var response = service.initializeChat("Hello");
        assertEquals("Hello from Jonah", response.getResponse());
        assertEquals("Hello", response.getMessage());
    }

    @Test
    void identifiesInvalidKey() {
        status = 401;
        body = "{\"error\":{\"code\":\"invalid_api_key\"}}";
        assertFailure(502, "ai_authentication_failed");
    }

    @Test
    void distinguishesQuotaFromRateLimit() {
        status = 429;
        body = "{\"error\":{\"code\":\"insufficient_quota\"}}";
        assertFailure(503, "ai_quota_exceeded");
        body = "{\"error\":{\"code\":\"rate_limit_exceeded\"}}";
        assertFailure(503, "ai_rate_limited");
    }

    @Test
    void identifiesUnavailableModel() {
        status = 404;
        body = "{\"error\":{\"code\":\"model_not_found\"}}";
        assertFailure(502, "ai_model_unavailable");
    }

    @Test
    void handlesNonJsonProviderErrors() {
        status = 502;
        body = "Bad Gateway";
        assertFailure(502, "ai_request_failed");
    }

    @Test
    void rejectsMalformedSuccess() {
        body = "{\"choices\":[]}";
        assertFailure(502, "ai_invalid_response");
    }

    private void assertFailure(int expectedStatus, String expectedCode) {
        var error = assertThrows(AiServiceException.class, () -> service.initializeChat("Hello"));
        assertEquals(expectedStatus, error.getStatus());
        assertEquals(expectedCode, error.getCode());
    }
}
