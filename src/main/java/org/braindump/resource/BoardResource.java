package org.braindump.resource;

import java.util.List;
import java.util.UUID;

import org.braindump.model.BoardEntity;
import org.braindump.model.ParticipantEntity;
import org.braindump.service.BoardService;
import org.braindump.service.ParticipantService;

import jakarta.ws.rs.Produces;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.Path;

@Path("/boards")

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BoardResource {

    @Inject
    ParticipantService participantService;
    @Inject
    BoardService boardService;

    @GET
    public List<BoardEntity> getAllBoards() {
        return boardService.getAllBoards();
    }

    @GET
    @Path("/{id}")
    public BoardEntity getBoardById(@PathParam("id") UUID id) {
        return boardService.getBoardById(id);
    }

    @POST
    @Path("/create")
    public Response createBoard(@Valid BoardEntity board) {
        BoardEntity created = boardService.createBoard(board);

        return Response.status(Response.Status.CREATED)
                .entity(created)
                .build();
    }

    @POST
    @Path("/{boardId}/participants")
    public Response addParticipant(@PathParam("boardId") UUID boardId, @Valid ParticipantEntity participant) {
        ParticipantEntity createdParticipant = participantService.joinBoard(
                participant.getDisplayName(), boardId);

        return Response.status(Response.Status.CREATED)
                .entity(createdParticipant)
                .build();
    }

}
