package org.acme.service;

import org.acme.helper.JonahPrompt;
import org.acme.dto.response.ChatResponse;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.annotation.PreDestroy;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import org.jboss.logging.Logger;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
public class ChatService {
    private static final Logger LOG = Logger.getLogger(ChatService.class);

    @ConfigProperty(name = "openai.api.key")
    String openAiApiKey;

    @ConfigProperty(name = "openai.api.url", defaultValue = "https://api.openai.com/v1/chat/completions")
    String openAiApiUrl;

    @ConfigProperty(name = "openai.model", defaultValue = "gpt-4.1-mini")
    String openAiModel;

    private final Client client;
    private final ObjectMapper objectMapper;

    public ChatService() {
        this.client = ClientBuilder.newBuilder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(45, TimeUnit.SECONDS).build();
        this.objectMapper = new ObjectMapper();
    }

    public ChatResponse initializeChat(String message) {
        try {
            Map<String, String> systemMessage = new HashMap<>();
            systemMessage.put("role", "system");
            systemMessage.put("content", JonahPrompt.trainingPrompt);

            Map<String, String> userMessage = new HashMap<>();
            userMessage.put("role", "user");
            userMessage.put("content", message);

            Map<String, Object> requestPayload = new HashMap<>();
            requestPayload.put("model", openAiModel);
            requestPayload.put("messages", Arrays.asList(systemMessage, userMessage));
            requestPayload.put("max_tokens", 100);
            requestPayload.put("temperature", 0.7);

            String jsonPayload = objectMapper.writeValueAsString(requestPayload);

            try (Response response = client.target(openAiApiUrl)
                    .request(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + openAiApiKey)
                    .header("Content-Type", MediaType.APPLICATION_JSON)
                    .post(Entity.entity(jsonPayload, MediaType.APPLICATION_JSON))) {

                if (response.getStatus() == 200) {
                    String responseBody = response.readEntity(String.class);
                    JsonNode responseNode = objectMapper.readTree(responseBody);

                    JsonNode content = responseNode.path("choices").path(0).path("message").path("content");
                    if (!content.isTextual() || content.asText().isBlank()) {
                        throw new AiServiceException(502, "ai_invalid_response", "The AI provider returned an empty or invalid response.");
                    }
                    String aiResponse = content.asText();

                    ChatResponse chatResponse = new ChatResponse();
                    chatResponse.setResponse(aiResponse);
                    chatResponse.setMessage(message);
                    chatResponse.setTimestamp(System.currentTimeMillis());

                    return chatResponse;
                } else {
                    String errorBody = response.readEntity(String.class);
                    LOG.errorf("OpenAI request failed: status=%d, requestId=%s", response.getStatus(),
                            response.getHeaderString("x-request-id"));
                    String providerCode = "";
                    try {
                        providerCode = objectMapper.readTree(errorBody).path("error").path("code").asText("");
                    } catch (Exception ignored) {
                        // Proxies can return a non-JSON error body.
                    }
                    throw AiServiceException.upstream(response.getStatus(), providerCode);
                }
            }
        } catch (AiServiceException e) {
            throw e;
        } catch (ProcessingException e) {
            LOG.errorf("OpenAI connection failed: %s", e.getClass().getSimpleName());
            throw new AiServiceException(502, "ai_connection_failed",
                    "The backend could not connect to the AI provider. Check backend network access and OPENAI_API_URL.");
        } catch (Exception e) {
            LOG.errorf("OpenAI response processing failed: %s", e.getClass().getSimpleName());
            throw new AiServiceException(502, "ai_invalid_response", "The AI provider returned an invalid response.");
        }
    }

    @PreDestroy
    void close() {
        client.close();
    }
}
