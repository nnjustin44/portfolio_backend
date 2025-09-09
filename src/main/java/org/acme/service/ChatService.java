package org.acme.service;

import org.acme.dto.request.ChatRequest;
import org.acme.dto.response.ChatResponse;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class ChatService {

    @ConfigProperty(name = "openai.api.key")
    String openAiApiKey;

    @ConfigProperty(name = "openai.api.url", defaultValue = "https://api.openai.com/v1/chat/completions")
    String openAiApiUrl;

    private final Client client;
    private final ObjectMapper objectMapper;

    public ChatService() {
        this.client = ClientBuilder.newBuilder().build();
        this.objectMapper = new ObjectMapper();
    }

    public ChatResponse initializeChat(String message) {
        try {
            Map<String, Object> requestPayload = new HashMap<>();
            requestPayload.put("model", "gpt-3.5-turbo");
            requestPayload.put("messages", java.util.Arrays.asList(
                    new HashMap<String, String>() {
                        {
                            put("role", "user");
                            put("content", message);
                        }
                    }));
            requestPayload.put("max_tokens", 150);
            requestPayload.put("temperature", 0.7);

            String jsonPayload = objectMapper.writeValueAsString(requestPayload);

            Response response = client.target(openAiApiUrl)
                    .request(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + openAiApiKey)
                    .header("Content-Type", MediaType.APPLICATION_JSON)
                    .post(Entity.entity(jsonPayload, MediaType.APPLICATION_JSON));

            if (response.getStatus() == 200) {

                String responseBody = response.readEntity(String.class);
                ObjectNode responseNode = (ObjectNode) objectMapper.readTree(responseBody);

                String aiResponse = responseNode.get("choices")
                        .get(0)
                        .get("message")
                        .get("content")
                        .asText();

                ChatResponse chatResponse = new ChatResponse();
                chatResponse.setResponse(aiResponse);
                chatResponse.setMessage(message);
                chatResponse.setTimestamp(System.currentTimeMillis());

                return chatResponse;
            } else {
                String errorBody = response.readEntity(String.class);
                System.err.println("OpenAI API Error: " + response.getStatus() + " - " + errorBody);
                return null;
            }

        } catch (Exception e) {
            System.err.println("Error calling OpenAI API: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}