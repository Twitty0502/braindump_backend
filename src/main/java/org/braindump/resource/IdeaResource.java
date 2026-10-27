package org.braindump.resource;

import java.util.List;
import java.util.UUID;

import org.braindump.model.IdeaEntity;
import org.braindump.service.IdeaService;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/ideas")

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class IdeaResource {

    @Inject
    IdeaService ideaService;

    // Endpoint för att hämta alla idéer oavsett vilken board de tillhör
    @GET
    public List<IdeaEntity> getAllIdeas() {
        return ideaService.listAll();
    }

    // Endpoint för att hämta alla idéer utifrån en specifik board
    @GET
    @Path("/board/{boardId}")
    public List<IdeaEntity> getIdeasByBoard(@PathParam("boardId") UUID boardId) {

        return ideaService.listByBoard(boardId);
    }

    // Endpoint för att skapa en ny idé
    @POST
    @Path("/create")
    public Response createIdea(@Valid IdeaEntity idea) {
        IdeaEntity createdIdea = ideaService.createIdea(
                idea.getTitle(),
                idea.getDescription(),
                idea.getBoard().getId(),
                idea.getParticipant().getId());

        return Response.status(Response.Status.CREATED)
                .entity(createdIdea)
                .build();
    }

    // Endpoint för att radera en specifik idé baserat på dess id
    @DELETE
    @Path("/{id}")
    public Response deleteIdea(@PathParam("id") Long id) {
        ideaService.deleteIdea(id);
        return Response.noContent().build();
    }

    // Endpoint för att rensa alla idéer kopplade till en specifik board
    @DELETE
    @Path("/board/{boardId}")
    public Response clearBoard(@PathParam("boardId") UUID boardId) {
        ideaService.clearBoard(boardId);
        return Response.noContent().build();
    }
}