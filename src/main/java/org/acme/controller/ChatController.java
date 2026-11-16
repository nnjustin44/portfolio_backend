package org.acme.controller;

import org.acme.service.ChatService;
import org.acme.service.AiServiceException;
import java.util.Map;
import org.acme.dto.request.ChatRequest;
import org.acme.dto.response.ChatResponse;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api")
public class ChatController {

    @Inject
    ChatService chatService;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "                                                                          \n" + //
                "                       Hello Quakrus REST!                          \n" + //
                "                                                                          \n" + //
                "⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⡆⠀⠀⠀⠀⠀⠀⠀⠀⠀⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀\n" + //
                "⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣼⡇⠀⠀⠀⠀⠀⠀⠀⢀⠀⠀⠀⣰⠂⠀⠀⣄⣰⣿⠁⠀⠀⠀⢀⣴⠀⢀⣤⡞⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀\n" + //
                "⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣀⣷⡟⠀⠀⠀⠀⠀⡄⠀⢠⣾⡄⢸⣾⣿⡴⢠⣴⣿⣿⣿⡦⠀⣰⢠⣾⣿⣶⣿⡟⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀\n" + //
                "⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣆⣸⣿⡿⠁⠀⠀⡄⢠⣼⣿⣤⣿⣿⣷⣾⣿⣿⣧⣼⣿⣿⣿⣿⠀⢰⣿⣿⣿⣿⣿⣿⣁⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀\n" + //
                "⠀⠀⠀⠀⠀⠀⠀⠀⢰⣤⣿⣿⡟⠀⠀⣀⣸⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⠟⠀⠀⣠⠀⢀⣤⡾⢀⣀⣀⣤⣶⡾⠋⠀⠀⠀\n" + //
                "⠀⠀⠀⠀⠀⢀⠀⣷⣾⣿⣿⠟⠀⠀⢠⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⠁⢀⣴⣿⣿⣶⣿⣿⣿⣿⣿⣿⡿⠋⠀⠀⠀⠀⠀\n" + //
                "⠀⠀⠀⠀⠀⢸⣶⣿⣿⣿⠏⠀⠀⢀⣾⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣟⠋⠀⠀⠀⠀⠀⠀⠀\n" + //
                "⠀⠀⠀⠀⣷⣾⣿⣿⣿⠇⠀⠀⠀⢼⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⠿⠛⠁⠀⠀⠀⠀⠀\n" + //
                "⠀⠀⢀⠈⣿⣿⣿⣿⡟⠀⠀⠀⠀⣾⣿⣿⣿⣿⣿⣿⣿⣿⠿⠿⠿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣏⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀\n" + //
                "⠀⠀⣸⣿⣿⣿⣿⣿⠁⠀⠀⠀⠀⢻⣿⣿⣿⡋⢹⣿⡟⠁⠀⠀⠀⠀⠙⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⠋⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀\n" + //
                "⠀⠀⢸⣿⣿⣿⣿⡏⠀⠀⠀⠀⠀⢸⣿⣿⣏⡇⣿⣿⠀⠀⠀⠀⠀⠀⠀⢹⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣦⣤⣶⣾⣇⣠⣤⡴⠂⠀⠀⠀\n" + //
                "⠘⣾⣿⣿⣿⣿⣿⡇⠀⠀⠀⠀⠀⠘⠻⢟⢀⡧⢹⡟⠀⠀⠀⠀⠀⠀⠀⠀⠹⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣶⣦⣤⣤⡄\n" + //
                "⠀⢿⣿⣿⣿⣿⣿⠁⠀⠀⠀⠀⠀⠀⠀⡇⣿⣿⢼⠀⠀⠀⠀⠀⠀⠀⠀⢀⣴⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡿⠟⠛⠉⠁\n" + //
                "⠀⢸⣿⣿⣿⣿⣿⡀⠀⠀⠀⠀⠀⠀⡰⠁⣿⡾⣸⠀⠀⣤⣶⣶⣶⣶⣾⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⠿⠿⠟⠒⠀⠀⠀\n" + //
                "⢰⣿⣿⣿⣿⣿⣿⡇⠀⠀⠀⠀⠀⠀⣇⢎⡻⣟⢹⡀⠀⢀⣼⣿⣿⣿⣿⡿⠿⠛⠋⠉⢸⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⠟⠛⠓⠀⠀⠀⠀⠀⠀\n" + //
                "⢸⣿⣿⣿⣿⣿⣿⡇⠀⠀⠀⠀⠀⡜⡇⣨⡟⡯⢸⠗⣴⠿⢫⣿⢿⣿⠏⠀⠀⠀⠀⠀⢈⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣀⣀⡀⠀⠀⠀⠀⠀⠀\n" + //
                "⠀⣹⣿⣿⣿⣿⣿⣿⠀⠀⠀⠀⢀⡟⣠⣏⡅⡇⣏⡏⣇⢠⠟⠁⣿⠃⠀⠀⠀⠀⠀⣴⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣤⣤⡀⠀⠀⠀⠀\n" + //
                "⢰⣿⣿⣿⣿⣿⣿⣿⣇⠀⠀⠀⡏⡕⡟⠀⢕⡅⡇⢓⠘⡂⠀⠀⠁⠀⠀⠀⠀⢠⣾⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣯⣀⠀⠀⠀⠀\n" + //
                "⠀⢹⣿⣿⣿⣿⣿⣿⣿⣦⠀⡴⠃⡇⡟⡀⣻⢅⠱⡸⡧⣣⠀⠀⠀⠀⠀⠀⠀⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⠿⢿⣟⠛⠛⠛⠳⠄⠀⠀\n" + //
                "⠀⠀⠙⣿⣿⣿⣿⣿⣿⣿⣷⡀⢠⡇⡇⣗⢸⢨⠀⢧⡿⣡⢗⡄⠀⠀⠀⠀⢸⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡇⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀\n" + //
                "⠀⢠⣴⣿⣿⣿⣿⣿⣿⣿⣿⣿⣾⡁⠇⢻⠈⣾⠀⠼⡇⣯⡀⢛⡈⠀⠀⠀⠸⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡷⠂⠀⠀⠀⠀⠀⠀⠀⠀\n" + //
                "⠀⠀⠈⢻⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣷⣼⡀⡿⢇⠀⡇⠻⡇⠈⡇⠀⠀⠀⠀⢿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣷⡀⠀⠀⠀⠀⠀⠀⠀⠀\n" + //
                "⠀⠀⠀⠀⠀⢈⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣷⣦⣧⣃⣀⣸⣄⣁⣀⣤⣴⣾⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡏⠀⠈⠙⠈⠙⠂⠀⠀⠀⠀⠀⠀⠀\n" + //
                "⠀⠀⠀⠀⠀⠚⠿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡄⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀\n" + //
                "⠀⠀⠀⠀⠀⠀⠀⠈⠉⢙⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡿⣿⣿⣿⡇⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀\n" + //
                "⠀⠀⠀⠀⠀⠀⠀⠀⠀⠺⠿⢿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣏⠀⠈⠀⠙⠇⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀\n" + //
                "⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠁⠈⠿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⠿⣿⣿⡿⠉⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀\n" + //
                "⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⠛⠉⠟⠉⢛⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡿⠿⣿⣿⣿⡿⠇⠀⠁⠙⠇⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀\n" + //
                "⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠿⠛⠙⠟⠀⠐⢿⣿⠟⠿⠀⠈⠉⣿⠏⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀\n" + //
                "⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀";
    }

    @POST
    @Path("/chat")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response openAiResponse(ChatRequest request) {
        try {
            if (request == null || request.getMessage() == null || request.getMessage().trim().isEmpty()) {
                return Response.status(400)
                        .entity(Map.of("error", "Input message required", "code", "message_required"))
                        .build();
            }

            ChatResponse response = chatService.initializeChat(request.getMessage().trim());

            if (response == null || response.getResponse() == null) {
                return Response.status(500)
                        .entity("Error: Failed to get response from AI service")
                        .build();
            }

            return Response.ok(response).build();

        } catch (AiServiceException e) {
            return Response.status(e.getStatus())
                    .entity(Map.of("error", e.getMessage(), "code", e.getCode()))
                    .build();
        } catch (Exception e) {
            return Response.status(500)
                    .entity(Map.of("error", "Unexpected backend error", "code", "internal_error"))
                    .build();
        }
    }
}
